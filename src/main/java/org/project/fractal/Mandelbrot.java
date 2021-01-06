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
import java.awt.Image;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

/**
 * Demo of using Fork/Join parallelism to speed up the rendering of the
 * Mandelbrot fractal. The fractal is shown centered around the origin
 * of the Complex plane with x and y coordinates in the interval [-2, 2].
 *
 * @author tvcutsem
 */
public class Mandelbrot extends Canvas {

    // size of fractal in pixels (HEIGHT X HEIGHT)
    private static final int HEIGHT = 512;
    // how long to test for orbit divergence
    private static final int NUM_ITERATIONS = 50;
    // maximum grid size to process sequentially
    private static final int SEQ_CUTOFF = 64;

    private int colorscheme[];

    // 2-dimensional array of colors stored in packed ARGB format
    private int[] fractal;

    private int height;
    private boolean optDrawGrid;
    private Image img;
    private String msg;

    private ForkJoinPool fjPool = new ForkJoinPool();

    /**
     * Construct a new Mandelbrot canvas.
     * The constructor will calculate the fractal (either sequentially
     * or in parallel), then store the result in an {@link java.awt.Image}
     * for faster drawing in its {@link #paint(Graphics)} method.
     *
     * @param height      the size of the fractal (height x height pixels).
     * @param optParallel if true, render in parallel
     * @param optDrawGrid if true, render the grid of leaf task pixel areas
     */
    public Mandelbrot(int height, boolean optParallel, boolean optDrawGrid) {
        this.optDrawGrid = optDrawGrid;
/*
        long start = System.currentTimeMillis();
        if (optParallel) {
            // parallel calculation through Fork/Join
            fjPool.invoke(new FractalTask(0, 0, height));
        } else {
            // sequential calculation by the main Thread
            calcMandelBrot(0, 0, height, height);
        }
        long end = System.currentTimeMillis();
        msg = (optParallel ? "parallel" : "sequential") +
                " done in " + (end - start) + "ms.";
*/

        //double x = 0;
        //double y = 0;
        double zoom = 4;
        for (double x = 0; x < 10; x++) {
            for (double y = 0;y < 10; y++) {
                System.out.println("file : " + getImageFromPos( x,  y,  zoom));
            }
        }
    }


    private File getImageFromPos(double x, double y, double zoom){
        // Directory path /x/y/zoom

        String path = "images/"+x+"/"+y+"/"+zoom;
        String filePath = path+"/Mandelbrot.png";

        File file = new File(filePath);
        if (file.exists()){
            System.out.println("FILE ALREADY EXIST");
            return file;
        } else {

            BufferedImage image = calcMandelBrot(x, y, zoom);
            try {

                Files.createDirectories(Paths.get(path));
                ImageIO.write(image, "png", new File(path + "/Mandelbrot.png"));
                System.out.println("FILE CREATED");
                return new File(filePath);
            } catch (IOException e) {}
        }
        return null;
    }

    /**
     * Draws part of the mandelbrot fractal.
     * <p>
     * This method calculates the colors of pixels in the square:
     * <p>
     * (srcx, srcy)           (srcx+size, srcy)
     * +--------------------------+
     * |                          |
     * |                          |
     * |                          |
     * |                          |
     * |                          |
     * |                          |
     * |                          |
     * +--------------------------+
     * (srcx, srcy+size)      (srcx+size, srcy + size)
     * @return
     */
   /* private void calcMandelBrot(int srcx, int srcy, int size, int height) {
       double x, y, t, cx, cy;
        int k;

        int width = 1000;
        height = 1000;
        int max = 1000;


        // SRCX => Column in image
        // SRCY => Rows in image
        // zoom  chiffre + grand = dezoom;
        // xPos position dans le fractal
        // yPos position dans le fractal
        double xPos = 1;
        double yPos = 1;
        double zoom = 4.0;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        // loop over specified rectangle grid
        // px = x dans l'image
        // py = y dans l'image
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                double c_re = ((col - width/2)*zoom/width) + xPos;
                double c_im = ((row - height/2)*zoom/width) + yPos;



                x = 0;
                y = 0;
                // test for divergence
                for (k = 0; k < NUM_ITERATIONS; k++) {
                    t = x * x - y * y + cx;
                    y = 2 * x * y + cy;
                    x = t;
                    if (x * x + y * y > 4) break;
                }
                image.setRGB(px, py, colorscheme[k]);
                //fractal[px + height * py] = colorscheme[k];
            }

        }
       if (true) {


            try {
                ImageIO.write(image, "png", new File("images/MandelbrotNEW.png"));
            } catch (IOException e) {
                System.out.println("Error " + e);
            }

        }
        // paint grid boundaries
        if (optDrawGrid) {
            drawGrid(row, col, size, Color.BLACK.getRGB());
        }*//*


    }*/

