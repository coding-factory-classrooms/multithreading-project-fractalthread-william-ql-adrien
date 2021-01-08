package org.project.fractal;

import java.awt.image.BufferedImage;

class MandelTask implements Runnable {

    private int[] colors;
    private final double xPos;
    private final double yPos;
    private final double zoom;
    private final int chunk;
    private final int chunkSize;
    private final BufferedImage image;
    private final int height;
    private final int width;
    private final int MAX_ITERATIONS;

    public MandelTask(double xPos, double yPos, double zoom, int chunk, int chunkSize, BufferedImage image, int height, int width, int MAX_ITERATIONS, int[] colors) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.zoom = zoom;
        this.chunk = chunk;
        this.chunkSize = chunkSize;
        this.image = image;
        this.height = height;
        this.width = width;
        this.MAX_ITERATIONS = MAX_ITERATIONS;
        this.colors = colors;
    }

    @Override
    public void run() {

        for (int row = chunk; row < (chunk + chunkSize) && row < height; row++) {
   /*         System.out.println("ROW = "+ row);
            System.out.println("(chunk + chunkSize) = "+ (chunk + chunkSize));*/
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
                   // imageData[col][row] = getColor(iteration);
                    image.setRGB(col, row, colors[iteration]);
                } else {
                  //  imageData[col][row] = black;
                    image.setRGB(col, row, 0);
                }
            }
        }
    }
}
