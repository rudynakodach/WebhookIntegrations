package rudynakodach.github.io.webhookintegrations;

import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import rudynakodach.github.io.webhookintegrations.Modules.LanguageConfiguration;

import java.io.IOException;
import java.io.OutputStream;
import java.net.*;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;

public class WebhookActions {
    JavaPlugin plugin;
    public WebhookActions(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Asynchronously sends JSON payloads to the webhook URL using {@link BukkitRunnable}. Will do nothing if {@code webhookUrl} is not set or {@code isEnabled} is set to false in the config.
     * @param json JSON payload to send
     */
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

    /**
     * Synchronously sends JSON payloads to the webhook URL. Will do nothing if {@code webhookUrl} is not set or {@code isEnabled} is set to false in the config.
     * @param json JSON payload to send
     */
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

    /**
     * @param player Player to check
     * @return Whether the player is vanished. {@code False} if {@code disableForVanishedPlayers} is set to {@code true} in the config
     */
    public boolean isPlayerVanished(Player player) {
        if(!plugin.getConfig().getBoolean("disableForVanishedPlayers")) {
            return false;
        }

        List<MetadataValue> meta = player.getMetadata("vanished");

        for(MetadataValue key : meta) {
            if(Boolean.parseBoolean(key.asString())) {
                return true;
            }
        }

        return false;
    }
}
