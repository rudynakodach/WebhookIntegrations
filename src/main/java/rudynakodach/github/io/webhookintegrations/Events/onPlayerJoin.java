package rudynakodach.github.io.webhookintegrations.Events;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import rudynakodach.github.io.webhookintegrations.WebhookActions;

import java.text.SimpleDateFormat;
import java.util.Date;

public class onPlayerJoin implements Listener {

    JavaPlugin plugin;

    public onPlayerJoin(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        if (!plugin.getConfig().getBoolean("onPlayerJoin.announce")) {
            return;
        }
        String json = plugin.getConfig().getString("onPlayerJoin.messageJson");

        assert json != null;
        if (json.trim().equals("")) {
            Component warningMessage = Component.text("Attempted to send an empty JSON on " +
                    NamedTextColor.GOLD + "onPlayerJoin");
            plugin.getComponentLogger().warn(warningMessage);
            return;
        }

        json = json.replace("%player%", event.getPlayer().getName());
        json = json.replace("%time%", new SimpleDateFormat("HH:mm:ss").format(new Date()));

        new WebhookActions(plugin).Send(json);
    }
}
