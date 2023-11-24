import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.staticfiles.Location;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class Main {
    private static String BASE_FOLDER = null;

    public static void main(String[] args) {
        parseBaseFolder(args);

        Javalin app = Javalin.create(config ->
                        config.staticFiles.add("/view", Location.CLASSPATH))
                .start(7070);

        app.post("/download", ctx -> {
            ctx.future(() -> downloadAsync(ctx));
            ctx.redirect("/");
        });
    }

    private static CompletableFuture<String> downloadAsync(Context ctx) {
        return CompletableFuture.supplyAsync(() -> Downloader.processDownloadRequest(ctx.formParamMap(), BASE_FOLDER));
    }

    private static void parseBaseFolder(String @NotNull [] args) {
        if (args.length != 1) {
            throw new RuntimeException("Le programme doit avoir un seul argument : le chemin d'accès au dossier de téléchargement");
        } else {
            BASE_FOLDER = args[0];
        }
    }
}
