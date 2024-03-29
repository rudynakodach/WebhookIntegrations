/*
 * WebhookIntegrations
 * Copyright (C) 2023 rudynakodach
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package rudynakodach.github.io.webhookintegrations;

import org.bukkit.plugin.java.JavaPlugin;

import java.net.*;
import java.io.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

public class AutoUpdater {
    static String downloadLinkUrl = "https://raw.githubusercontent.com/rudynakodach/WebhookIntegrations/master/downloadurl";
    static String filenameUrl = "https://raw.githubusercontent.com/rudynakodach/WebhookIntegrations/master/filename";
    static String buildNumberUrl = "https://raw.githubusercontent.com/rudynakodach/WebhookIntegrations/master/buildnumber";
    final JavaPlugin plugin;
    public AutoUpdater(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean Update() {
        try {
            String downloadUrl = getDownloadUrl();
            plugin.getLogger().log(Level.INFO, "Release URL: " + downloadUrl);

            byte[] data = getLatestFile(downloadUrl);
            if(data == null) {
                plugin.getLogger().log(Level.SEVERE, "Received plugin data was null.");
                return false;
            }
            plugin.getLogger().log(Level.INFO, "Plugin data received.");

            String destination = plugin.getServer().getUpdateFolderFile().getAbsolutePath();

            File updateFolder = new File(destination);
            if(!updateFolder.exists())
            {
                if(!updateFolder.mkdir()) {
                    return false;
                }
            }

            String newFilename = getLatestFilename();
            if(newFilename == null) {
                plugin.getLogger().log(Level.SEVERE, "Received filename was null.");
                return false;
            }
            File file = new File(destination, newFilename);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(data);
            fos.close();
            plugin.getLogger().log(Level.INFO, "New version downloaded to " + file.getPath());
            return true;
        } catch (ExecutionException | InterruptedException | IOException e) {
            plugin.getLogger().log(Level.SEVERE,"Update failed: " + e.getMessage());
            return false;
        }
    }

    private String getDownloadUrl() throws ExecutionException, InterruptedException {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            try {
                URL target = new URL(downloadLinkUrl);
                HttpURLConnection connection = (HttpURLConnection) target.openConnection();
                connection.setRequestMethod("GET");

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_NO_CONTENT) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String inputLine;
                    StringBuilder resp = new StringBuilder();
                    while ((inputLine = in.readLine()) != null) {
                        resp.append(inputLine);
                    }
                    in.close();

                    String response = resp.toString();
                    response = response.replaceAll("[\r\n\t]", "");
                    return response;
                }
            } catch (IOException ignored) {}
            return null;
        });
        return future.get();
    }

    private String getLatestFilename() throws ExecutionException, InterruptedException {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            try {
                URL target = new URL(filenameUrl);
                HttpURLConnection connection = (HttpURLConnection) target.openConnection();
                connection.setRequestMethod("GET");

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_NO_CONTENT) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String inputLine;
                    StringBuilder resp = new StringBuilder();
                    while ((inputLine = in.readLine()) != null) {
                        resp.append(inputLine);
                    }
                    in.close();

                    String response = resp.toString();
                    response = response.replaceAll("[\r\n\t]", "");
                    return response;
                }
            } catch (IOException ignored) {}
            return null;
        });

        return future.get();
    }

    private byte[] getLatestFile(String url) throws ExecutionException, InterruptedException {
        CompletableFuture<byte[]> future = CompletableFuture.supplyAsync(() -> {
            try {
                URL fileUrl = new URL(url);
                URLConnection connection = fileUrl.openConnection();
                InputStream inputStream = connection.getInputStream();

                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                int nRead;
                byte[] data = new byte[1024];

                while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, nRead);
                }

                buffer.flush();
                return buffer.toByteArray();
            } catch (IOException ignored) {}
            return null;
        });
        return future.get();
    }

    public int getLatestVersion() throws IOException {
        URL target = new URL(buildNumberUrl);
        HttpURLConnection connection = (HttpURLConnection) target.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        if(responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_NO_CONTENT) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder resp = new StringBuilder();
            while((inputLine = in.readLine()) != null) {
                resp.append(inputLine);
            }
            in.close();

            String response = resp.toString();
            response = response.replaceAll("[\r\n\t]", "");

            return Integer.parseInt(response);
        }
        return -1;
    }
}
