# WebhookIntegrations
Najłatwiejsze rozwiązanie do integracji webhooków z serwerem minecraft.

# Funkcje

- Logowanie wiadomości czatu
- Wiadomości wejścia i wyjścia gracza
- Wiadomości o śmierci gracza
- Powiadomienia o osiągnięciach gracza
# Użycie
Pobierz plik .jar z [tąd](https://github.com/rudynakodach/WebhookIntegrations/releases/latest) i dodaj go do folderu `Plugins`.

Kiedy będziesz w grze lub konsoli użyj komendy `/seturl <adres url webhooka>`.
Jeśli użycie tej komendy się powiedzie, powinieneś widzieć twoje wiadomości na dicordzie.
Jeśli nie, sprawdź czy adres url jest poprawny.

Możesz skonfigurować jakie wiadomości mają być wysyłane w pliku `config.yml` w folderze `plugins/WebhookIntegrations`.

# Uprawnienia i komendy
Plugin posiada 2 główne komendy które zawierają jego główne funkcje.
```
  setWebhookUrl:
    SUstawia url webhook'a.
    Usage: /seturl <url | string>
    Permission: webhookintegration.seturl
   
   send:
    Wysyła wiadomosć przez webhook.
    Usage: /send <isEmbed | boolean> <message | string>
    Permission: webhookintegration.send
```

# Konfiguracja
Przygotowanie plguinu do działania jest bardzo proste!
W tym przykładzie zmodyfikujemy wiadomości dołaczenia wysyłane przez plugin.
![Image](https://cdn.discordapp.com/attachments/943973201392861216/1068280210333630464/image.png)
Tą wiadmość możesz edytować używajac zmiennej `onPlayerJoinEventMessage` a kolor jej embedu zmieniać `onPlayerJoinEventEmbedColor`.
Jeśli chesz aby twoja wiadomość wyglądała tak jak w przykładzie zmień wartość `onPlayerEventMessage` na:
```yml
onPlayerJoinEventMessage: '[%time%] **%player%** joined the server.'
```
and for the color,
```yml
onPlayerJoinEventEmbedColor: 3066993
```
Jeśli niechcesz, aby wiadomości były wysyłane  możesz to zmienić w konfiguracji:
```yml
announceChatMessages: false
```

Keep in mind that the embed color has to be a decimal number.
### Translated to polish by [NightOwl](https://nightowl.dev)

