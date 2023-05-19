package rudynakodach.github.io.webhookintegrations.Events;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.plugin.java.JavaPlugin;
import rudynakodach.github.io.webhookintegrations.WebhookActions;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class onPlayerAdvancementCompleted implements Listener {
    JavaPlugin plugin;
    public onPlayerAdvancementCompleted(JavaPlugin plugin) {
        this.plugin = plugin;
    }
    @EventHandler
    public void onAdvancementMade(PlayerAdvancementDoneEvent event) {
        if (!plugin.getConfig().getBoolean("onPlayerAdvancementComplete.announce")) {
            return;
        }

        // if the advancement is hidden
        if(event.getAdvancement().getDisplay() == null) {
            return;
        }

        String advancement = PlainTextComponentSerializer.plainText().serialize(event.getAdvancement().getDisplay().title());
        String advancementDescription = PlainTextComponentSerializer.plainText().serialize(event.getAdvancement().getDisplay().description());

        String json = plugin.getConfig().getString("onPlayerAdvancementComplete.messageJson");

            json = json.replace("$desc$", advancementDescription)
                .replace("$playersOnline$", String.valueOf(plugin.getServer().getOnlinePlayers().size()))
                .replace("$advancement$", advancement)
                .replace("$player$", event.getPlayer().getName())
                .replace("$uuid$", event.getPlayer().getUniqueId().toString())
                .replace("$time$", new SimpleDateFormat("HH:mm:ss").format(new Date()));

            if(plugin.getServer().getPluginManager().getPermission("PlaceholderAPI") != null) {
                json = PlaceholderAPI.setPlaceholders(event.getPlayer(), json);
            }

            new WebhookActions(plugin).SendAsync(json);
    }
}