package rudynakodach.github.io.webhookintegrations;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.bukkit.plugin.java.JavaPlugin;
import rudynakodach.github.io.webhookintegrations.Commands.SendToWebhook;
import rudynakodach.github.io.webhookintegrations.Commands.SetWebhookURL;

import java.io.IOException;
import java.util.Objects;
import java.util.logging.Level;

public final class WebhookIntegrations extends JavaPlugin {

    public static int currentBuildNumber = 3;
    static String buildNumberUrl = "https://raw.githubusercontent.com/rudynakodach/WebhookIntegrations/master/buildnumber";

    //on startup
    @Override
    public void onEnable() {

        getLogger().log(Level.INFO, "Witaj Świecie!");

        getLogger().log(Level.INFO, "Sprawdzanie akutalizacji..");

        int receivedBuildNumber = getVersion();
        if (currentBuildNumber < receivedBuildNumber && receivedBuildNumber != -1) {
            Component text = Component.text("Nowa wersja jest dostępna na githubie! Proszę zaktualizować plugin!.", NamedTextColor.GREEN);
            getComponentLogger().info(text);
        }

        this.saveDefaultConfig();

        PlayerEventListener pel = new PlayerEventListener(getLogger(), this.getConfig(), this);


        if (Objects.equals(getConfig().getString("webhookUrl"), "")) {
            getLogger().log(Level.WARNING, "Zmienna webhookUrl jest pusta zmień ją w configu lub uzyj \"/seturl <url>\"!");
        }

        getServer().getPluginManager().registerEvents(pel, this);
        getLogger().log(Level.INFO, "Zarejestrowano eventy.");

        SetWebhookURL setWebhookUrlCommand = new SetWebhookURL(pel, getConfig(), this, getLogger());
        Objects.requireNonNull(getCommand("seturl")).setExecutor(setWebhookUrlCommand);

        SendToWebhook sendToWebhookCommand = new SendToWebhook(pel, getConfig(), this, getLogger());
        Objects.requireNonNull(getCommand("send")).setExecutor(sendToWebhookCommand);
        getLogger().log(Level.INFO, "Zarehestrowano komendy.");

    }

    //on shutdown
    @Override
    public void onDisable() {
        getLogger().log(Level.INFO, "to moja ostatnia wiadomosć");
        getLogger().log(Level.INFO, "do widzenia świecie!");
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
            getLogger().log(Level.WARNING, "Wystąpił błąd podczas sprawdzania numeru wersji: " + e.getMessage());
        }
        return -1;
    }
}
