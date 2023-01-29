package rudynakodach.github.io.webhookintegrations.Events;

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
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
        String reason = PlainTextComponentSerializer.plainText().serialize(event.reason());
        String time = new SimpleDateFormat("HH:mm:ss").format(new Date());

        if (reason.equals("")) {
            reason = "Unspecified reason.";
        }

        String json = plugin.getConfig().getString("onPlayerKicked.messageJson");

        json = json.replace("%player%", playerName);
        json = json.replace("%reason%", reason);
        json = json.replace("%time%", time);

        new WebhookActions(plugin).Send(json);
    }
}
