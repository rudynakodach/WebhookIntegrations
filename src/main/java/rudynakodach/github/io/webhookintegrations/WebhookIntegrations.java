package rudynakodach.github.io.webhookintegrations;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.logging.Level;

public final class WebhookIntegrations extends JavaPlugin {

    //on startup
    @Override
    public void onEnable() {

        getLogger().log(Level.INFO, "Hello, world!");

        this.saveDefaultConfig();

        PlayerEventListener pel = new PlayerEventListener(getLogger(), this.getConfig(), this);

        Commands cmds = new Commands(pel, getConfig(), this, getLogger());

        if(Objects.equals(getConfig().getString("webhookUrl"), "")) {
            getLogger().log(Level.WARNING, "WebhookURL is empty and cannot be used! Set the value of webhookUrl inside the config.yml file and restart the server or use \"/seturl <url>\"!");
        }

        getServer().getPluginManager().registerEvents(pel,this);
        getLogger().log(Level.INFO, "Events registered.");

        Objects.requireNonNull(getCommand("seturl")).setExecutor(cmds);
        getLogger().log(Level.INFO, "Commands registered.");
    }

    //on shutdown
    @Override
    public void onDisable() {
        getLogger().log(Level.INFO, "this is my final message");
        getLogger().log(Level.INFO, "goodb ye");
    }
}
