package rudynakodach.github.io.webhookintegrations;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import okhttp3.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.util.logging.Level;

public class WebhookActions {

    JavaPlugin plugin;

    public WebhookActions(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void Send(String json) {
        String webhookUrl = plugin.getConfig().getString("webhookUrl").trim();

        if (webhookUrl.equals("")) {
            Component warningMessage = Component.text("Attempted to send a message to an empty webhook URL! Use /seturl or disable the event in the config!", NamedTextColor.RED);
            plugin.getComponentLogger().warn(warningMessage);
        }
        new BukkitRunnable() {
            public void run() {
                OkHttpClient client = new OkHttpClient();
                MediaType mediaType = MediaType.get("application/json");
                RequestBody requestBody = RequestBody.create(json, mediaType);

                Request request = new Request.Builder()
                        .url(webhookUrl)
                        .post(requestBody)
                        .build();
                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        plugin.getLogger().log(Level.WARNING, "Failed to send eventMessage to Discord webhook: " + response.body().string()); // <-- this caused me a mental breakdown
                        plugin.getLogger().log(Level.INFO, json);
                    }
                    response.close();
                } catch (IOException e) {
                    plugin.getLogger().warning("Failed to POST json to URL: " + e.getMessage());
                }
            }
        }.runTaskAsynchronously(plugin);
    }
}
