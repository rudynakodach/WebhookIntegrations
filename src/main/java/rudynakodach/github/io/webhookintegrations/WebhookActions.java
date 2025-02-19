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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rudynakodach.github.io.webhookintegrations.Modules.LanguageConfiguration;
import rudynakodach.github.io.webhookintegrations.Modules.MessageConfiguration;
import rudynakodach.github.io.webhookintegrations.Modules.MessageType;
import rudynakodach.github.io.webhookintegrations.Modules.TemplateConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class WebhookActions {
    private final JavaPlugin plugin;
    private final String target;
    private final String json;

    private @Nullable HashMap<String, String> headers = null;

    public WebhookActions(MessageConfiguration.Message message) {
        this.plugin = message.getPlugin();
        this.target = message.getTarget();
        this.json = message.getJson();
    }

    public WebhookActions(TemplateConfiguration.Template template) {
        this.plugin = template.getPlugin();
        this.target = template.getTarget();
        this.json = template.getJson();
    }

    public WebhookActions(JavaPlugin plugin, String target, String json) {
        this.plugin = plugin;
        this.target = target;
        this.json = json;
    }

    public @NotNull WebhookActions setHeaders(@Nullable HashMap<String, String> headers) {
        this.headers = headers;

        return this;
    }

    public @NotNull WebhookActions setHeaders(MessageType type) {
        this.headers = MessageConfiguration.get().getHeaders(type);

        return this;
    }

    /**
     * Asynchronously sends JSON payloads to the webhook URL using {@link BukkitRunnable}. Will do nothing if {@code webhookUrl} is not set or {@code isEnabled} is set to false in the config.
     */
    public void SendAsync() {
        if(!plugin.getConfig().getBoolean("isEnabled")) {return;}
        if(!plugin.getConfig().contains("webhooks.%s".formatted(target))) {
            plugin.getLogger().log(Level.SEVERE, LanguageConfiguration.get().getLocalizedString("config.noWebhookUrl").formatted(target));
            return;
        }

        String webhookUrl = plugin.getConfig().getString("webhooks.%s".formatted(target), "").trim();

        if (webhookUrl.isEmpty()) {
            plugin.getLogger().log(Level.WARNING, "Attempted to send a message to an empty webhook URL! Use /setUrl or disable the event in the config!");
            return;
        }

        new BukkitRunnable() {
            public void run() {
                send(webhookUrl, json);
            }
        }.runTaskAsynchronously(plugin);
    }

    /**
     * Synchronously sends JSON payloads to the webhook URL. Will do nothing if {@code webhookUrl} is not set or {@code isEnabled} is set to false in the config.
     */
    public void SendSync() {
        if(!plugin.getConfig().getBoolean("isEnabled")) {return;}
        if(!plugin.getConfig().contains("webhooks.%s".formatted(target))) {
            plugin.getLogger().log(Level.SEVERE, LanguageConfiguration.get().getLocalizedString("config.noWebhookUrl"));
            return;
        }

        String webhookUrl = plugin.getConfig().getString("webhooks.%s".formatted(target), "").trim();

        if (webhookUrl.isEmpty()) {
            plugin.getLogger().log(Level.WARNING, "Attempted to send a message to an empty webhook URL! Use /setUrl or disable the event in the config!");
            return;
        }

        send(webhookUrl, json);
    }

    private void send(@NotNull String webhookUrl, @NotNull String json) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(webhookUrl);

        StringEntity requestEntity = new StringEntity(
                json,
                ContentType.APPLICATION_JSON
        );
        httpPost.setEntity(requestEntity);

        if(headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                httpPost.addHeader(entry.getKey(), entry.getValue());
            }
        }

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
     * @return whether the player is vanished. {@code False} if {@code disableForVanishedPlayers} is set to {@code true} in the config
     */
    public static boolean isPlayerVanished(@NotNull JavaPlugin plugin, @NotNull Player player) {
        if(!plugin.getConfig().getBoolean("disableForVanishedPlayers")) {
            return false;
        }

        for (MetadataValue meta : player.getMetadata("vanished")) {
            if (meta.asBoolean()) return true;
        }
        return false;
    }

    public static @NotNull String escapeMarkdown(@NotNull String s) {
        return s.replaceAll("[*_~#\\-|\\[\\]()>`]", "\\\\\\\\$0");
    }

    public static @NotNull String removeColorCoding(@NotNull JavaPlugin plugin, @NotNull String text) {
        String regex = plugin.getConfig().getString("color-code-regex", "[&§][a-f0-9klmnorA-FKLMNOR]|&?#[0-9a-fA-F]{6}");

        return  text.replaceAll(regex, "");
    }

    public static int getPlayerCount(@NotNull JavaPlugin plugin) {
        if(plugin.getConfig().getBoolean("exclude-vanished-from-player-count", false)) {
            return (int) plugin.getServer().getOnlinePlayers().stream().filter(p -> !isPlayerVanished(plugin, p)).count();
        } else {
            return plugin.getServer().getOnlinePlayers().size();
        }
    }
}
