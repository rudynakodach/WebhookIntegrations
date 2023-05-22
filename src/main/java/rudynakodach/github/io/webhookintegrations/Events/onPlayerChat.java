package rudynakodach.github.io.webhookintegrations.Events;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import rudynakodach.github.io.webhookintegrations.Modules.MessageConfiguration;
import rudynakodach.github.io.webhookintegrations.Modules.MessageType;
import rudynakodach.github.io.webhookintegrations.WebhookActions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.Set;
import java.util.TimeZone;

public class onPlayerChat implements Listener {

    JavaPlugin plugin;

    public onPlayerChat(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerChatEvent(AsyncChatEvent event) {
        if (!MessageConfiguration.get().canAnnounce(MessageType.PLAYER_CHAT.getValue())) {
            return;
        }
        boolean allowPlaceholdersInMessage = MessageConfiguration.get().getConfig().getBoolean("onPlayerChat.allow-placeholders-in-message");

        String message = PlainTextComponentSerializer.plainText().serialize(event.message());
        String playerName = event.getPlayer().getName();
        String playerWorldName = event.getPlayer().getWorld().getName();

        String json = MessageConfiguration.get().getMessage(MessageType.PLAYER_CHAT.getValue());

        if(json == null) {
            return;
        }

        ConfigurationSection section = plugin.getConfig().getConfigurationSection("censoring");
        if(section != null) {
            Set<String> keys = section.getKeys(false);
            for(String key : keys) {
                message = message.replace(key, String.valueOf(plugin.getConfig().get("censoring." + key)));
            }
        }



        if(plugin.getConfig().getBoolean("remove-force-pings")) {
            message = message.replaceAll("<@[0-9]+>", "");
        }
        if(plugin.getConfig().getBoolean("remove-force-channel-pings")) {
            message = message.replaceAll("<#[0-9]+>", "");
        }

        if(message.trim().equalsIgnoreCase("")) {
            return;
        }

        if(allowPlaceholdersInMessage) {
            json = json.replace("$message$", message);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        sdf.setTimeZone(TimeZone.getTimeZone(plugin.getConfig().getString("timezone")));

        json = json.replace("$playersOnline$",String.valueOf(plugin.getServer().getOnlinePlayers().size()))
            .replace("$timestamp$", sdf.format(new Date()))
            .replace("$maxPlayers$",String.valueOf(plugin.getServer().getMaxPlayers()))
            .replace("$uuid$", event.getPlayer().getUniqueId().toString())
            .replace("$player$", playerName)
                .replace("$time$", new SimpleDateFormat(
                        Objects.requireNonNullElse(
                                plugin.getConfig().getString("date-format"),
                                "HH:mm:ss")).format(new Date())
                )
            .replace("$world$", playerWorldName);

        if(plugin.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            json = PlaceholderAPI.setPlaceholders(event.getPlayer(), json);
        }

        if(!allowPlaceholdersInMessage) {
            json = json.replace("$message$", message);
        }

        new WebhookActions(plugin).SendAsync(json);
    }
}
