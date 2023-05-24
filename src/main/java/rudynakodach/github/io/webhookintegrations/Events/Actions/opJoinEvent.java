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

package rudynakodach.github.io.webhookintegrations.Events.Actions;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import rudynakodach.github.io.webhookintegrations.WebhookIntegrations;

import java.util.ArrayList;
import java.util.Collection;

public class opJoinEvent implements Listener {
    Collection<String> opsJoined = new ArrayList<>();
    private final JavaPlugin plugin;

    public opJoinEvent(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {
        if (!opsJoined.contains(event.getPlayer().getName())) {
            if (plugin.getServer().getOperators().contains(event.getPlayer())) {
                if (!WebhookIntegrations.isLatest) {
                    event.getPlayer().sendMessage(ChatColor.GRAY + "[" + ChatColor.BLUE + "W" + ChatColor.WHITE + "I" + ChatColor.GRAY + "]" + ChatColor.WHITE + " Update available. Please update from either GitHub, SpigotMC or Bukkit.");
                    opsJoined.add(event.getPlayer().getName());
                }
            }
        }
    }
}
