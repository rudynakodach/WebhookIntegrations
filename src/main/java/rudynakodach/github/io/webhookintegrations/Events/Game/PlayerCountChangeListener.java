package rudynakodach.github.io.webhookintegrations.Events.Game;

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;
import rudynakodach.github.io.webhookintegrations.Modules.MessageConfiguration;
import rudynakodach.github.io.webhookintegrations.Modules.MessageType;
import rudynakodach.github.io.webhookintegrations.WebhookActions;
import rudynakodach.github.io.webhookintegrations.WebhookIntegrations;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class PlayerCountChangeListener implements Listener {

    private final JavaPlugin plugin;
    private @Nullable BukkitRunnable currentRunnable;
    private int lastPlayerCountSent = 0;

    public PlayerCountChangeListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    private void update() {
        if(!MessageConfiguration.get().canAnnounce(MessageType.PLAYER_COUNT_CHANGED)) {
            return;
        }

        if(currentRunnable != null) {
            currentRunnable.cancel();
        }


        currentRunnable = new BukkitRunnable() {
            @Override
            public void run() {
                if(this.isCancelled() || WebhookActions.getPlayerCount(plugin) == lastPlayerCountSent) {
                   return;
                }

                sendPlayerCount();
            }
        };

        int delay = MessageConfiguration.get().getYamlConfig().getInt("%s.timeout-delay".formatted(MessageType.PLAYER_COUNT_CHANGED), 0);
        if(delay > 0) {
            currentRunnable.runTaskLaterAsynchronously(plugin, delay);
        } else {
            currentRunnable.runTaskAsynchronously(plugin);
        }
    }

    private void sendPlayerCount() {
        String json = MessageConfiguration.get().getMessage(MessageType.PLAYER_COUNT_CHANGED);

        int playerCount = WebhookActions.getPlayerCount(plugin);
        int playerCountChange = playerCount - lastPlayerCountSent;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        sdf.setTimeZone(TimeZone.getTimeZone(plugin.getConfig().getString("timezone")));

        String serverMotd = PlainTextComponentSerializer.plainText().serialize(plugin.getServer().motd());
        json = json.replace("$motd$", serverMotd)
                .replace("$playersOnline$", String.valueOf(playerCount))
                .replace("$oldPlayerCount$", String.valueOf(lastPlayerCountSent))
                .replace("$time$", new SimpleDateFormat(plugin.getConfig().getString("date-format", "HH:mm:ss")).format(new Date()))
                .replace("$timestamp$", sdf.format(new Date()))
                .replace("$playerCountChange$", playerCountChange > 0 ? "+" + playerCountChange : String.valueOf(playerCountChange));


        lastPlayerCountSent = WebhookActions.getPlayerCount(plugin);


        if(plugin.getConfig().getBoolean("remove-color-coding", false)) {
            json = WebhookActions.removeColorCoding(plugin, json);
        }

        new WebhookActions(plugin, MessageConfiguration.get().getTarget(MessageType.PLAYER_COUNT_CHANGED)).setHeaders(MessageConfiguration.get().getHeaders(MessageType.PLAYER_COUNT_CHANGED)).SendSync(json);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        update();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        update();
    }
}
