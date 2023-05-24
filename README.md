# WebhookIntegrations
The simpliest solution for Discord Webhook integration with your Minecraft server.

[![License](https://img.shields.io/github/license/rudynakodach/WebhookIntegrations?style=plastic)](https://img.shields.io/github/license/rudynakodach/WebhookIntegrations)

[![Servers](https://img.shields.io/bstats/servers/18509)](https://bstats.org/plugin/bukkit/WebhookIntegrations/18509)
[![Players](https://img.shields.io/bstats/players/18509)](https://bstats.org/plugin/bukkit/WebhookIntegrations/18509)

# Download
Get the plugin from [SpigotMC](https://www.spigotmc.org/resources/webhookintegrations-1-17-1-19-pl-en.107688/) or [CurseForge](https://legacy.curseforge.com/minecraft/bukkit-plugins/webhookintegrations)

## NOTE
**THIS PLUGIN WILL NO LONGER SUPPORT LEGACY MINECRAFT VERSIONS. IF YOUR SERVER'S VERSION IS OUTDATED, PLEASE USE __ViaRewind__, __ViaBackwards__ or __ViaVersion__ DEPENDING ON YOUR NEEDS.**
# Features

- Chat logging
- Player quit/join messages
- Player kick logging
- Player death messages
- Advancement logging
- Server start/stop messages

# Usage
Download the .jar file from [here](https://github.com/rudynakodach/WebhookIntegrations/releases/latest) and drop it into your `Plugins` folder.

Once in game/console, to set the URL for your Webhook, use `/seturl <url>`.
If done correctly, you will be able to see messages appearing in the specified channel.
However, if you do not see the bot to start messaging, check the console for warnings from the plugin.

You can configure what messages will be sent and their format inside WebhookIntegrations/config.yml of your server's Plugins folder

# Permissions & Commands
The plugin currently has 2 commands which hold the main functionality.
```
  setWebhookUrl:
    Sets the URL for your webhook.
    Usage: /seturl <url | string>
    Permission: webhookintegration.seturl
```
```
   send:
    Sends the provided message to a webhook.
    Usage: /send <isEmbed | boolean> <message | string>
    Permission: webhookintegration.send
```

# Configuration
Setting up this plugin is trivial.
In this example, I will modify the join message to be as in the image below
![Image](https://cdn.discordapp.com/attachments/943973201392861216/1068280210333630464/image.png)

The following image is a result of a player joining the server. 
The root containing the JSON is called `onPlayerJoin`.
`onPlayerJoin` contains two entries:
```yml
  announce: true
  messageJson: "..."
```
`announce` determines whether send the event to the webhook or not.

`messageJson` is the raw JSON sent to your webhook, allowing you to fully customize messages.

To achieve the same result as the image above, the JSON would look like this:
```json
{
  "embeds": [
    {
      "description": "[%time%] **%player%** joined the game.", 
      "color": 3066993
    }
  ]
}
```
JSON syntax is very easy and important. An incorrect JSON will not be sent, and you will see a warning in the console.

A both short and good overview of Webhook structure can be found [here](https://gist.github.com/Birdie0/78ee79402a4301b1faf412ab5f1cdcf9#structure-of-webhooks).

