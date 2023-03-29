package rudynakodach.github.io.webhookintegrations.Events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import rudynakodach.github.io.webhookintegrations.WebhookActions;

import java.text.SimpleDateFormat;
import java.util.Date;

public class onPlayerQuit implements Listener {

    JavaPlugin plugin;

    public onPlayerQuit(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        if (!plugin.getConfig().getBoolean("onPlayerQuit.announce")) {return;}

        String json = plugin.getConfig().getString("onPlayerQuit.messageJson");

        json = json.replace("%playersOnline%",String.valueOf(plugin.getServer().getOnlinePlayers().size()));
        json = json.replace("%maxPlayers%",String.valueOf(plugin.getServer().getMaxPlayers()));
        json = json.replace("%player%", event.getPlayer().getName());
        json = json.replace("%time%", new SimpleDateFormat("HH:mm:ss").format(new Date()));

        new WebhookActions(plugin).SendAsync(json);

    }
}
