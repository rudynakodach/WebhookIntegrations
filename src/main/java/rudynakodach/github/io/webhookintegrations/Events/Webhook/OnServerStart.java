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

package rudynakodach.github.io.webhookintegrations.Events.Webhook;

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;
import rudynakodach.github.io.webhookintegrations.Modules.MessageConfiguration;
import rudynakodach.github.io.webhookintegrations.Modules.MessageType;
import rudynakodach.github.io.webhookintegrations.WebhookActions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.TimeZone;

public class OnServerStart implements Listener {

    private final JavaPlugin plugin;

    public OnServerStart(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onServerLoad(ServerLoadEvent event) {
        if(event.getType() != ServerLoadEvent.LoadType.STARTUP) { return; }

        if(!MessageConfiguration.get().canAnnounce(MessageType.SERVER_START)) {
            return;
        }
        String json = MessageConfiguration.get().getMessage(MessageType.SERVER_START);

        String serverIp = plugin.getServer().getIp();
        int slots = plugin.getServer().getMaxPlayers();
        String serverMotd = PlainTextComponentSerializer.plainText().serialize(plugin.getServer().motd());
        String serverName = plugin.getServer().getName();
        String serverVersion = plugin.getServer().getVersion();
        Boolean isOnlineMode = plugin.getServer().getOnlineMode();
        int playersOnline = plugin.getServer().getOnlinePlayers().size();

        if(json == null) {
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        sdf.setTimeZone(TimeZone.getTimeZone(plugin.getConfig().getString("timezone")));

        json = json.replace("$serverIp$", serverIp)
                .replace("$timestamp$", sdf.format(new Date()))
                .replace("$time$", new SimpleDateFormat(
                        Objects.requireNonNullElse(
                                plugin.getConfig().getString("date-format"),
                                "")).format(new Date()))
                .replace("$maxPlayers$", String.valueOf(slots))
                .replace("$serverMotd$", serverMotd)
                .replace("$serverName$", serverName)
                .replace("$serverVersion$", serverVersion)
                .replace("$isOnlineMode$", String.valueOf(isOnlineMode))
                .replace("$playersOnline$", String.valueOf(playersOnline));

        new WebhookActions(plugin).SendSync(json);
    }
}
