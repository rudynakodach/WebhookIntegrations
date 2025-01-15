package rudynakodach.github.io.webhookintegrations.Utils.Timeout;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class TimeoutManager {

    private static TimeoutManager instance;
    private final HashMap<String, @Nullable BukkitRunnable> activeTimeouts = new HashMap<>();

    public TimeoutManager(JavaPlugin plugin) {
        this.plugin = plugin;

        instance = this;
    }

    public static @NotNull TimeoutManager get() {
        return instance;
    }

    private final JavaPlugin plugin;

    public void timeout(Player player, @Nullable BukkitRunnable onEnd) {
        int timeoutDelay = plugin.getConfig().getInt("timeout-delay", 0);


        if(timeoutDelay > 0) {
            BukkitRunnable runnable = new BukkitRunnable() {
                @Override
                public void run() {
                    if(this.isCancelled()) {
                        return;
                    }

                    activeTimeouts.remove(player.getName());

                    if(onEnd != null) {
                        onEnd.run();
                        cancel();
                    }
                }
            };

            if(activeTimeouts.get(player.getName()) != null) {
                activeTimeouts.get(player.getName()).cancel();
            }

            activeTimeouts.put(player.getName(), runnable);

            runnable.runTaskLaterAsynchronously(plugin, timeoutDelay);
        } else {
            if(onEnd != null) {
                onEnd.run();
            }
        }
    }

    public void timeout(Player player) {
        timeout(player, null);
    }

    public boolean isTimedOut(Player player) {
        return activeTimeouts.containsKey(player.getName());
    }

}
