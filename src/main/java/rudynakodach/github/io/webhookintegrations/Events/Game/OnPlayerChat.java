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

package rudynakodach.github.io.webhookintegrations.Events.Game;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import rudynakodach.github.io.webhookintegrations.Modules.MessageConfiguration;
import rudynakodach.github.io.webhookintegrations.Modules.MessageType;
import rudynakodach.github.io.webhookintegrations.WebhookActions;

import java.text.SimpleDateFormat;
import java.util.*;

public class OnPlayerChat implements Listener {

    JavaPlugin plugin;

    public OnPlayerChat(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerChatEvent(AsyncChatEvent event) {
        if (!MessageConfiguration.get().canAnnounce(MessageType.PLAYER_CHAT)) {
            return;
        }

        if(new WebhookActions(plugin).isPlayerVanished(event.getPlayer())) {
            return;
        }

        boolean allowPlaceholdersInMessage = MessageConfiguration.get().getYamlConfig().getBoolean("onPlayerChat.allow-placeholders-in-message");

        String message = PlainTextComponentSerializer.plainText().serialize(event.message());
        String playerName = event.getPlayer().getName();
        String playerWorldName = event.getPlayer().getWorld().getName();

        String json = MessageConfiguration.get().getMessage(MessageType.PLAYER_CHAT);

        if(json == null) {
            return;
        }

        ConfigurationSection section = plugin.getConfig().getConfigurationSection("censoring");
        if(section != null) {
            Set<String> keys = section.getKeys(false);
            for(String key : keys) {
                message = message.replace(key, String.valueOf(plugin.getConfig().get("censoring." + key)));
            }
        }

        if(plugin.getConfig().getBoolean("remove-force-pings")) {
            message = message.replaceAll("<@[0-9]+>", "");
        }
        if(plugin.getConfig().getBoolean("remove-force-channel-pings")) {
            message = message.replaceAll("<#[0-9]+>", "");
        }
        if(plugin.getConfig().getBoolean("remove-force-role-pings")) {
            message = message.replaceAll("<@&[0-9]+>", "");
        }

        if(message.trim().equalsIgnoreCase("")) {
            return;
        }

        if(plugin.getConfig().getBoolean("useRegexCensoring")) {
            List<String> patterns = Objects.requireNonNull(plugin.getConfig().getConfigurationSection("regexCensoring")).getKeys(false).stream().toList();
            for(String expr : patterns) {
                message = message.replace(expr, Objects.requireNonNull(plugin.getConfig().getString("regexCensoring." + expr)));
            }
        }

        if(allowPlaceholdersInMessage) {
            json = json.replace("$message$", message);
            if(plugin.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
                json = PlaceholderAPI.setPlaceholders(event.getPlayer(), json);
            }
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        sdf.setTimeZone(TimeZone.getTimeZone(plugin.getConfig().getString("timezone")));

        json = json.replace("$playersOnline$",String.valueOf(plugin.getServer().getOnlinePlayers().size()))
            .replace("$timestamp$", sdf.format(new Date()))
            .replace("$maxPlayers$",String.valueOf(plugin.getServer().getMaxPlayers()))
            .replace("$uuid$", event.getPlayer().getUniqueId().toString())
            .replace("$player$", playerName)
                .replace("$time$", new SimpleDateFormat(
                        Objects.requireNonNullElse(
                                plugin.getConfig().getString("date-format"),
                                "HH:mm:ss")).format(new Date())
                )
            .replace("$world$", playerWorldName);

        if(!allowPlaceholdersInMessage) {
            if(plugin.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
                json = PlaceholderAPI.setPlaceholders(event.getPlayer(), json);
            }
            json = json.replace("$message$", message);
        }

        new WebhookActions(plugin).SendAsync(json);
    }
}
