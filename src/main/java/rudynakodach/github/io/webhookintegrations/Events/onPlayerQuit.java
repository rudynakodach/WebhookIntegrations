package rudynakodach.github.io.webhookintegrations.Events;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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
        if (!plugin.getConfig().getBoolean("onPlayerQuit.announce")) {
            return;
        }

        String json = plugin.getConfig().getString("onPlayerQuit.messageJson");

        assert json != null;
        if (json.equals("")) {
            Component warning = Component.text("Attempted to send an empty JSON on " +
                    NamedTextColor.GOLD + "onPlayerQuit!");
            plugin.getComponentLogger().warn(warning);
            return;
        }

        json = json.replace("%player%", event.getPlayer().getName());
        json = json.replace("%time%", new SimpleDateFormat("HH:mm:ss").format(new Date()));

        new WebhookActions(plugin).Send(json);

    }
}
