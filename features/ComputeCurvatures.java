/**
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation
 * (http://www.gnu.org/licenses/gpl.txt ). This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public
 * License for more details.
 *
 * Based on Stephan Preibisch
 * http://www.longair.net/edinburgh/imagej/tubeness/"
 */

package Tubeness.features;

import ij.IJ;
import ij.process.ImageProcessor;
import ij.ImagePlus;
import ij.process.ByteProcessor;
import ij.process.ShortProcessor;
import ij.process.FloatProcessor;
import ij.measure.Calibration;
import Tubeness.math3d.Eigensystem3x3Double;
import Tubeness.math3d.Eigensystem3x3Float;
import Tubeness.math3d.Eigensystem2x2Double;
import Tubeness.math3d.Eigensystem2x2Float;
import Tubeness.math3d.JacobiDouble;
import Tubeness.math3d.JacobiFloat;


public class ComputeCurvatures implements Runnable {

    private FloatArray data;
    private double[][] hessianMatrix;
    private float[] eigenValues = new float[3];

    private double min = Double.MAX_VALUE, max = Double.MIN_VALUE;
    protected ImagePlus imp;
    protected double sigma;
    protected boolean useCalibration;
    protected GaussianGenerationCallback callback;


    public ComputeCurvatures() {
    }

    public ComputeCurvatures(ImagePlus imp,
            double sigma,
            GaussianGenerationCallback callback,
            boolean useCalibration) {
        this.imp = imp;
        this.sigma = sigma;
        this.callback = callback;
        this.useCalibration = useCalibration;
    }

    private boolean cancelGeneration = false;

    public void cancelGaussianGeneration() {
        cancelGeneration = true;
    }

    public void run() {
        if (imp == null) {
            IJ.error("BUG: imp should not be null - are you using the right constructor?");
            return;
        }
        setup();
    }

    public void setup() {
        try {
            data = ImageToFloatArray(imp.getProcessor());

            if (data == null) {
                return;
            }

            boolean computeGauss = true;
            boolean showGauss = true;

            Calibration calibration = imp.getCalibration();

            data = computeGaussianFastMirror((FloatArray2D) data, (float) sigma, callback, useCalibration ? calibration : null);
            if (data == null) {
                if (callback != null) {
                    callback.proportionDone(-1);
                }
                return;
            }
            if (showGauss) {
                FloatArrayToImagePlus((FloatArray2D) data, "Gauss image", 0, 255).show();
            }

        } catch (OutOfMemoryError e) {

            long requiredMiB = (imp.getWidth()
                    * imp.getHeight()
                    * imp.getStackSize() * 4) / (1024 * 1024);

            throw new UnsupportedOperationException("Out of memory when calculating the Gaussian "
                    + "convolution of the image (requires "
                    + requiredMiB + "MiB");
        }
    }

    public void setData(ImagePlus imp) {
        data = ImageToFloatArray(imp.getProcessor());
        if (data == null) {
            System.err.println("Data = null!!!!");
        }
    }

    public void setSigma(double sigma) {
        this.sigma = sigma;
    }

    public FloatArray getData() {
        return data;
    }

    public boolean hessianEigenvaluesAtPoint2D(int x,
            int y,
            boolean orderOnAbsoluteSize,
            float[] result, /* should be 2 elements */
            boolean normalize,
            boolean fixUp,
            float sepX,
            float sepY) {

        FloatArray2D data2D = (FloatArray2D) data;

        if (fixUp) {

            if (x == 0) {
                x = 1;
            }
            if (x == (data2D.width - 1)) {
                x = data2D.width - 2;
            }

            if (y == 0) {
                y = 1;
            }
            if (y == (data2D.height - 1)) {
                y = data2D.height - 2;
            }
        }

        float[][] hessianMatrix = computeHessianMatrix2DFloat(data2D, x, y, sigma, sepX, sepY);
        float[] eigenValues = computeEigenValues(hessianMatrix);

        if (eigenValues == null) {
            return false;
        }

        float e0 = eigenValues[0];
        float e1 = eigenValues[1];

        float e0c = orderOnAbsoluteSize ? Math.abs(e0) : e0;
        float e1c = orderOnAbsoluteSize ? Math.abs(e1) : e1;

        if (e0c <= e1c) {
            result[0] = e0;
            result[1] = e1;
        } else {
            result[0] = e1;
            result[1] = e0;
        }
        if (normalize) {
            float divideBy = Math.abs(result[1]);
            result[0] /= divideBy;
            result[1] /= divideBy;
        }
        return true;
    }


