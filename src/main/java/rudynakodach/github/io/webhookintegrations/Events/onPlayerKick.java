package rudynakodach.github.io.webhookintegrations.Events;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.plugin.java.JavaPlugin;
import rudynakodach.github.io.webhookintegrations.Modules.MessageConfiguration;
import rudynakodach.github.io.webhookintegrations.Modules.MessageType;
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
        if (!MessageConfiguration.get().canAnnounce(MessageType.PLAYER_KICK.getValue())) {
            return;
        }
        String playerName = event.getPlayer().getName();
        String reason = PlainTextComponentSerializer.plainText().serialize(event.reason());
        String time = new SimpleDateFormat("HH:mm:ss").format(new Date());

        if (reason.equals("")) {
            reason = "Unspecified reason.";
        }

        String json = MessageConfiguration.get().getMessage(MessageType.PLAYER_KICK.getValue());

        if(json == null) {
            return;
        }

        json = json.replace("$playersOnline$",String.valueOf(plugin.getServer().getOnlinePlayers().size()))
            .replace("$maxPlayers$",String.valueOf(plugin.getServer().getMaxPlayers()))
            .replace("$uuid$", event.getPlayer().getUniqueId().toString())
            .replace("$player$", playerName)
            .replace("$reason$", reason)
            .replace("$time$", time);

        if(plugin.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            json = PlaceholderAPI.setPlaceholders(event.getPlayer(), json);
        }

        new WebhookActions(plugin).SendAsync(json);
    }
}
