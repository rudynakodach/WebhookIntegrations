package rudynakodach.github.io.webhookintegrations.Events;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import rudynakodach.github.io.webhookintegrations.Modules.MessageConfiguration;
import rudynakodach.github.io.webhookintegrations.Modules.MessageType;
import rudynakodach.github.io.webhookintegrations.WebhookActions;

import java.text.SimpleDateFormat;
import java.util.Date;

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
        String time = new SimpleDateFormat("HH:mm:ss").format(new Date());
        String playerWorldName = event.getPlayer().getWorld().getName();

        String json = MessageConfiguration.get().getMessage(MessageType.PLAYER_CHAT.getValue());

        if(json == null) {
            return;
        }

        for(String key : plugin.getConfig().getConfigurationSection("censoring").getKeys(false)) {
            message = message.replace(key, String.valueOf(plugin.getConfig().get("censoring." + key)));
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

        json = json.replace("$playersOnline$",String.valueOf(plugin.getServer().getOnlinePlayers().size()))
            .replace("$maxPlayers$",String.valueOf(plugin.getServer().getMaxPlayers()))
            .replace("$uuid$", event.getPlayer().getUniqueId().toString())
            .replace("$player$", playerName)
            .replace("$time$", time)
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
