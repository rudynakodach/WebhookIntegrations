package rudynakodach.github.io.webhookintegrations.Events;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import rudynakodach.github.io.webhookintegrations.Modules.MessageConfiguration;
import rudynakodach.github.io.webhookintegrations.Modules.MessageType;
import rudynakodach.github.io.webhookintegrations.WebhookActions;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class onPlayerQuit implements Listener {

    JavaPlugin plugin;

    public onPlayerQuit(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        if (!MessageConfiguration.get().canAnnounce(MessageType.PLAYER_QUIT.getValue())) {return;}

        String json = MessageConfiguration.get().getMessage(MessageType.PLAYER_QUIT.getValue());

        if(json == null) {
            return;
        }

        json = json.replace("$playersOnline$",String.valueOf(plugin.getServer().getOnlinePlayers().size()))
            .replace("$timestamp$", DateTimeFormatter.ISO_INSTANT.format(Instant.now()))
            .replace("$maxPlayers$",String.valueOf(plugin.getServer().getMaxPlayers()))
            .replace("$player$", event.getPlayer().getName())
            .replace("$time$", new SimpleDateFormat("HH:mm:ss").format(new Date()));

        if(plugin.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            json = PlaceholderAPI.setPlaceholders(event.getPlayer(), json);
        }

        new WebhookActions(plugin).SendAsync(json);
    }
}
