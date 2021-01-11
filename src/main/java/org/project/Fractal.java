package org.project;

import java.util.ArrayList;
import java.util.List;

public class Fractal {

    private int iterations = 1000;
    
    private int width = 900;
    private int height = 600;

    public int getWidth() {
        return width;
    }
    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }
    public void setHeight(int height) {
        this.height = height;
    }

    FractalData fractalData;
    public FractalData getFractalData() {
        return fractalData;
    }

    public Fractal() {
        fractalData = new FractalData();
    }

    public int[] getFractal() {
            int[] fractalValues = new int[width*height*4];

        for (var ix = 0; ix < width; ++ix) {
            for (var iy = 0; iy < height; ++iy) {
                var x = fractalData.xmin + (fractalData.xmax - fractalData.xmin) * ix / (width - 1) * fractalData.zoom;
                var y = fractalData.ymin + (fractalData.ymax - fractalData.ymin) * iy / (height - 1) * fractalData.zoom;
                var i = mandelIter(x, y, iterations);
                var ppos = 4 * (width * iy + ix);

                if (i > iterations) {
                    fractalValues[ppos] = 0;
                    fractalValues[ppos + 1] = 0;
                    fractalValues[ppos + 2] = 0;
                } else {
                    var c = 3 * Math.log(i) / Math.log(iterations - 1.0);
                    if (c < 1) {
                        fractalValues[ppos] = (int)Math.round(255 * c);
                        fractalValues[ppos + 1] = 0;
                        fractalValues[ppos + 2] = 0;
                    }
                    else if (c < 2) {
                        fractalValues[ppos] = 255;
                        fractalValues[ppos + 1] = (int)Math.round(255 * (c - 1));
                        fractalValues[ppos + 2] = 0;
                    } else {
                        fractalValues[ppos] = 255;
                        fractalValues[ppos + 1] = 255;
                        fractalValues[ppos + 2] = (int)Math.round(255 * (c - 2));
                    }
                }
                fractalValues[ppos + 3] = 255;
            }
        }
        return fractalValues;
    }

    private int mandelIter(int cx, int cy, int maxIteration) {
        double x = 0.0;
        double y = 0.0;
        double xx = 0;
        double yy = 0;
        double xy = 0;

        var i = maxIteration -1;
        while (i != 0 && xx + yy <= 4) {
            xy = x * y;
            xx = x * x;
            yy = y * y;
            x = xx - yy + cx;
            y = xy + xy + cy;
            i--;
        }
        return maxIteration - i;
    }

    class  FractalData{
        private int xmin = -2;
        private int xmax = 1;
        private int ymin = -1;
        private int ymax = 1;
        private int zoom = 1;

        public int getXmin() {
            return xmin;
        }
        public int getXmax() {
            return xmax;
        }
        public int getYmin() {
            return ymin;
        }
        public int getYmax() {
            return ymax;
        }

        public int getZoom() {
            return zoom;
        }
        public void setZoom(int zoom) {
            this.zoom = zoom;
        }

        public void setMinMax(int xmin, int xmax, int ymin, int ymax) {
            this.xmin = xmin;
            this.xmax = xmax;
            this.ymin = ymin;
            this.ymax = ymax;
        }
    }
}