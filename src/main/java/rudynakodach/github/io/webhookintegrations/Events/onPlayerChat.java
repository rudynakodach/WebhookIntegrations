package rudynakodach.github.io.webhookintegrations.Events;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.serializer.plain.PlainComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
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
        if (!plugin.getConfig().getBoolean("onPlayerChat.announce")) {
            return;
        }
        String message = PlainComponentSerializer.plain().serialize(event.message());
        String playerName = event.getPlayer().getName();
        String time = new SimpleDateFormat("HH:mm:ss").format(new Date());
        String playerWorldName = event.getPlayer().getWorld().getName();

        String json = plugin.getConfig().getString("onPlayerChat.messageJson");

        for(String key : plugin.getConfig().getConfigurationSection("censoring").getKeys(false)) {
            message = message.replace(key, plugin.getConfig().getString("censoring." + key + ".to"));
        }

        json = json.replace("%playersOnline%",String.valueOf(plugin.getServer().getOnlinePlayers().size()));
        json = json.replace("%maxPlayers%",String.valueOf(plugin.getServer().getMaxPlayers()));
        json = json.replace("%player%", playerName);
        json = json.replace("%time%", time);
        json = json.replace("%message%", message);
        json = json.replace("%world%", playerWorldName);

        new WebhookActions(plugin).Send(json);
    }
}
