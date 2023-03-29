package rudynakodach.github.io.webhookintegrations;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import rudynakodach.github.io.webhookintegrations.Commands.*;
import rudynakodach.github.io.webhookintegrations.Events.*;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.logging.Level;

public final class WebhookIntegrations extends JavaPlugin {
    public static boolean isLatest = true;
    public static int currentBuildNumber = 28;
    public static String localeLang;
    public static FileConfiguration lang;

    //on startup
    @Override
    public void onEnable() {
        getLogger().log(Level.INFO, "Hello, World!");

        if(!new File(getDataFolder(), "lang.yml").exists()) {
            this.saveResource("lang.yml",false);
        }
        if(!new File(getDataFolder(), "advancements.yml").exists()) {
            this.saveResource("advancements.yml", false);
        }

        File langFile = new File(this.getDataFolder(),"lang.yml");
        WebhookIntegrations.lang = YamlConfiguration.loadConfiguration(langFile);
        getLogger().log(Level.INFO,"Initializing language...");
        saveDefaultConfig();

        Locale locale = Locale.getDefault();
        localeLang = locale.toString();

        if(!lang.contains(localeLang)) {
            localeLang = "en_US";
        }

        getLogger().log(Level.INFO,"Hooked to " + localeLang);

        if(getConfig().getBoolean("check-for-updates")) {
            getLogger().log(Level.INFO, lang.getString(localeLang + ".update.checking"));

            try {
                int receivedBuildNumber = new AutoUpdater(this).getLatestVersion();
                if (currentBuildNumber < receivedBuildNumber && receivedBuildNumber != -1) {
                    isLatest = false;
                    getLogger().log(Level.WARNING, "Current: " + currentBuildNumber + " | New: " + receivedBuildNumber);
                    getLogger().log(Level.WARNING, "------------------------- WI -------------------------");
                    getLogger().log(Level.INFO, lang.getString(localeLang + ".update.updateFound"));
                    getLogger().log(Level.WARNING, "------------------------------------------------------");

                    if (getConfig().getBoolean("auto-update")) {
                        isLatest = new AutoUpdater(this).Update();
                    }

                } else {
                    getLogger().log(Level.INFO, lang.getString(localeLang + ".update.latest"));
                }
            } catch (IOException e) {
                getLogger().log(Level.WARNING, lang.getString(localeLang + ".update.checkFailed") + e.getMessage());
            }
        }

        this.saveDefaultConfig();

        if (Objects.equals(Objects.requireNonNull(getConfig().getString("webhookUrl")).trim(), "")) {
            getLogger().log(Level.WARNING, lang.getString(localeLang + ".onStart.webhookEmpty"));
        }

        getLogger().log(Level.INFO, lang.getString(localeLang + ".onStart.registeringEvents"));

        onPlayerChat chatEvent = new onPlayerChat(this);
        getServer().getPluginManager().registerEvents(chatEvent, this);

        onPlayerJoin onPlayerJoinEvent = new onPlayerJoin(this);
        getServer().getPluginManager().registerEvents(onPlayerJoinEvent, this);

        onPlayerQuit playerQuitEvent = new onPlayerQuit(this);
        getServer().getPluginManager().registerEvents(playerQuitEvent, this);

        onPlayerKick playerKick = new onPlayerKick(this);
        getServer().getPluginManager().registerEvents(playerKick, this);

        onPlayerAdvancementCompleted onPlayerAdvancement = new onPlayerAdvancementCompleted(this);
        getServer().getPluginManager().registerEvents(onPlayerAdvancement, this);

        onPlayerDeath playerDeath = new onPlayerDeath(this);
        getServer().getPluginManager().registerEvents(playerDeath,this);

        getLogger().log(Level.INFO, lang.getString(localeLang + ".onStart.eventRegisterFinish"));

        SetWebhookURL setWebhookUrlCommand = new SetWebhookURL(getConfig(), this);
        Objects.requireNonNull(getCommand("setUrl")).setExecutor(setWebhookUrlCommand);

        SendToWebhook sendToWebhookCommand = new SendToWebhook(getConfig(), this, getLogger());
        Objects.requireNonNull(getCommand("send")).setExecutor(sendToWebhookCommand);

        WIActions resetConfig = new WIActions(this);
        Objects.requireNonNull(getCommand("wi")).setExecutor(resetConfig);

        getLogger().log(Level.INFO, lang.getString(localeLang + ".onStart.commandRegisterFinish"));

        if(getConfig().getBoolean("onServerStart.announce")) {
            String json = getConfig().getString("onServerStart.messageJson");

            String serverIp = getServer().getIp();
            int slots = getServer().getMaxPlayers();
            String serverMotd = getServer().getMotd();
            String serverName = getServer().getName();
            String serverVersion = getServer().getVersion();
            Boolean isOnlineMode = getServer().getOnlineMode();
            int playersOnline = getServer().getOnlinePlayers().size();

            json = json.replace("%time%", new SimpleDateFormat("HH:mm:ss").format(new Date()));
            json = json.replace("%serverIp%", serverIp);
            json = json.replace("%maxPlayers%", String.valueOf(slots));
            json = json.replace("%serverMotd%", serverMotd);
            json = json.replace("%serverName%", serverName);
            json = json.replace("%serverVersion%", serverVersion);
            json = json.replace("%isOnlineMode%", String.valueOf(isOnlineMode));
            json = json.replace("%playersOnline%", String.valueOf(playersOnline));

            new WebhookActions(this).SendSync(json);
        }
    }

    //on shutdown
    @Override
    public void onDisable() {
        if (getConfig().getBoolean("onServerStop.announce")) {
            if (!getConfig().getString("webhookUrl").trim().equals("")) {
                String serverIp = getServer().getIp();
                int slots = getServer().getMaxPlayers();
                String serverMotd = getServer().getMotd();
                String serverName = getServer().getName();
                String serverVersion = getServer().getVersion();
                Boolean isOnlineMode = getServer().getOnlineMode();
                int playersOnline = getServer().getOnlinePlayers().size();

                String json = getConfig().getString("onServerStop.messageJson");

                json = json.replace("%time%", new SimpleDateFormat("HH:mm:ss").format(new Date()));
                json = json.replace("%serverIp%", serverIp);
                json = json.replace("%maxPlayers%", String.valueOf(slots));
                json = json.replace("%serverMotd%", serverMotd);
                json = json.replace("%serverName%", serverName);
                json = json.replace("%serverVersion%", serverVersion);
                json = json.replace("%isOnlineMode%", String.valueOf(isOnlineMode));
                json = json.replace("%playersOnline%", String.valueOf(playersOnline));

                new WebhookActions(this).SendSync(json);
            }
        }

        getLogger().log(Level.INFO, "this is my final message");
        getLogger().log(Level.INFO, "goodb ye");
    }
}