package org.project.fractal;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.TimeUnit;


import javax.imageio.ImageIO;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.*;

/**
 * @author ANDRIEU William
 */
public class Mandelbrot  {


    // how long to test for orbit divergence
    private static final int MAX_ITERATIONS = 5000;


    private int height;
    private int width;

    private int black = 0;
    private int[] colors;
    private BufferedImage image;


    /**
     * Construct a new Mandelbrot
     *
     * @param width    the size of the fractal (width pixels).
     * @param height   the size of the fractal (height pixels).
     * @param parallel if true, render in parallel
     */
    public Mandelbrot(int width, int height, boolean parallel) {

        this.width = width;
        this.height = height;

        this.colors = new int[MAX_ITERATIONS];
        for (int i = 0; i < MAX_ITERATIONS; i++) {
            colors[i] = Color.HSBtoRGB(i / 256f, 1, i / (i + 8f));
        }

        this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    }


    public File getImageFromPos(double x, double y, double zoom) {
        // Directory path /x/y/zoom

        String path = "images/" + x + "/" + y + "/" + zoom;
        String filePath = path + "/Mandelbrot.png";

        File file = new File(filePath);
        if (file.exists() && false) {
            System.out.println("FILE ALREADY EXIST");
            return file;
        } else {
            BufferedImage image = calcMandelBrot(x, y, zoom);
            try {
                Files.createDirectories(Paths.get(path));
                ImageIO.write(image, "png", new File(path + "/Mandelbrot.png"));
                System.out.println("FILE CREATED");
                return new File(filePath);
            } catch (IOException e) {
            }
        }
        return null;
    }

    // col => Column in image
    // row => Rows in image
    // zoom  chiffre + grand = dezoom;
    // xPos position dans le fractal
    // yPos position dans le fractal
    private BufferedImage calcMandelBrot(double xPos, double yPos, double zoom) {

        int[][] imageData = new int[width][height];
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        int cores = Runtime.getRuntime().availableProcessors();
        ExecutorService executorService = Executors.newFixedThreadPool(cores);
        System.out.println("Cores :"+ cores);

        long start = System.currentTimeMillis();
        int chunkSize = height / (cores*4);

        for (int chunkX = 0; chunkX < height; chunkX += chunkSize) {
            for (int chunkY = 0; chunkY < width; chunkY += chunkSize) {

            MandelTask mandelTask = new MandelTask(xPos, yPos, zoom, chunkX, chunkY, image, height, width, MAX_ITERATIONS,chunkSize, colors);
            executorService.execute(mandelTask);
            }

        }
        executorService.shutdown();


        try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long elapsed = System.currentTimeMillis() - start;
        System.out.println("Time to generate : " + elapsed + " ms");

        return image;
    }


    private BufferedImage generateChunk(double xPos, double yPos, double zoom, int chunk, int chunkSize, int[][] imageData) {
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int row = 0;  row < height; row++) {
            for (int col = 0; col < width; col++) {

                double c_re = ((col - width / 2) * zoom / width) + xPos;
                double c_im = ((row - height / 2) * zoom / width) + yPos;
                double x = 0, y = 0;
                int iteration = 0;
                while (x * x + y * y < 4 && iteration < MAX_ITERATIONS) {
                    double x_new = x * x - y * y + c_re;
                    y = 2 * x * y + c_im;
                    x = x_new;
                    iteration++;
                }

                if (iteration < MAX_ITERATIONS) {
                    bufferedImage.setRGB(col, row, getColor(iteration));
                } else {
                    bufferedImage.setRGB(col, row, black);
                }
            }
        }
        return bufferedImage;
    }

    private int getColor(int index) {
        return colors[index];
    }



}
