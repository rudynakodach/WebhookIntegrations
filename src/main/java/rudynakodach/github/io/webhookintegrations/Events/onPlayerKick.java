package rudynakodach.github.io.webhookintegrations.Events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.plugin.java.JavaPlugin;
import rudynakodach.github.io.webhookintegrations.WebhookActions;

import java.text.SimpleDateFormat;
import java.util.Date;

public class onPlayerKick implements Listener {
    JavaPlugin plugin;

    public onPlayerKick(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerKickedEvent(PlayerKickEvent event) {
        if (!plugin.getConfig().getBoolean("onPlayerKicked.announce")) {
            return;
        }
        String playerName = event.getPlayer().getName();
        String reason = event.getReason();
        String time = new SimpleDateFormat("HH:mm:ss").format(new Date());

        if (reason.equals("")) {
            reason = "Unspecified reason.";
        }

        String json = plugin.getConfig().getString("onPlayerKicked.messageJson");

        json = json.replace("%playersOnline%",String.valueOf(plugin.getServer().getOnlinePlayers().size()));
        json = json.replace("%maxPlayers%",String.valueOf(plugin.getServer().getMaxPlayers()));
        json = json.replace("%uuid%", event.getPlayer().getUniqueId().toString());
        json = json.replace("%player%", playerName);
        json = json.replace("%reason%", reason);
        json = json.replace("%time%", time);

        new WebhookActions(plugin).SendAsync(json);
    }
}
