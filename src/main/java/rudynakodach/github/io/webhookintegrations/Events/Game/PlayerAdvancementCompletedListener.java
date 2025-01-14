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
import rudynakodach.github.io.webhookintegrations.WebhookActions;

import static rudynakodach.github.io.webhookintegrations.Events.Game.PlayerJoinListener.playersOnCountdown;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.TimeZone;

public class PlayerAdvancementCompletedListener implements Listener {
    private final JavaPlugin plugin;

    public PlayerAdvancementCompletedListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onAdvancementMade(PlayerAdvancementDoneEvent event) {
        if (!MessageConfiguration.get().canAnnounce(MessageType.PLAYER_ADVANCEMENT))    { return; }
        if (event.getAdvancement().getDisplay() == null)                                { return; }
        if (WebhookActions.isPlayerVanished(plugin, event.getPlayer()))                 { return; }

        if (playersOnCountdown.contains(event.getPlayer().getName()) &&
                plugin.getConfig().getBoolean("ignore-events-during-timeout", false)) {
            return;
        }

        String advancement = PlainTextComponentSerializer.plainText().serialize(event.getAdvancement().getDisplay().title());
        String advancementDescription = PlainTextComponentSerializer.plainText().serialize(event.getAdvancement().getDisplay().description());

        String json = MessageConfiguration.get().getMessage(MessageType.PLAYER_ADVANCEMENT);

        String playerName = event.getPlayer().getName();
        if(plugin.getConfig().getBoolean("preventUsernameMarkdownFormatting")) {
            playerName = WebhookActions.escapePlayerName(event.getPlayer());
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        sdf.setTimeZone(TimeZone.getTimeZone(plugin.getConfig().getString("timezone")));

        json = json.replace("$desc$", advancementDescription)
                .replace("$timestamp$", sdf.format(new Date()))
                .replace("$playersOnline$", String.valueOf(plugin.getServer().getOnlinePlayers().size()))
                .replace("$advancement$", advancement)
                .replace("$player$", playerName)
                .replace("$uuid$", event.getPlayer().getUniqueId().toString())
                .replace("$time$", new SimpleDateFormat(
                        Objects.requireNonNullElse(
                                plugin.getConfig().getString("date-format"),
                                "HH:mm:ss")).format(new Date())
                );

        if (plugin.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            json = PlaceholderAPI.setPlaceholders(event.getPlayer(), json);
        }

        if(plugin.getConfig().getBoolean("remove-color-coding", false)) {
            json = WebhookActions.removeColorCoding(plugin, json);
        }

        new WebhookActions(plugin, MessageConfiguration.get().getTarget(MessageType.PLAYER_ADVANCEMENT)).SendAsync(json);
    }
}