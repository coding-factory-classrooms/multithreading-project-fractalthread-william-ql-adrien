package org.project.fractal;


/*
Copyright (c) 2011, Tom Van Cutsem, Vrije Universiteit Brussel
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of the Vrije Universiteit Brussel nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL Vrije Universiteit Brussel BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.*/

import javax.imageio.ImageIO;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author ANDRIEU William
 */
public class Mandelbrot extends Canvas {

    // size of fractal in pixels (HEIGHT X HEIGHT)
    private static final int HEIGHT = 512;
    // how long to test for orbit divergence
    private static final int MAX_ITERATIONS = 5000;
    // maximum grid size to process sequentially
    private static final int SEQ_CUTOFF = 64;


    private int height;
    private int width;

    private int black = 0;
    private int[] colors;
    private BufferedImage image;


    /**
     * Construct a new Mandelbrot
     * The constructor will calculate the fractal (either sequentially
     * or in parallel), then store the result in an {@link java.awt.Image}
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
            calcMandelBrot(x, y, zoom);
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
    private int[][] calcMandelBrot(double xPos, double yPos, double zoom) {

        int[][] imageData = new int[width][height];
        // BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
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
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long elapsed = System.currentTimeMillis() - start;
        System.out.println("Time to generate : " + elapsed + " ms");



        return imageData;
    }


    private void generateChunk(double xPos, double yPos, double zoom, int chunk, int chunkSize, int[][] imageData) {
        for (int row = chunk; row < (chunk + chunkSize) && row < height; row++) {
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
                    imageData[col][row] = getColor(iteration);
                    image.setRGB(col, row, getColor(iteration));
                } else {
                    imageData[col][row] = black;
                    image.setRGB(col, row, black);
                }
            }
        }
    }

    private int getColor(int index) {
        return colors[index];
    }


    /**
     * Divide the grid into four equally-sized subgrids until they
     * are small enough to be drawn sequentially.
     */
    private class FractalTask extends RecursiveAction {
        final int srcx;
        final int srcy;
        final int size;

        public FractalTask(int sx, int sy, int siz) {
            srcx = sx;
            srcy = sy;
            size = siz;
        }

        @Override
        protected void compute() {
            if (size < SEQ_CUTOFF) {
                calcMandelBrot(0, 0, 4);
            } else {
                FractalTask ul = new FractalTask(srcx, srcy, size / 2);
                FractalTask ur = new FractalTask(srcx + size / 2, srcy, size / 2);
                FractalTask ll = new FractalTask(srcx, srcy + size / 2, size / 2);
                FractalTask lr = new FractalTask(srcx + size / 2, srcy + size / 2, size / 2);
                // forks and immediately joins the four subtasks
                invokeAll(ul, ur, ll, lr);
            }
        }
    }


}
