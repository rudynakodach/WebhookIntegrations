package rudynakodach.github.io.webhookintegrations.Events;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.plugin.java.JavaPlugin;
import rudynakodach.github.io.webhookintegrations.Modules.MessageConfiguration;
import rudynakodach.github.io.webhookintegrations.Modules.MessageType;
import rudynakodach.github.io.webhookintegrations.WebhookActions;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class onPlayerAdvancementCompleted implements Listener {
    JavaPlugin plugin;
    public onPlayerAdvancementCompleted(JavaPlugin plugin) {
        this.plugin = plugin;
    }
    @EventHandler
    public void onAdvancementMade(PlayerAdvancementDoneEvent event) {
        if (!MessageConfiguration.get().canAnnounce(MessageType.PLAYER_ADVANCEMENT.getValue())) {
            return;
        }

        // if the advancement is hidden
        if (event.getAdvancement().getDisplay() == null) {
            return;
        }

        String advancement = PlainTextComponentSerializer.plainText().serialize(event.getAdvancement().getDisplay().title());
        String advancementDescription = PlainTextComponentSerializer.plainText().serialize(event.getAdvancement().getDisplay().description());

        String json = MessageConfiguration.get().getMessage(MessageType.PLAYER_ADVANCEMENT.getValue());

        json = json.replace("$desc$", advancementDescription)
                .replace("$timestamp$", DateTimeFormatter.ISO_INSTANT.format(Instant.now()))
                .replace("$playersOnline$", String.valueOf(plugin.getServer().getOnlinePlayers().size()))
                .replace("$advancement$", advancement)
                .replace("$player$", event.getPlayer().getName())
                .replace("$uuid$", event.getPlayer().getUniqueId().toString())
                .replace("$time$", new SimpleDateFormat("HH:mm:ss").format(new Date()));

        if (plugin.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            json = PlaceholderAPI.setPlaceholders(event.getPlayer(), json);
        }

        new WebhookActions(plugin).SendAsync(json);
    }
}