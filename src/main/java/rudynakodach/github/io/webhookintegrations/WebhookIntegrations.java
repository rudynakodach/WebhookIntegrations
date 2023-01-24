package rudynakodach.github.io.webhookintegrations;

import org.bukkit.plugin.java.JavaPlugin;
import rudynakodach.github.io.webhookintegrations.Commands.SendToWebhook;
import rudynakodach.github.io.webhookintegrations.Commands.SetWebhookURL;

import java.util.Objects;
import java.util.logging.Level;

public final class WebhookIntegrations extends JavaPlugin {

    //on startup
    @Override
    public void onEnable() {

        getLogger().log(Level.INFO, "Hello, world!");

        this.saveDefaultConfig();

        PlayerEventListener pel = new PlayerEventListener(getLogger(), this.getConfig(), this);


        if(Objects.equals(getConfig().getString("webhookUrl"), "")) {
            getLogger().log(Level.WARNING, "WebhookURL is empty and cannot be used! Set the value of webhookUrl inside the config.yml file and restart the server or use \"/seturl <url>\"!");
        }

        getServer().getPluginManager().registerEvents(pel,this);
        getLogger().log(Level.INFO, "Events registered.");

        SetWebhookURL setWebhookUrlCommand = new SetWebhookURL(pel, getConfig(), this, getLogger());
        Objects.requireNonNull(getCommand("seturl")).setExecutor(setWebhookUrlCommand);

        SendToWebhook sendToWebhookCommand = new SendToWebhook(pel, getConfig(), this, getLogger());
        Objects.requireNonNull(getCommand("send")).setExecutor(sendToWebhookCommand);
        getLogger().log(Level.INFO, "Commands registered.");

    }

    //on shutdown
    @Override
    public void onDisable() {
        getLogger().log(Level.INFO, "this is my final message");
        getLogger().log(Level.INFO, "goodb ye");
    }
}
