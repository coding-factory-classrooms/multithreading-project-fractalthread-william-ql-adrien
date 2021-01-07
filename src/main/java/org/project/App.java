package org.project;

import org.project.core.Conf;
import org.project.core.Template;
import org.project.fractal.Mandelbrot;
import org.project.middlewares.LoggerMiddleware;
import spark.Spark;

import javax.imageio.ImageIO;
import java.io.File;
import java.util.HashMap;

public class App {
    public static void main(String[] args) {
        initialize();

        Mandelbrot mandelbrot = new Mandelbrot(1000, 1000, false);

        mandelbrot.getImageFromPos(1,1,4);

        Spark.get("/", (req, res) -> {
            return Template.render("home.html", new HashMap<>());
        });
        Spark.get("/", (req, res) -> {
            return Template.render("home.html", new HashMap<>());
        });
    }

    static void initialize() {
        Template.initialize();

        // Display exceptions in logs
        Spark.exception(Exception.class, (e, req, res) -> e.printStackTrace());

        // Serve static files (img/css/js)
        Spark.staticFiles.externalLocation(Conf.STATIC_DIR.getPath());

        // Configure server port
        Spark.port(Conf.HTTP_PORT);
        final LoggerMiddleware log = new LoggerMiddleware();
        Spark.before(log::process);
    }
}
