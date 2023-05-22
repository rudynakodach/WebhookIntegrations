package rudynakodach.github.io.webhookintegrations;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import rudynakodach.github.io.webhookintegrations.Modules.LanguageConfiguration;

import java.io.IOException;
import java.io.OutputStream;
import java.net.*;
import java.util.Objects;
import java.util.logging.Level;

public class WebhookActions {
    JavaPlugin plugin;
    public WebhookActions(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void SendAsync(String json) {
        if(!plugin.getConfig().getBoolean("isEnabled")) {return;}
        if(!plugin.getConfig().contains("webhookUrl")) {
            plugin.getLogger().log(Level.SEVERE, LanguageConfiguration.get().getString("config.noWebhookUrl"));
            return;
        }

        String webhookUrl = Objects.requireNonNull(plugin.getConfig().getString("webhookUrl")).trim();

        if (webhookUrl.equals("")) {
            plugin.getLogger().log(Level.WARNING, "Attempted to send a message to an empty webhook URL! Use /setUrl or disable the event in the config!");
            return;
        }

        new BukkitRunnable() {
            public void run() {
                try {
                    URL url = new URL(webhookUrl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setDoOutput(true);

                    OutputStream outputStream = connection.getOutputStream();
                    outputStream.write(json.getBytes());
                    outputStream.flush();
                    outputStream.close();

                    int responseCode = connection.getResponseCode();
                    if (responseCode >= 400) {
                        plugin.getLogger().log(Level.WARNING, "Failed to send eventMessage to Discord webhook: " + connection.getResponseMessage());
                        plugin.getLogger().log(Level.INFO, json);
                    }
                } catch (IOException e) {
                    plugin.getLogger().warning("Failed to POST json to URL: " + e.getMessage());
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    public void SendSync(String json) {
        if(!plugin.getConfig().getBoolean("isEnabled")) {return;}
        if(!plugin.getConfig().contains("webhookUrl")) {
            plugin.getLogger().log(Level.SEVERE, LanguageConfiguration.get().getString("config.noWebhookUrl"));
            return;
        }

        String webhookUrl = Objects.requireNonNull(plugin.getConfig().getString("webhookUrl")).trim();

        if (webhookUrl.equals("")) {
            plugin.getLogger().log(Level.WARNING, "Attempted to send a message to an empty webhook URL! Use /setUrl or disable the event in the config!");
            return;
        }

        try {
            URL url = new URL(webhookUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(json.getBytes());
            outputStream.flush();
            outputStream.close();

            int responseCode = connection.getResponseCode();
            if (responseCode >= 400) {
                plugin.getLogger().log(Level.WARNING, "Failed to send eventMessage to Discord webhook: " + connection.getResponseMessage());
                plugin.getLogger().log(Level.INFO, json);
            }
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to POST json to URL: " + e.getMessage());
        }
    }
}
