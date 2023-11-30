package main;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.staticfiles.Location;
import io.javalin.rendering.JavalinRenderer;
import io.javalin.rendering.template.JavalinMustache;
import static io.javalin.apibuilder.ApiBuilder.*;

import java.util.concurrent.CompletableFuture;

import static io.javalin.apibuilder.ApiBuilder.path;
import static io.javalin.rendering.template.TemplateUtil.model;

public class Main {

	private static final String DOWNLOAD_OK_DIV = """
			    <div class="notification is-success mt-6">
			      Téléchargement en cours ! 👌\s
			    </div>
			""";
	private static String baseFolder = null;

	public static void main(String[] args) {
		parseBaseFolder(args);

		Javalin app = Javalin.create().start(7070);

		Javalin.create(config -> config.staticFiles.add(staticFiles -> {
			staticFiles.directory = "/public";              // the directory where your files are located
			staticFiles.location = Location.CLASSPATH;      // Location.CLASSPATH (jar) or Location.EXTERNAL (file system)
		}));

		JavalinRenderer.register(new JavalinMustache(), ".hbs");

		app.routes(() -> path("links-downloader", () -> {
			get(ctx -> ctx.render("index.hbs"));
			get("success", ctx -> ctx.render("index.hbs", model("message", DOWNLOAD_OK_DIV)));
			post("download", ctx -> {
				ctx.future(() -> downloadAsync(ctx));
				ctx.redirect("success");
			});
		}));
	}

	private static CompletableFuture<String> downloadAsync(Context ctx) {
		return CompletableFuture.supplyAsync(() -> Downloader.processDownloadRequest(ctx.formParamMap(), baseFolder));
	}

	private static void parseBaseFolder(String[] args) {
		if (args.length != 1) {
			throw new RuntimeException("Le programme doit avoir un seul argument : le chemin d'accès au dossier de téléchargement");
		} else {
			baseFolder = args[0];
		}
	}

}
