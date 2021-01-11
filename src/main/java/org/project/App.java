package org.project;

import org.project.core.Conf;
import org.project.core.Template;
import org.project.fractal.Mandelbrot;
import org.project.middlewares.LoggerMiddleware;
import spark.Spark;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.OutputStream;
import java.util.HashMap;

import static spark.Spark.halt;

public class App {
    public static void main(String[] args) {
        initialize();

        Mandelbrot mandelbrot = new Mandelbrot(1024, 1024, false);

        mandelbrot.getImageFromPos(1,1,4);

        Spark.get("/", (req, res) -> {
            return Template.render("home.html", new HashMap<>());
        });

        FracalController fracalController = new FracalController(new Fractal());
        Spark.get("/fractal", (req, res) -> fracalController.fractalData(req, res));


        Spark.get("/image", (req, res) -> {
            return Template.render("image.html", new HashMap<>());
        });

        Spark.get("/images/:x/:y/:zoom", (req, res) -> {
            double x = Double.parseDouble(req.params(":x"));
            double y = Double.parseDouble(req.params(":y"));
            double zoom = Double.parseDouble(req.params(":zoom"));


            File file = mandelbrot.getImageFromPos(x, y , zoom);
            res.raw().setContentType("image/jpeg");

            try (OutputStream out = res.raw().getOutputStream()) {
                ImageIO.write(ImageIO.read(file), "png", out);
            }


            return res;

            //return Template.render("image.html", new HashMap<>());
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