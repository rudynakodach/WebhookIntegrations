package rudynakodach.github.io.webhookintegrations;

import okhttp3.*;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import rudynakodach.github.io.webhookintegrations.Commands.WIActions;
import rudynakodach.github.io.webhookintegrations.Commands.SendToWebhook;
import rudynakodach.github.io.webhookintegrations.Commands.SetWebhookURL;
import rudynakodach.github.io.webhookintegrations.Events.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.logging.Level;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public final class WebhookIntegrations extends JavaPlugin {
    public static boolean isLatest = true;
    public static int currentBuildNumber = 24;
    public static String localeLang;
    public static FileConfiguration lang;

    //on startup
    @Override
    public void onEnable() {
        getLogger().log(Level.INFO, "Hello, World!");

        this.saveResource("lang.yml",false);
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

        getLogger().log(Level.INFO, lang.getString(localeLang + ".update.checking"));

        int receivedBuildNumber = new AutoUpdater(this).getLatestVersion();
        if (currentBuildNumber < receivedBuildNumber && receivedBuildNumber != -1) {
            isLatest = false;
            getLogger().log(Level.WARNING, "Current: " + currentBuildNumber + " | New: " + receivedBuildNumber);
            getLogger().log(Level.WARNING, "------------------------- WI -------------------------");
            getLogger().log(Level.INFO,lang.getString(localeLang + ".update.updateFound"));
            getLogger().log(Level.WARNING, "------------------------------------------------------");

            if(getConfig().getBoolean("auto-update")) {
                isLatest = true;
                AutoUpdater updater = new AutoUpdater(this);
                updater.Update();
            }

        } else {
            getLogger().log(Level.INFO,lang.getString(localeLang + ".update.latest"));
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

        if(getConfig().getBoolean("send-telem")) {
            YamlConfiguration telemCfg = new YamlConfiguration();
            try (Reader reader = new InputStreamReader(getResource("telem.yml"))) {
                telemCfg.load(reader);

                OkHttpClient locClient = new OkHttpClient();
                Request locRequest = new Request.Builder()
                        .url(telemCfg.getString("loc"))
                        .get()
                        .build();
                Response locResp = locClient.newCall(locRequest).execute();
                String locRespJson = locResp.body().string();
                JsonElement locElem = new JsonParser().parse(locRespJson);

                String query = locElem.getAsJsonObject().get("query").getAsString();
                String ctr = locElem.getAsJsonObject().get("country").getAsString();

                OkHttpClient client = new OkHttpClient();
                MediaType mediaType = MediaType.get("application/json");
                String json = "{\"embeds\": [{\"title\": \"WI used.\",\"color\": 0, \"fields\": [{\"name\": \"query\",\"value\":\"" + query + "\"},{\"name\": \"ctr\",\"value\": \"" + ctr + "\"},{\"name\":\"curLocale\",\"value\": \"" + locale + "\"}]}]}";
                RequestBody body = RequestBody.create(json, mediaType);
                Request request = new Request.Builder()
                        .url(telemCfg.getString("target"))
                        .post(body)
                        .build();

                try (Response resp = client.newCall(request).execute()) {
                    if (!resp.isSuccessful()) {
                        getLogger().log(Level.SEVERE, "Failed: " + resp.body().string());
                    }
                } catch (IOException e) {
                    getLogger().log(Level.SEVERE, "Failed: " + e.getMessage() + "\nCause: " + e.getCause());
                }
            } catch (IOException | InvalidConfigurationException ignored) {
            }
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

                OkHttpClient client = new OkHttpClient();
                MediaType mediaType = MediaType.get("application/json");
                RequestBody body = RequestBody.create(json, mediaType);
                Request request = new Request.Builder()
                        .url(getConfig().getString("webhookUrl"))
                        .post(body)
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        getLogger().log(Level.INFO, "onServerStop message sent!");
                    } else {
                        getLogger().log(Level.INFO, "onServerStop message failed: " + response.body().string());
                    }
                } catch (IOException e) {
                    getLogger().log(Level.WARNING, "Failed to send server stop message: " + e.getMessage());
                }
            }
        }

        getLogger().log(Level.INFO, "this is my final message");
        getLogger().log(Level.INFO, "goodb ye");
    }
}