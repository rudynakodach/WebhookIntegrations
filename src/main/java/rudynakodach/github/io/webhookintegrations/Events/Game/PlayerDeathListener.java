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
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;
import rudynakodach.github.io.webhookintegrations.Modules.MessageConfiguration;
import rudynakodach.github.io.webhookintegrations.Modules.MessageType;
import rudynakodach.github.io.webhookintegrations.Utils.Timeout.TimeoutManager;
import rudynakodach.github.io.webhookintegrations.WebhookActions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.TimeZone;

public class PlayerDeathListener implements Listener {

    JavaPlugin plugin;
    public PlayerDeathListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent event) {
        if (TimeoutManager.get().isTimedOut(event.getPlayer()) &&
                plugin.getConfig().getBoolean("ignore-events-during-timeout", false)) {
            return;
        }

        String playerName = event.getEntity().getName();
        if(plugin.getConfig().getBoolean("preventUsernameMarkdownFormatting")) {
            playerName = WebhookActions.escapeMarkdown(event.getPlayer().getName());
        }
        String deathMessage = PlainTextComponentSerializer.plainText().serialize(event.deathMessage() == null ? Component.empty() : Objects.requireNonNull(event.deathMessage()));

        String newLevel = String.valueOf(event.getNewLevel());
        String newExp = String.valueOf(event.getNewExp());
        String oldLevel = String.valueOf(event.getEntity().getLevel());
        String oldExp = String.valueOf(event.getEntity().getTotalExperience());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        sdf.setTimeZone(TimeZone.getTimeZone(plugin.getConfig().getString("timezone")));

        if(event.getEntity().getKiller() != null) {
            MessageConfiguration.Message message = MessageConfiguration.get().getMessage(MessageType.PLAYER_DEATH_KILLED);
            if(!message.canPlayerTrigger(event.getPlayer().getKiller())) { return; }

            String killerName = event.getEntity().getKiller().getName();
            if(plugin.getConfig().getBoolean("preventUsernameMarkdownFormatting")) {
                killerName = WebhookActions.escapeMarkdown(event.getEntity().getKiller().getName());
            }

            String json = message.getJson();

            json = json.replace("$playersOnline$",String.valueOf(WebhookActions.getPlayerCount(plugin)))
                .replace("$timestamp$", sdf.format(new Date()))
                .replace("$maxPlayers$",String.valueOf(plugin.getServer().getMaxPlayers()))
                .replace("$time$", new SimpleDateFormat(plugin.getConfig().getString("date-format", "HH:mm:ss")).format(new Date()))
                .replace("$victim$",playerName)
                .replace("$rawVictim$", event.getPlayer().getName())
                .replace("$rawKiller$", event.getEntity().getKiller().getName())
                .replace("$killer$",killerName)
                .replace("$deathMessage$",deathMessage)
                .replace("$newLevel$",newLevel)
                .replace("$newExp$",newExp)
                .replace("$oldLevel$",oldLevel)
                .replace("$oldExp$",oldExp);

            if(plugin.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
                json = PlaceholderAPI.setRelationalPlaceholders(event.getPlayer(), event.getEntity().getKiller(), json);
            }

            new WebhookActions(message.setJson(json)).setHeaders(MessageType.PLAYER_DEATH_KILLED).SendAsync();
        }
        else {
            MessageConfiguration.Message message = MessageConfiguration.get().getMessage(MessageType.PLAYER_DEATH_NPC);
            if(!message.canPlayerTrigger(event.getPlayer())) { return; }

            String json = message.getJson();

            if(json == null) {
                return;
            }

            json = json.replace("$time$", new SimpleDateFormat(plugin.getConfig().getString("date-format", "HH:mm:ss")).format(new Date()))
                .replace("$timestamp$", sdf.format(new Date()))
                .replace("$player$", playerName)
                .replace("$rawVictim$", event.getPlayer().getName())
                .replace("$deathMessage$", deathMessage)
                .replace("$newLevel$",newLevel)
                .replace("$newExp$",newExp)
                .replace("$oldLevel$",oldLevel)
                .replace("$oldExp$",oldExp);

            if(plugin.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
                json = PlaceholderAPI.setPlaceholders(event.getPlayer(), json);
            }

            if(plugin.getConfig().getBoolean("remove-color-coding", false)) {
                json = WebhookActions.removeColorCoding(plugin, json);
            }

            new WebhookActions(message.setJson(json)).setHeaders(MessageType.PLAYER_DEATH_NPC).SendAsync();
        }
    }
}
