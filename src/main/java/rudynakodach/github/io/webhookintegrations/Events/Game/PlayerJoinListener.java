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

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import rudynakodach.github.io.webhookintegrations.Modules.MessageConfiguration;
import rudynakodach.github.io.webhookintegrations.Modules.MessageType;
import rudynakodach.github.io.webhookintegrations.Utils.Timeout.TimeoutManager;
import rudynakodach.github.io.webhookintegrations.WebhookActions;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;

public class PlayerJoinListener implements Listener {

    JavaPlugin plugin;
    public PlayerJoinListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        if (!MessageConfiguration.get().canAnnounce(MessageType.PLAYER_JOIN)) {
            return;
        }

        if (!MessageConfiguration.get().hasPlayerPermission(event.getPlayer(), MessageType.PLAYER_JOIN)) {
            return;
        }

        if(TimeoutManager.get().isTimedOut(event.getPlayer()) &&
                plugin.getConfig().getBoolean("ignore-events-during-timeout", false)) {
            return;
        }

        if(WebhookActions.isPlayerVanished(plugin, event.getPlayer())) {
            return;
        }

        String json = MessageConfiguration.get().getMessage(MessageType.PLAYER_JOIN);

        if(json == null) {
            return;
        }

        String playerName = event.getPlayer().getName();
        if(plugin.getConfig().getBoolean("preventUsernameMarkdownFormatting")) {
            playerName = WebhookActions.escapeMarkdown(event.getPlayer().getName());
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        sdf.setTimeZone(TimeZone.getTimeZone(plugin.getConfig().getString("timezone")));

        json = json.replace("$playersOnline$", String.valueOf(WebhookActions.getPlayerCount(plugin)))
                .replace("$timestamp$", sdf.format(new Date()))
                .replace("$maxPlayers$", String.valueOf(plugin.getServer().getMaxPlayers()))
                .replace("$uuid$", event.getPlayer().getUniqueId().toString())
                .replace("$player$", playerName)
                .replace("$rawUsername$", event.getPlayer().getName())
                .replace("$time$", new SimpleDateFormat(plugin.getConfig().getString("date-format", "HH:mm:ss")).format(new Date()));

        if(plugin.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            json = PlaceholderAPI.setPlaceholders(event.getPlayer(), json);
        }

        if(plugin.getConfig().getBoolean("remove-color-coding", false)) {
            json = WebhookActions.removeColorCoding(plugin, json);
        }

        String finalJson = json;
        plugin.getLogger().log(Level.INFO, finalJson);
        WebhookActions action = new WebhookActions(plugin, MessageConfiguration.get().getTarget(MessageType.PLAYER_JOIN)).setHeaders(MessageConfiguration.get().getHeaders(MessageType.PLAYER_JOIN));

        int timeoutDelay = plugin.getConfig().getInt("timeout-delay", 0);
        if(timeoutDelay > 0 && !(event.getPlayer().hasPermission("webhookintegrations.bypassTimeout"))) {
            BukkitRunnable runnable = new BukkitRunnable(){
                @Override
                public void run() {
                    // check if player didn't leave
                    if(plugin.getServer().getOnlinePlayers().contains(event.getPlayer())) {
                        action.SendAsync(finalJson);
                    }
                }
            };

            TimeoutManager.get().timeout(event.getPlayer(), runnable);
        } else {
            action.SendAsync(finalJson);
        }
    }
}