    public float [] doubleToFloat (double [] a){
        float [] a2 = new float [a.length];

        for (int r=0; r<a.length; r++) {
            a2 [r] = (float) a[r];
        }
        return a2;
    }
    public double [][] floatToDouble (float [][] a){
        double [][] a2 = new double [a.length][a[0].length];

        for (int r=0; r<a.length; r++) {
            for (int c=0; c<a[0].length; c++) {
                a2 [r][c] = (double) a[r][c];
            }
        }
        return a2;
    }

    public boolean hessianEigenvaluesAtPoint2D(int x,
            int y,
            boolean orderOnAbsoluteSize,
            double[] result, /* should be 2 elements */
            boolean normalize,
            boolean fixUp,
            float sepX,
            float sepY) {

        FloatArray2D data2D = (FloatArray2D) data;

        if (fixUp) {

            if (x == 0) {
                x = 1;
            }
            if (x == (data2D.width - 1)) {
                x = data2D.width - 2;
            }

            if (y == 0) {
                y = 1;
            }
            if (y == (data2D.height - 1)) {
                y = data2D.height - 2;
            }
        }

        double[][] hessianMatrix = computeHessianMatrix2DDouble(data2D, x, y, sigma, sepX, sepY);
        double[] eigenValues = computeEigenValues(hessianMatrix);
        if (eigenValues == null) {
            return false;
        }


        double e0 = eigenValues[0];
        double e1 = eigenValues[1];

        double e0c = orderOnAbsoluteSize ? Math.abs(e0) : e0;
        double e1c = orderOnAbsoluteSize ? Math.abs(e1) : e1;

        if (e0c <= e1c) {
            result[0] = e0;
            result[1] = e1;
        } else {
            result[0] = e1;
            result[1] = e0;
        }
        if (normalize) {
            double divideBy = Math.abs(result[1]);
            result[0] /= divideBy;
            result[1] /= divideBy;
        }
        return true;
    }

    public static ImagePlus FloatArrayToImagePlus(FloatArray2D image, String name, float min, float max) {
        ImagePlus imp = IJ.createImage(name, "32-Bit Black", image.width, image.height, 1);
        FloatProcessor ip = (FloatProcessor) imp.getProcessor();
        FloatArrayToFloatProcessor(ip, image);

        if (min == max) {
            ip.resetMinAndMax();
        } else {
            ip.setMinAndMax(min, max);
        }

        imp.updateAndDraw();

        return imp;
    }

    public static void FloatArrayToFloatProcessor(ImageProcessor ip, FloatArray2D pixels) {
        float[] data = new float[pixels.width * pixels.height];

        int count = 0;
        for (int y = 0; y < pixels.height; y++) {
            for (int x = 0; x < pixels.width; x++) {
                data[count] = pixels.data[count++];
            }
        }

        ip.setPixels(data);
        ip.resetMinAndMax();
    }

    public double[] computeEigenValues(double[][] matrix) {

        if (matrix.length == 3 && matrix[0].length == 3) {
            Eigensystem3x3Double e = new Eigensystem3x3Double(matrix);
            boolean result = e.findEvalues();
            return result ? e.getEvaluesCopy() : null;
        } else if (matrix.length == 2 && matrix[0].length == 2) {
            Eigensystem2x2Double e = new Eigensystem2x2Double(matrix);
            boolean result = e.findEvalues();
            return result ? e.getEvaluesCopy() : null;
        } else {
            JacobiDouble jc = new JacobiDouble(matrix, 50);
            return jc.getEigenValues();
        }
    }

    public float[] computeEigenValues(float[][] matrix) {

        if (matrix.length == 3 && matrix[0].length == 3) {
            Eigensystem3x3Float e = new Eigensystem3x3Float(matrix);
            boolean result = e.findEvalues();
            return result ? e.getEvaluesCopy() : null;
        } else if (matrix.length == 2 && matrix[0].length == 2) {
            Eigensystem2x2Float e = new Eigensystem2x2Float(matrix);
            boolean result = e.findEvalues();
            return result ? e.getEvaluesCopy() : null;
        } else {
            JacobiFloat jc = new JacobiFloat(matrix, 50);
            return jc.getEigenValues();
        }
    }

