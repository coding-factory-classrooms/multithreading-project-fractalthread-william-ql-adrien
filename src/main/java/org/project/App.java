package org.project;

import org.project.core.Conf;
import org.project.core.Template;
import org.project.fractal.Julia;
import org.project.fractal.Mandelbrot;
import org.project.middlewares.LoggerMiddleware;
import spark.Spark;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.concurrent.Executors;

import static spark.Spark.halt;

public class App {

    private static final boolean DEBUG = true;

    public static void main(String[] args) throws IOException {
        initialize();

        System.out.println("START SERVEUR");
        //vide le cache au lancement
       /* if (new File("images/").exists() && !DEBUG){
            deleteCacheDirectory("images/");
        }*/

        int cores = Runtime.getRuntime().availableProcessors();
        Mandelbrot mandelbrot = new Mandelbrot(1000);
        Julia julia = new Julia();


        Spark.get("/", (req, res) -> {
            return Template.render("home.html", new HashMap<>());
        });
        Spark.get("/image", (req, res) -> {
            return Template.render("image.html", new HashMap<>());
        });
        Spark.get("/images/:type/:height/:width/:x/:y/:zoom", (req, res) -> {
            String type = req.params(":type");
            int height = Integer.parseInt(req.params(":height"));
            int width = Integer.parseInt(req.params(":width"));
            double x = Double.parseDouble(req.params(":x"));
            double y = Double.parseDouble(req.params(":y"));
            double zoom = Double.parseDouble(req.params(":zoom"));

            BufferedImage bufferedImage;
            switch(type) {
                case "mandelbrot":
                    bufferedImage = mandelbrot.generateFractal(height, width, x, y, zoom, Executors.newFixedThreadPool(cores)/*new ThreadPool(cores)*/, true);
                    break;
                case "julia":
                    bufferedImage = julia.generateFractal(height, width, x, y, zoom, Executors.newFixedThreadPool(cores)/*new ThreadPool(cores)*/, true);
                    break;
                default:
                    bufferedImage = mandelbrot.generateFractal(height, width, x, y, zoom, Executors.newFixedThreadPool(cores)/*new ThreadPool(cores)*/, false);
                    break;
            }

            res.raw().setContentType("image/jpeg");

            try (OutputStream out = res.raw().getOutputStream()) {
                ImageIO.write(bufferedImage, "png", out);
            } catch (IIOException e) {
                System.out.println("Error " + e);
            }


            return res;

            //return Template.render("image.html", new HashMap<>());
        });
    }

/*    public static void deleteCacheDirectory(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        Files.walkFileTree(path, new SimpleFileVisitor<>() {
                    // delete directories or folders
                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                        Files.delete(dir);
                        return FileVisitResult.CONTINUE;
                    }
                    // delete files
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                            throws IOException {
                        Files.delete(file);
                        return FileVisitResult.CONTINUE;
                    }
                }
        );
    }*/

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
