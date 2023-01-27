# WebhookIntegrations
The simpliest solution for Discord Webhook integration with your Minecraft server.

# Features

- Chat logging
- Player quit/join messages
- Player death messages
- Advancement logging

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
   
   send:
    Sends the provided message to a webhook.
    Usage: /send <isEmbed | boolean> <message | string>
    Permission: webhookintegration.send
```

# Configuration
Setting up this plugin is trivial.
In this example, I will modify the join message to be as in the image below
![Image](https://cdn.discordapp.com/attachments/943973201392861216/1068280210333630464/image.png)
The following message is a `onPlayerJoinEventMessage` message of specified `onPlayerJoinEventEmbedColor`.
To make your message look the same, `onPlayerEventMessage` will be set to:
```yml
onPlayerJoinEventMessage: '[%time%] **%player%** joined the server.'
```
and for the color,
```yml
onPlayerJoinEventEmbedColor: 3066993
```
If you don't want a message to be announced, like chat messages, you need to disabled them in the config:
```yml
announceChatMessages: false
```

Keep in mind that the embed color has to be a decimal number.
