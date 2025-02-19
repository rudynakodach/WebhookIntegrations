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
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.plugin.java.JavaPlugin;
import rudynakodach.github.io.webhookintegrations.Modules.MessageConfiguration;
import rudynakodach.github.io.webhookintegrations.Modules.MessageType;
import rudynakodach.github.io.webhookintegrations.Utils.Timeout.TimeoutManager;
import rudynakodach.github.io.webhookintegrations.WebhookActions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class PlayerAdvancementCompletedListener implements Listener {
    private final JavaPlugin plugin;

    public PlayerAdvancementCompletedListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onAdvancementMade(PlayerAdvancementDoneEvent event) {
        MessageConfiguration.Message message = MessageConfiguration.get().getMessage(MessageType.PLAYER_ADVANCEMENT);
        if(!message.canPlayerTrigger(event.getPlayer()))    { return; }
        if (event.getAdvancement().getDisplay() == null)    { return; }


        if (TimeoutManager.get().isTimedOut(event.getPlayer()) &&
                plugin.getConfig().getBoolean("ignore-events-during-timeout", false)) {
            return;
        }

        String advancement = PlainTextComponentSerializer.plainText().serialize(event.getAdvancement().getDisplay().title());
        String advancementDescription = PlainTextComponentSerializer.plainText().serialize(event.getAdvancement().getDisplay().description());

        String json = message.getJson();

        String playerName = event.getPlayer().getName();
        if(plugin.getConfig().getBoolean("preventUsernameMarkdownFormatting")) {
            playerName = WebhookActions.escapeMarkdown(event.getPlayer().getName());
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        sdf.setTimeZone(TimeZone.getTimeZone(plugin.getConfig().getString("timezone")));

        json = json.replace("$desc$", advancementDescription)
                .replace("$timestamp$", sdf.format(new Date()))
                .replace("$playersOnline$", String.valueOf(WebhookActions.getPlayerCount(plugin)))
                .replace("$advancement$", advancement)
                .replace("$rawUsername$", event.getPlayer().getName())
                .replace("$player$", playerName)
                .replace("$uuid$", event.getPlayer().getUniqueId().toString())
                .replace("$time$", new SimpleDateFormat(plugin.getConfig().getString("date-format", "HH:mm:ss")).format(new Date()));

        if (plugin.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            json = PlaceholderAPI.setPlaceholders(event.getPlayer(), json);
        }

        if(plugin.getConfig().getBoolean("remove-color-coding", false)) {
            json = WebhookActions.removeColorCoding(plugin, json);
        }

        new WebhookActions(message.setJson(json)).setHeaders(MessageType.PLAYER_ADVANCEMENT).SendAsync();
    }
}