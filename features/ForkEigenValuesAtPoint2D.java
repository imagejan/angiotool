package Tubeness.features;

import Utils.Utils;
import ij.ImagePlus;
import ij.process.ByteProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import Tubeness.math3d.Eigensystem2x2Float;
import Tubeness.math3d.Eigensystem3x3Float;
import Tubeness.math3d.JacobiFloat;

/**
 *
 * @author <a href="mailto:zudairee@mail.nih.gov">
 * Enrique Zudaire</a>, Radiation Oncology Branch, NCI, NIH
 */

public class ForkEigenValuesAtPoint2D extends RecursiveAction {

    float [] evalues = new float [3];
    protected static FloatArray2D data2D;

    protected boolean fixUp = false;
    protected boolean orderOnAbsoluteSize = true;
    protected boolean normalize = false;

    float sepX=1, sepY=1;
    int width=0, height=0;
    ImagePlus original;
    int threshold;
    
    public float[] sliceFinal; 

    float minResult = Float.MAX_VALUE;
    float maxResult = Float.MIN_VALUE;
    int mLength;

    int mStartA, mStartB;
    protected double sigma;

    protected static int sThreshold = 5;


    public ForkEigenValuesAtPoint2D(ImagePlus original, FloatArray2D data, int start, int length, double sigma, int threshold, float [] dst){
        this.original = original;
        width = original.getWidth();
        height = original.getHeight();

        data2D = data;
        mLength = length;

        mStartA = mStartB = start;
        if (mStartA==0) mStartA=1;
        if (mStartB+mLength >= width-1) mLength-=1;

        this.sigma = sigma;
        this.threshold = threshold;
        this.sliceFinal = dst;
    }

    ForkEigenValuesAtPoint2D() {
    }

    private void computeDirectly (){
        long count = 0;
        long total = height * width;
        ImageProcessor fp = original.getProcessor();
        for (int y = 1; y < height - 1; ++y) {
            for (int x = mStartA; x < mStartB + mLength; ++x) {
                if (fp.getPixelValue(x, y) > this.threshold){

                        boolean real = hessianEigenvaluesAtPoint2D(x, y,
                                                                     true, // order absolute
                                                                     evalues,
                                                                     normalize,
                                                                     false,
                                                                     sepX,
                                                                     sepY);
                        int index = y * width + x;
                        float value = 0;
                        if( real ) {
                            value = measureFromEvalues2D(evalues, 1);
                        }

                            this.sliceFinal[index] = value;
                    }
                count ++;
                }
        }
    }

    @Override
    protected void compute() {
        if (mLength < sThreshold) {
            computeDirectly();
            return;
        } else {
            int split = mLength / 2;
            int remainder = mLength % 2;
            int secondHalf = split + remainder;
            invokeAll(new ForkEigenValuesAtPoint2D(original, data2D, mStartB,         split,      sigma, this.threshold, this.sliceFinal),
                      new ForkEigenValuesAtPoint2D(original, data2D, mStartB + split, secondHalf, sigma, this.threshold, this.sliceFinal));
        }
    }
    
  public float []  computeEigenvalues (ImagePlus original, double sigma, int threshold) {
      
    data2D = ImageToFloatArray (original.getProcessor());
    int [] histogram2 = Utils.getFloatHistogram2(original);

    this.threshold = Utils.findHistogramMax(histogram2)+ 2;

    this.threshold = 3;

    this.sliceFinal = new float [data2D.data.length];

    ForkEigenValuesAtPoint2D fe = new ForkEigenValuesAtPoint2D(original, data2D, 0, original.getWidth(), sigma, this.threshold, this.sliceFinal);

    ForkJoinPool pool = new ForkJoinPool();

    pool.invoke(fe);

    return this.sliceFinal;
}

    public boolean hessianEigenvaluesAtPoint2D(int x,
            int y,
            boolean orderOnAbsoluteSize,
            float[] result, /* should be 2 elements */
            boolean normalize,
            boolean fixUp,
            float sepX,
            float sepY) {

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

        float[][] hessianMatrix = computeHessianMatrix2DFloat(data2D, x, y, this.sigma, sepX, sepY);
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

	public float measureFromEvalues2D( float [] evalues, int vesselness ) {

            float measure = 0;

            if (evalues[1] >= 0){
                return 0;
             }else{
                return (float)Math.abs(evalues[1]);
            }
	}


    public float[][] computeHessianMatrix2DFloat(FloatArray2D laPlace, int x, int y, double sigma, float sepX, float sepY) {

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
        } else
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
