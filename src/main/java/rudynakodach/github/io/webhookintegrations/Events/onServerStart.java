package rudynakodach.github.io.webhookintegrations.Events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;
import rudynakodach.github.io.webhookintegrations.WebhookActions;

import java.text.SimpleDateFormat;
import java.util.Date;

public class onServerStart implements Listener {
    JavaPlugin plugin;
    public onServerStart(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onServerStartEvent(ServerLoadEvent event) {
        if(!plugin.getConfig().getBoolean("onServerStart.announce")) {return;}

        if(event.getType().equals(ServerLoadEvent.LoadType.STARTUP)) {
            String json = plugin.getConfig().getString("onServerStart.messageJson");

            String serverIp = plugin.getServer().getIp();
            int slots = plugin.getServer().getMaxPlayers();
            String serverMotd = plugin.getServer().getMotd();
            String serverName = plugin.getServer().getName();
            String serverVersion = plugin.getServer().getVersion();
            Boolean isOnlineMode = plugin.getServer().getOnlineMode();
            int playersOnline = plugin.getServer().getOnlinePlayers().size();
            String minecraftVersion = plugin.getServer().getMinecraftVersion();

            json = json.replace("%time%", new SimpleDateFormat("HH:mm:ss").format(new Date()));
            json = json.replace("%serverIp%",serverIp);
            json = json.replace("%maxPlayers%",String.valueOf(slots));
            json = json.replace("%serverMotd%",serverMotd);
            json = json.replace("%serverName%",serverName);
            json = json.replace("%serverVersion%", serverVersion);
            json = json.replace("%isOnlineMode%",String.valueOf(isOnlineMode));
            json = json.replace("%playersOnline%",String.valueOf(playersOnline));
            json = json.replace("%minecraftVersion%",minecraftVersion);

            plugin.getLogger().info(json);

            new WebhookActions(plugin).Send(json);
        }
    }
}
