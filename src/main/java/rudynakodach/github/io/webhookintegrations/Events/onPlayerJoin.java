package rudynakodach.github.io.webhookintegrations.Events;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import rudynakodach.github.io.webhookintegrations.WebhookActions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

public class onPlayerJoin implements Listener {

    JavaPlugin plugin;
    Collection<Player> players = new ArrayList<>();
    public onPlayerJoin(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        if(!players.contains(event.getPlayer())) {
            if (plugin.getServer().getOperators().contains(event.getPlayer())) {
                event.getPlayer().sendMessage(ChatColor.GRAY + "[" + ChatColor.BLUE + "W" + ChatColor.WHITE + "I" + ChatColor.GRAY + "]" + ChatColor.WHITE + " Update available. Please update from either GitHub, SpigotMC or Bukkit.");
                players.add(event.getPlayer());
            }
        }
        if (plugin.getConfig().getBoolean("onPlayerJoin.announce")) {
            String json = plugin.getConfig().getString("onPlayerJoin.messageJson");

            json = json.replace("%playersOnline%", String.valueOf(plugin.getServer().getOnlinePlayers().size()));
            json = json.replace("%maxPlayers%", String.valueOf(plugin.getServer().getMaxPlayers()));
            json = json.replace("%uuid%", event.getPlayer().getUniqueId().toString());
            json = json.replace("%player%", event.getPlayer().getName());
            json = json.replace("%time%", new SimpleDateFormat("HH:mm:ss").format(new Date()));

            new WebhookActions(plugin).Send(json);
        }
    }
}
