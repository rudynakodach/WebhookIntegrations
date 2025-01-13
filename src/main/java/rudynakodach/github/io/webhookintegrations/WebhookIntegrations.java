/*
 * WebhookIntegrations
 * Copyright (C) 2023 rudynakodach
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package rudynakodach.github.io.webhookintegrations;

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import rudynakodach.github.io.webhookintegrations.Commands.*;
import rudynakodach.github.io.webhookintegrations.Events.Actions.OpJoinEvent;
import rudynakodach.github.io.webhookintegrations.Events.Game.*;
import rudynakodach.github.io.webhookintegrations.Modules.LanguageConfiguration;
import rudynakodach.github.io.webhookintegrations.Modules.MessageConfiguration;
import rudynakodach.github.io.webhookintegrations.Modules.MessageType;
import rudynakodach.github.io.webhookintegrations.Modules.TemplateConfiguration;
import rudynakodach.github.io.webhookintegrations.Utils.ConfigMigrator;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;
import java.util.logging.Level;

public final class WebhookIntegrations extends JavaPlugin {
    public static boolean isLatest = true;
    public static final int currentBuildNumber = 60;
    public static final int currentConfigVersion = 2;

    @Override
    public void onEnable() {
        // bStats integration
        Metrics metrics = new Metrics(this, 18509);

        saveDefaultConfig();
        getLogger().log(Level.INFO, "Hello, World!");

        if(!new File(getDataFolder(), "messages.yml").exists()) {
            this.saveResource("messages.yml", false);
        }

        if(!new File(getDataFolder(), "lang.yml").exists()) {
            this.saveResource("lang.yml",false);
        }

        if(!new File(getDataFolder(), "templates.yml").exists()) {
            this.saveResource("templates.yml", false);
        }

        if(!new File(getDataFolder(), "config-backups").exists()) {
            boolean result = new File(getDataFolder(), "config-backups").mkdir();
            if(!result) {
                getLogger().log(Level.SEVERE, "Failed to create the config-backups directory.");
            }
        }

        getLogger().log(Level.INFO,"Initializing language...");
        File langFile = new File(this.getDataFolder(),"lang.yml");

        Locale locale = Locale.getDefault();
        String selectedLanguage = locale.toString();

        YamlConfiguration languageConfig = YamlConfiguration.loadConfiguration(langFile);

        if(getConfig().get("language-override") instanceof String languageOverride) {
            if(!languageConfig.contains(selectedLanguage)) {
                getLogger().log(Level.INFO, "Language override is set to a language that doesn't exist. Falling back to en_US. For all available languages see lang.yml.");
                selectedLanguage = "en_US";
            } else {
                getLogger().log(Level.INFO, "Language override set to: " + languageOverride);
                selectedLanguage = languageOverride;
            }
        } else {
            if(!languageConfig.contains(selectedLanguage)) {
                selectedLanguage = "en_US";
            }
            getLogger().log(Level.INFO,"Hooked to " + selectedLanguage);
        }

        LanguageConfiguration language = new LanguageConfiguration(this, selectedLanguage, languageConfig);
        new TemplateConfiguration(this);
        new MessageConfiguration(this);

        int presentConfigVersion = getConfig().getInt("config-version", 1);

        if(currentConfigVersion > presentConfigVersion) {
            ConfigMigrator.migrate(this, presentConfigVersion, currentConfigVersion);
        }

        getLogger().log(Level.INFO, language.getLocalizedString("onStart.message"));

        if(getConfig().getBoolean("check-for-updates")) {
            getLogger().log(Level.INFO, language.getLocalizedString("update.checking"));

            try {
                int receivedBuildNumber = new AutoUpdater(this).getLatestVersion();
                if (currentBuildNumber < receivedBuildNumber && receivedBuildNumber != -1) {
                    isLatest = false;
                    getLogger().log(Level.WARNING, "------------------------- WI -------------------------");
                    getLogger().log(Level.INFO, language.getLocalizedString("update.updateFound"));
                    getLogger().log(Level.WARNING, "------------------------------------------------------");

                    if (getConfig().getBoolean("auto-update")) {
                        isLatest = new AutoUpdater(this).Update();
                    }

                } else {
                    getLogger().log(Level.INFO, language.getLocalizedString("update.latest"));
                }
            } catch (IOException e) {
                getLogger().log(Level.WARNING, language.getLocalizedString("update.checkFailed") + e.getMessage());
            }
        }

        String webhookUrl = getConfig().getString("webhookUrl");

        if (webhookUrl == null) {
            getLogger().log(Level.WARNING, language.getLocalizedString("onStart.webhookEmpty"));
        } else {
            if(webhookUrl.equalsIgnoreCase("")) {
                getLogger().log(Level.WARNING, language.getLocalizedString("onStart.webhookEmpty"));
            }
        }

        getLogger().log(Level.INFO, language.getLocalizedString("onStart.registeringEvents"));


        // Events
        OnServerStart serverStart = new OnServerStart(this);
        getServer().getPluginManager().registerEvents(serverStart, this);

        OnPlayerChat chatEvent = new OnPlayerChat(this);
        getServer().getPluginManager().registerEvents(chatEvent,  this);

        OnPlayerJoin onPlayerJoinEvent = new OnPlayerJoin(this);
        getServer().getPluginManager().registerEvents(onPlayerJoinEvent, this);

        OpJoinEvent opJoinEvent = new OpJoinEvent(this);
        getServer().getPluginManager().registerEvents(opJoinEvent, this);

        OnPlayerQuit playerQuitEvent = new OnPlayerQuit(this);
        getServer().getPluginManager().registerEvents(playerQuitEvent, this);

        OnPlayerKick playerKick = new OnPlayerKick(this);
        getServer().getPluginManager().registerEvents(playerKick, this);

        OnPlayerAdvancementCompleted onPlayerAdvancement = new OnPlayerAdvancementCompleted(this);
        getServer().getPluginManager().registerEvents(onPlayerAdvancement, this);

        OnPlayerDeath playerDeath = new OnPlayerDeath(this);
        getServer().getPluginManager().registerEvents(playerDeath,this);

        getLogger().log(Level.INFO, language.getLocalizedString("onStart.eventRegisterFinish"));

        // Commands
        SetWebhookURL setWebhookUrlCommand = new SetWebhookURL(this);
        Objects.requireNonNull(getCommand("seturl")).setExecutor(setWebhookUrlCommand);

        SendToWebhook sendToWebhookCommand = new SendToWebhook(getConfig(), this);
        Objects.requireNonNull(getCommand("send")).setExecutor(sendToWebhookCommand);

        WIActions resetConfig = new WIActions(this);
        Objects.requireNonNull(getCommand("wi")).setExecutor(resetConfig);

        getLogger().log(Level.INFO, language.getLocalizedString("onStart.commandRegisterFinish"));

        // Metrics
        metrics.addCustomChart(new SimplePie("url_state", () ->
                String.valueOf(!Objects.requireNonNullElse(getConfig().getString("webhookUrl"), "").equalsIgnoreCase(""))));

        metrics.addCustomChart(new SimplePie("lang_used", () -> LanguageConfiguration.get().getLocale()));

        metrics.addCustomChart(new SimplePie("timezone", () -> getConfig().getString("timezone")));

        metrics.addCustomChart(new SimplePie("remove-force-role-pings", () -> String.valueOf(getConfig().getBoolean("remove-force-role-pings"))));
        metrics.addCustomChart(new SimplePie("remove-force-pings", () -> String.valueOf(getConfig().getBoolean("remove-force-pings"))));
        metrics.addCustomChart(new SimplePie("remove-force-channel-pings", () -> String.valueOf(getConfig().getBoolean("remove-force-channel-pings"))));
    }

    @Override
    public void onDisable() {
        if(Bukkit.isStopping()) {
            sendStopMessage();
        }

        getLogger().log(Level.INFO, "this is my final message");
        getLogger().log(Level.INFO, "goodb ye");
    }

    private void sendStopMessage() {
        if(!MessageConfiguration.get().canAnnounce(MessageType.SERVER_STOP)) {
            return;
        }

        String serverIp = getServer().getIp();
        int slots = getServer().getMaxPlayers();
        String serverMotd = PlainTextComponentSerializer.plainText().serialize(getServer().motd());
        String serverName = getServer().getName();
        String serverVersion = getServer().getVersion();
        Boolean isOnlineMode = getServer().getOnlineMode();
        int playersOnline = getServer().getOnlinePlayers().size();

        String json = MessageConfiguration.get().getMessage(MessageType.SERVER_STOP);

        if(json == null) {
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        sdf.setTimeZone(TimeZone.getTimeZone(getConfig().getString("timezone")));

        json = json.replace("$time$", new SimpleDateFormat(
                        Objects.requireNonNullElse(
                                getConfig().getString("date-format"),
                                "")).format(new Date()))
            .replace("$timestamp$", sdf.format(new Date()))
            .replace("$serverIp$", serverIp)
            .replace("$maxPlayers$", String.valueOf(slots))
            .replace("$serverMotd$", serverMotd)
            .replace("$serverName$", serverName)
            .replace("$serverVersion$", serverVersion)
            .replace("$isOnlineMode$", String.valueOf(isOnlineMode))
            .replace("$playersOnline$", String.valueOf(playersOnline));

        new WebhookActions(this, MessageConfiguration.get().getTarget(MessageType.SERVER_STOP)).SendSync(json);
    }
}