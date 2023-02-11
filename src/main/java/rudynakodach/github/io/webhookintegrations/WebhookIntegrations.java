package rudynakodach.github.io.webhookintegrations;

import net.kyori.adventure.text.serializer.plain.PlainComponentSerializer;
import okhttp3.*;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import rudynakodach.github.io.webhookintegrations.Commands.ConfigActions;
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

    public static int currentBuildNumber = 17;
    static String buildNumberUrl = "https://raw.githubusercontent.com/rudynakodach/WebhookIntegrations/master/buildnumber";
    public static String localeLang;
    public static FileConfiguration lang;

    //on startup
    @Override
    public void onEnable() {

        getLogger().log(Level.INFO,"Initializing language...");
        this.saveResource("lang.yml",false);

        Locale locale = Locale.getDefault();
        localeLang = locale.toString();

        if(!localeLang.equals("pl_PL") && !localeLang.equals("en_US")) {
          WebhookIntegrations.localeLang = "en_US";
        } else {
            WebhookIntegrations.localeLang = locale.toString();
        }

        File langFile = new File(this.getDataFolder(),"lang.yml");
        WebhookIntegrations.lang = YamlConfiguration.loadConfiguration(langFile);

        getLogger().log(Level.INFO,"Hooked to " + localeLang);

        getLogger().log(Level.INFO, "Hello, World!");
        getLogger().log(Level.INFO, lang.getString(localeLang + ".update.checking"));

        int receivedBuildNumber = getVersion();
        if (currentBuildNumber < receivedBuildNumber && receivedBuildNumber != -1) {
            getLogger().log(Level.WARNING, "------------------------- WI -------------------------");
            getLogger().log(Level.INFO,lang.getString(localeLang + ".update.updateFound"));
            getLogger().log(Level.WARNING, "------------------------------------------------------");

            if(getConfig().getBoolean("auto-update")) {
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

        onServerStart serverStart = new onServerStart(this);
        getServer().getPluginManager().registerEvents(serverStart, this);

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
        Objects.requireNonNull(getCommand("seturl")).setExecutor(setWebhookUrlCommand);

        SendToWebhook sendToWebhookCommand = new SendToWebhook(getConfig(), this, getLogger());
        Objects.requireNonNull(getCommand("send")).setExecutor(sendToWebhookCommand);

        ConfigActions resetConfig = new ConfigActions(this);
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
        if(Bukkit.isStopping()) {
            if(getConfig().getBoolean("onServerStop.announce")) {
                if(!getConfig().getString("webhookUrl").trim().equals("")) {
                    String serverIp = getServer().getIp();
                    int slots = getServer().getMaxPlayers();
                    String serverMotd = PlainComponentSerializer.plain().serialize(getServer().motd());
                    String serverName = getServer().getName();
                    String serverVersion = getServer().getVersion();
                    Boolean isOnlineMode = getServer().getOnlineMode();
                    int playersOnline = getServer().getOnlinePlayers().size();
                    String minecraftVersion = getServer().getMinecraftVersion();

                    String json = getConfig().getString("onServerStop.messageJson");

                    json = json.replace("%time%", new SimpleDateFormat("HH:mm:ss").format(new Date()));
                    json = json.replace("%serverIp%",serverIp);
                    json = json.replace("%maxPlayers%",String.valueOf(slots));
                    json = json.replace("%serverMotd%",serverMotd);
                    json = json.replace("%serverName%",serverName);
                    json = json.replace("%serverVersion%", serverVersion);
                    json = json.replace("%isOnlineMode%",String.valueOf(isOnlineMode));
                    json = json.replace("%playersOnline%",String.valueOf(playersOnline));
                    json = json.replace("%minecraftVersion%",minecraftVersion);

                    getLogger().info(json);

                    OkHttpClient client = new OkHttpClient();
                    MediaType mediaType = MediaType.get("application/json");
                    RequestBody body = RequestBody.create(json,mediaType);
                    Request request = new Request.Builder()
                            .url(getConfig().getString("webhookUrl"))
                            .post(body)
                            .build();
                    try {
                        Response response = client.newCall(request).execute();
                        if(response.isSuccessful()) {
                            getLogger().log(Level.INFO, "onServerStop message sent!");
                        }
                        else {
                            getLogger().log(Level.INFO, "onServerStop message failed: " + response.body().string());
                        }
                    } catch (IOException e) {
                        getLogger().log(Level.WARNING, "Failed to send server stop message: " + e.getMessage());
                    }
                }
            }
        }

        getLogger().log(Level.INFO, "this is my final message");
        getLogger().log(Level.INFO, "goodb ye");
    }

    public Integer getVersion() {

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(buildNumberUrl)
                .get()
                .build();
        try (Response response = client.newCall(request).execute()) {

            if (response.isSuccessful()) {
                String body = response.body().string();
                body = body.trim();
                body = body.replaceAll("[\r\n\t]", "");
                int receivedBuildNumber = Integer.parseInt(body);
                response.close();
                return receivedBuildNumber;
            }

        } catch (IOException e) {
            getLogger().log(Level.WARNING, getConfig().getString(localeLang + ".update.checkFailed") + e.getMessage());
        }
        return -1;
    }
}