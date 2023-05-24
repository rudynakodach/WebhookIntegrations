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

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import rudynakodach.github.io.webhookintegrations.Modules.LanguageConfiguration;

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
                CloseableHttpClient httpClient = HttpClients.createDefault();
                HttpPost httpPost = new HttpPost(webhookUrl);

                StringEntity requestEntity = new StringEntity(
                        json,
                        ContentType.APPLICATION_JSON
                );
                httpPost.setEntity(requestEntity);

                try (CloseableHttpResponse ignored = httpClient.execute(httpPost)) {}
                catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        httpClient.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(webhookUrl);

        StringEntity requestEntity = new StringEntity(
                json,
                ContentType.APPLICATION_JSON
        );
        httpPost.setEntity(requestEntity);

        try (CloseableHttpResponse ignored = httpClient.execute(httpPost)) {}
        catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                httpClient.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
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