    public double[][] computeHessianMatrix2DDouble(FloatArray2D laPlace, int x, int y, double sigma, float sepX, float sepY) {
        if (laPlace == null) {
            laPlace = (FloatArray2D) data;
        }

        double[][] hessianMatrix = new double[2][2]; 

        double temp = 2 * laPlace.get(x, y);

        hessianMatrix[0][0] = laPlace.get(x + 1, y) - temp + laPlace.get(x - 1, y);
        hessianMatrix[1][1] = laPlace.get(x, y + 1) - temp + laPlace.get(x, y - 1);
        hessianMatrix[0][1] = hessianMatrix[1][0] =
                ((laPlace.get(x + 1, y + 1) - laPlace.get(x - 1, y + 1)) / 2
                - (laPlace.get(x + 1, y - 1) - laPlace.get(x - 1, y - 1)) / 2) / 2;

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                hessianMatrix[i][j] *= (sigma * sigma);
            }
        }

        return hessianMatrix;
    }

    public float[][] computeHessianMatrix2DFloat(FloatArray2D laPlace, int x, int y, double sigma, float sepX, float sepY) {
        if (laPlace == null) {
            laPlace = (FloatArray2D) data;
        }

        float[][] hessianMatrix = new float[2][2];

        float temp = 2 * laPlace.get(x, y);

        hessianMatrix[0][0] = laPlace.get(x + 1, y) - temp + laPlace.get(x - 1, y);
        hessianMatrix[1][1] = laPlace.get(x, y + 1) - temp + laPlace.get(x, y - 1);
        hessianMatrix[0][1] = hessianMatrix[1][0] =
                ((laPlace.get(x + 1, y + 1) - laPlace.get(x - 1, y + 1)) / 2
                - (laPlace.get(x + 1, y - 1) - laPlace.get(x - 1, y - 1)) / 2) / 2;
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                hessianMatrix[i][j] *= (sigma * sigma);
            }
        }
        return hessianMatrix;
    }

    public static float[] createGaussianKernel1D(float sigma, boolean normalize) {
        float[] gaussianKernel;

        if (sigma <= 0) {

            gaussianKernel = new float[3];
            gaussianKernel[1] = 1;

        } else {

            int size = Math.max(3, (int) (2 * (int) (3 * sigma + 0.5) + 1));

            float two_sq_sigma = 2 * sigma * sigma;
            gaussianKernel = new float[size];

            for (int x = size / 2; x >= 0; --x) {

                float val = (float) Math.exp(-(float) (x * x) / two_sq_sigma);

                gaussianKernel[size / 2 - x] = val;
                gaussianKernel[size / 2 + x] = val;
            }
        }

        if (normalize) {
            float sum = 0;

            for (int i = 0; i < gaussianKernel.length; i++) {
                sum += gaussianKernel[i];
            }

            for (int i = 0; i < gaussianKernel.length; i++) {
                gaussianKernel[i] /= sum;
            }
        }
        return gaussianKernel;
    }


    public FloatArray2D computeGaussianFastMirror(FloatArray2D input, float sigma, GaussianGenerationCallback callback, Calibration calibration) {
        FloatArray2D output = new FloatArray2D(input.width, input.height);

        float avg;
        float kernelsumX = 0, kernelsumY = 0, kernelsumZ = 0;

        float pixelWidth = 1, pixelHeight = 1, pixelDepth = 1;

        if (calibration != null) {
            pixelWidth = (float) calibration.pixelWidth;
            pixelHeight = (float) calibration.pixelHeight;
            pixelDepth = (float) calibration.pixelDepth;
        }

        float[] kernelX = createGaussianKernel1D(sigma / pixelWidth, true);
        float[] kernelY = createGaussianKernel1D(sigma / pixelHeight, true);
        int filterSizeX = kernelX.length;
        int filterSizeY = kernelY.length;

        for (int i = 0; i < kernelX.length; i++) {
            kernelsumX += kernelX[i];
        }
        for (int i = 0; i < kernelY.length; i++) {
            kernelsumY += kernelY[i];
        }

        double totalPoints = input.width * input.height * 2;
        long pointsDone = 0;

        for (int x = 0; x < input.width; x++) {
            if (cancelGeneration) {
                return null;
            }
            for (int y = 0; y < input.height; y++) {
                avg = 0;

                if (x - filterSizeX / 2 >= 0 && x + filterSizeX / 2 < input.width) {
                    for (int f = -filterSizeX / 2; f <= filterSizeX / 2; f++) {
                        avg += input.get(x + f, y) * kernelX[f + filterSizeX / 2];
                    }
                } else {
                    for (int f = -filterSizeX / 2; f <= filterSizeX / 2; f++) {
                        avg += input.getMirror(x + f, y) * kernelX[f + filterSizeX / 2];
                    }
                }

                output.set(avg / kernelsumX, x, y);

            }
            pointsDone += input.height;
            if (callback != null) {
                callback.proportionDone(pointsDone / totalPoints);
            }
        }

        for (int x = 0; x < input.width; x++) {
            if (cancelGeneration) {
                return null;
            }
            {
                float[] temp = new float[input.height];

                for (int y = 0; y < input.height; y++) {
                    avg = 0;

                    if (y - filterSizeY / 2 >= 0 && y + filterSizeY / 2 < input.height) {
                        for (int f = -filterSizeY / 2; f <= filterSizeY / 2; f++) {
                            avg += output.get(x, y + f) * kernelY[f + filterSizeY / 2];
                        }
                    } else {
                        for (int f = -filterSizeY / 2; f <= filterSizeY / 2; f++) {
                            avg += output.getMirror(x, y + f) * kernelY[f + filterSizeY / 2];
                        }
                    }

                    temp[y] = avg / kernelsumY;
                }

                for (int y = 0; y < input.height; y++) {
                    output.set(temp[y], x, y);
                }

                pointsDone += input.height;
                if (callback != null) {
                    callback.proportionDone(pointsDone / totalPoints);
                }
            }
        }

        if (callback != null) {
            callback.proportionDone(1.0);
        }

        return output;
    }


    public FloatArray2D ImageToFloatArray(ImageProcessor ip) {
        FloatArray2D image;
        Object pixelArray = ip.getPixels();
        int count = 0;

        if (ip instanceof ByteProcessor) {
            image = new FloatArray2D(ip.getWidth(), ip.getHeight());
            byte[] pixels = (byte[]) pixelArray;

            for (int y = 0; y < ip.getHeight(); y++) {
                for (int x = 0; x < ip.getWidth(); x++) {
                    image.data[count] = pixels[count++] & 0xff;
                }
            }
        } else if (ip instanceof ShortProcessor) {
            image = new FloatArray2D(ip.getWidth(), ip.getHeight());
            short[] pixels = (short[]) pixelArray;

            for (int y = 0; y < ip.getHeight(); y++) {
                for (int x = 0; x < ip.getWidth(); x++) {
                    image.data[count] = pixels[count++] & 0xffff;
                }
            }
        } else if (ip instanceof FloatProcessor) {
            image = new FloatArray2D(ip.getWidth(), ip.getHeight());
            float[] pixels = (float[]) pixelArray;

            for (int y = 0; y < ip.getHeight(); y++) {
                for (int x = 0; x < ip.getWidth(); x++) {
                    image.data[count] = pixels[count++];
                }
            }
        } else //RGB
        {
            System.err.println("RGB images not supported");
            image = null;
        }
        return image;
    }

    public abstract class FloatArray {

        public float data[] = null;

        public abstract FloatArray clone();
    }

    public class FloatArray2D extends FloatArray {

        public float data[] = null;
        public int width = 0;
        public int height = 0;

        public FloatArray2D(int width, int height) {
            data = new float[width * height];
            this.width = width;
            this.height = height;
        }

        public FloatArray2D(float[] data, int width, int height) {
            this.data = data;
            this.width = width;
            this.height = height;
        }

        public FloatArray2D clone() {
            FloatArray2D clone = new FloatArray2D(width, height);
            System.arraycopy(this.data, 0, clone.data, 0, this.data.length);
            return clone;
        }

        public int getPos(int x, int y) {
            return x + width * y;
        }

        public float get(int x, int y) {
            return data[getPos(x, y)];
        }

        public float getMirror(int x, int y) {
            if (x >= width) {
                x = width - (x - width + 2);
            }

            if (y >= height) {
                y = height - (y - height + 2);
            }

            if (x < 0) {
                int tmp = 0;
                int dir = 1;

                while (x < 0) {
                    tmp += dir;
                    if (tmp == width - 1 || tmp == 0) {
                        dir *= -1;
                    }
                    x++;
                }
                x = tmp;
            }

            if (y < 0) {
                int tmp = 0;
                int dir = 1;

                while (y < 0) {
                    tmp += dir;
                    if (tmp == height - 1 || tmp == 0) {
                        dir *= -1;
                    }
                    y++;
                }
                y = tmp;
            }

            return data[getPos(x, y)];
        }

        public float getZero(int x, int y) {
            if (x >= width) {
                return 0;
            }

            if (y >= height) {
                return 0;
            }

            if (x < 0) {
                return 0;
            }

            if (y < 0) {
                return 0;
            }

            return data[getPos(x, y)];
        }

        public void set(float value, int x, int y) {
            data[getPos(x, y)] = value;
        }
    }
}
