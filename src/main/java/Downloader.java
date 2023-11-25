import io.javalin.http.InternalServerErrorResponse;
import io.javalin.util.JavalinLogger;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class Downloader {

    public static String processDownloadRequest(Map<String, List<String>> formParams, String basePath) {
        List<String> links;
        String folder;
        LinkType type;
        try {
            links = Arrays.stream(formParams.get("links").get(0).split("\n")).toList();
            folder = formParams.get("folder").get(0);
            type = LinkType.getByReadableName(formParams.get("type").get(0));
        } catch (Exception e) {
            throw new InternalServerErrorResponse(String.format("L'un des paramètres du formulaire est manquant ou malformé : %s", e.getMessage()));
        }
        Path folderPath = Path.of(basePath, type.readableName, folder);
        links.forEach(link -> {
            try {
                String filename = link.substring(link.lastIndexOf("/") + 1).replace("\r", "");
                JavalinLogger.info("Téléchargement de " + filename + " en cours");
                download(link, folderPath, filename);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        return "Completed";
    }

    private static CompletableFuture<String> download(String url, Path path, String filename) throws IOException {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Instant start = Instant.now();
                Files.createDirectories(path);
                ReadableByteChannel readableByteChannel = Channels.newChannel(new URL(url).openStream());
                FileOutputStream fileOutputStream = new FileOutputStream(path.resolve(filename).toString());
                FileChannel fileChannel = fileOutputStream.getChannel();
                fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
                fileOutputStream.close();
                Instant end = Instant.now();
                JavalinLogger.info(String.format("Téléchargement de %s terminé en %d s !", filename, Duration.between(start, end).getSeconds()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return filename;
        });
    }
}