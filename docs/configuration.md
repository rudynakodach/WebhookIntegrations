# WebhookIntegrations config file overview
### [Back to guide](guide.md)

### ⚠️ Do not manually change underlined values unless you know what you're doing. This could break the plugin's functionality

`config-version`: <ins>the config file's current version | int

`check-for-updates`: whether to check for new updates | bool

`auto-update`: whether to update the plugin automatically | bool

`webhooks`: <ins>where all webhooks are paired with their IDs

`send-quit-when-kicked`: if the player gets kicked, do we send the quit message? | bool

`timeout-delay`: the time in ticks that we need to wait before sending any events, used to prevent spam when players rejoining | int

`ignore-events-during-timeout`: whether to ignore all events and not send them when `timeout-delay` is active | bool 

`exclude-vanished-from-player-count`: whether we should ignore vanished players from player counts | bool

`remove-color-coding`: whether to remove all color coding from messages | bool

`color-code-regex`: <ins>the regex pattern to use to remove color coding | string

`preventUsernameMarkdownFormatting`: whether to remove **_markdown_** formatting from player names by displaying unformatted text | bool

`language-override`: <ins>should we use language override | string or none

`isEnabled`: whether the plugin should send events to the webhooks | bool

`disableForVanishedPlayers`: whether to ignore events if a player is vanished | bool

`censoring`: replaces text from player's chat messages, e.g. a configuration of
```yaml
censoring:
  "@everyone": "everyone"
```
will replace a chat message "Hello @everyone" to "Hello everyone"

`useRegexCensoring`: whether we should use regex censoring for chat messages | bool

`regexCensoring`: all regex rules for censoring, works similarly to regular censoring 

`remove-force-pings`: on discord you can ping someone by typing <@id> in chat, where id is the user's id, this removes those pings from messages | bool

`remove-force-channel-pings`: the same thing as above but for channels | bool

`remove-force-role-pings`: the same thing as above but for roles | bool

`date-format`: date format to use | string

`timezone`: your timezone | string