    private BufferedImage calcMandelBrot(double xPos, double yPos, double zoom){

        int width = 1000;
        height = 1000;
        int max = 1000;


        // col => Column in image
        // row => Rows in image
        // zoom  chiffre + grand = dezoom;
        // xPos position dans le fractal
        // yPos position dans le fractal


        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        int black = 0;
        int[] colors = new int[max];
        for (int i = 0; i<max; i++) {
            colors[i] = Color.HSBtoRGB(i/256f, 1, i/(i+8f));
        }

        int i = 0;
        long meanTime =0;

            long start = System.currentTimeMillis();
            for (int row = 0; row < height; row++) {
                for (int col = 0; col < width; col++) {
                    double c_re = ((col - width/2)*zoom/width) + xPos;
                    double c_im = ((row - height/2)*zoom/width) + yPos;
                    double x = 0, y = 0;
                    int iteration = 0;
                    while (x*x+y*y < 4 && iteration < max) {
                        double x_new = x*x-y*y+c_re;
                        y = 2*x*y+c_im;
                        x = x_new;
                        iteration++;
                    }
                    if (iteration < max) image.setRGB(col, row, colors[iteration]);
                    else image.setRGB(col, row, black);
                }
            }
            i++;
            long elapsed = System.currentTimeMillis() - start;
            meanTime += elapsed/i;

        System.out.println(meanTime + " ms");

        return image;
    }


    /**
     * Draw the rectangular outline of the grid to show the
     * subdivision of the canvas into grids processed in parallel.
     */
    private void drawGrid(int x, int y, int size, int color) {
        for (int i = 0; i < size; i++) {
            fractal[x + i + height * (y)] = color;
            fractal[x + i + height * (y + size - 1)] = color;
        }
        for (int j = 0; j < size; j++) {
            fractal[x + height * (y + j)] = color;
            fractal[x + size - 1 + height * (y + j)] = color;
        }
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

    @Override
    public void paint(Graphics g) {
        // draw the fractal from the stored image
        g.drawImage(this.img, 0, 0, null);
        // draw the message text in the lower-right-hand corner
        byte[] data = this.msg.getBytes();
        g.drawBytes(
                data,
                0,
                data.length,
                getWidth() - (data.length) * 8,
                getHeight() - 20);
    }

    /**
     * Auxiliary function that converts an array of pixels into a BufferedImage.
     * This is used to be able to quickly draw the fractal onto the canvas using
     * native code, instead of us having to manually plot each pixel to the canvas.
     */
    private static Image getImageFromArray(int[] pixels, int width, int height) {
        // RGBdefault expects 0x__RRGGBB packed pixels
        ColorModel cm = DirectColorModel.getRGBdefault();
        SampleModel sampleModel = cm.createCompatibleSampleModel(width, height);
        DataBuffer db = new DataBufferInt(pixels, height, 0);
        WritableRaster raster = Raster.createWritableRaster(sampleModel, db, null);
        BufferedImage image = new BufferedImage(cm, raster, false, null);
        return image;
    }

    /**
     * Supported command-line options:
     * -p : render in parallel (default: sequential)
     * -g : draw grid of pixels drawn by leaf tasks (default: off)
     */
    public static void main(String args[]) {
        boolean optParallel = false;
        boolean optDrawGrid = false;
        for (String arg : args) {
            if (arg.equals("-p")) {
                optParallel = true;
            } else if (arg.equals("-g")) {
                optDrawGrid = true;
            } else {
                System.err.println("unknown option: " + arg);
            }
        }

        //Frame f = new Frame();
        Mandelbrot canvas = new Mandelbrot(HEIGHT, optParallel, optDrawGrid);
/*        f.setSize(HEIGHT, HEIGHT);
        f.add(canvas);
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        f.setVisible(true);*/
    }
}