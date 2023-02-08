package rudynakodach.github.io.webhookintegrations;

import okhttp3.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

public class AutoUpdater {
    static String linkUrl = "https://raw.githubusercontent.com/rudynakodach/WebhookIntegrations/master/downloadurl";
    static String filenameUrl = "https://raw.githubusercontent.com/rudynakodach/WebhookIntegrations/master/filename";
    final JavaPlugin plugin;
    public AutoUpdater(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    void Update() {
        try {
            String downloadUrl = getDownloadUrl();
            plugin.getLogger().log(Level.INFO, "Release URL: " + downloadUrl);

            byte[] data = getLatestFile(downloadUrl);
            plugin.getLogger().log(Level.INFO, "Plugin data received.");

            String destination = plugin.getServer().getUpdateFolderFile().getAbsolutePath();

            File updateFolder = new File(destination);
            if(!updateFolder.exists())
            {
                updateFolder.mkdir();
            }

            File file = new File(destination, getLatestFilename(filenameUrl));
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(data);
            fos.close();
            plugin.getLogger().log(Level.INFO, "New version downloaded to " + file.getPath());

        } catch (ExecutionException | InterruptedException | IOException e) {plugin.getLogger().log(Level.SEVERE,"Update failed: " + e.getMessage());}
    }

    private String getDownloadUrl() throws ExecutionException, InterruptedException {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(linkUrl)
                    .get()
                    .build();
            try {
                Response response = client.newCall(request).execute();
                if(response.isSuccessful()) {
                    String resp = response.body().string();
                    resp = resp.replaceAll("[\r\n\t]", "");
                    return resp.trim();
                }
                response.close();
            }
            catch (IOException ignored) {}
            return "";
        });
        return future.get();
    }

    public String getLatestFilename(String url) throws ExecutionException, InterruptedException {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                String resp = response.body().string();
                resp = resp.replaceAll("[\r\n\t]", "");
                return resp.trim();
            } catch (IOException | IllegalStateException e) {
                plugin.getLogger().log(Level.SEVERE, "Getting filename failed: " + e.getMessage());
                return "";
            }
        });

        return future.get();
    }

    public byte[] getLatestFile(String url) throws ExecutionException, InterruptedException {
        CompletableFuture<byte[]> future = CompletableFuture.supplyAsync(() -> {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                return response.body().bytes();
            }
            catch (IOException ignored) {}
            return null;
        });
        return future.get();
    }
}
