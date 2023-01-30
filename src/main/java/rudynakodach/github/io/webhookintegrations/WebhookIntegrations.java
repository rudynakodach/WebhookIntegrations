package rudynakodach.github.io.webhookintegrations;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import rudynakodach.github.io.webhookintegrations.Commands.SendToWebhook;
import rudynakodach.github.io.webhookintegrations.Commands.SetWebhookURL;
import rudynakodach.github.io.webhookintegrations.Events.*;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Objects;
import java.util.logging.Level;

public final class WebhookIntegrations extends JavaPlugin {

    public static int currentBuildNumber = 6;
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
            Component text = Component.text(lang.getString(localeLang + ".update.updateFound"), NamedTextColor.GREEN);
            getComponentLogger().info(text);
        } else {
            Component text = Component.text(lang.getString(localeLang + ".update.latest"), NamedTextColor.GREEN);
            getComponentLogger().info(text);
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

        getLogger().log(Level.INFO, lang.getString(localeLang + ".onStart.eventRegisterFinish"));

        SetWebhookURL setWebhookUrlCommand = new SetWebhookURL(getConfig(), this);
        Objects.requireNonNull(getCommand("seturl")).setExecutor(setWebhookUrlCommand);

        SendToWebhook sendToWebhookCommand = new SendToWebhook(getConfig(), this, getLogger());
        Objects.requireNonNull(getCommand("send")).setExecutor(sendToWebhookCommand);
        getLogger().log(Level.INFO, lang.getString(localeLang + ".onStart.commandRegisterFinish"));

    }

    //on shutdown
    @Override
    public void onDisable() {
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
