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
 * @author <a href="mailto:zudairee@mail.nih.gov">
 * Enrique Zudaire</a>, Radiation Oncology Branch, NCI, NIH
 * angiotool.nci.nih.gov
 *
 * Based on Stephan Preibisch
 * http://www.longair.net/edinburgh/imagej/tubeness/"
 */
package Tubeness.features;

import RecursiveGaussianFilter.RecursiveGaussianFilter;
import ij.ImagePlus;
import ij.ImageStack;
import ij.measure.Calibration;
import ij.process.FloatProcessor;


public abstract class HessianEvalueProcessor {

    public abstract float measureFromEvalues2D( float [] evalues, int vesselness );

    protected boolean normalize = false;
    protected double [] sigma;
    protected int threshold = 0;

    public void setSigma( double [] newSigma ) {
        sigma = newSigma;
    }

    public ImagePlus generateImage(ImagePlus original) {

        Calibration calibration=original.getCalibration();

        ComputeCurvatures c;

        int width = original.getWidth();
        int height = original.getHeight();

        ImageStack stack = new ImageStack(width, height);

        float[] evalues = new float[3];

        float minResult = Float.MAX_VALUE;
        float maxResult = Float.MIN_VALUE;

        float[] slice = new float[width * height];
        for (int i = 0; i<slice.length; i++){
            slice [i] = 0;
        }

        for (int s = 0; s<sigma.length; s++){

            float [][] f = original.getProcessor().getFloatArray();
            RecursiveGaussianFilter rgf = null;


            rgf = new RecursiveGaussianFilter (sigma[s], RecursiveGaussianFilter.Method.VAN_VLIET);
            rgf.apply00(f, f);

            FloatProcessor fp = new FloatProcessor (f);
            ImagePlus rgfIplus = new ImagePlus ("", fp);

            c = new ComputeCurvatures();
            c.setData(rgfIplus);
            c.setSigma(sigma[s]);

             ForkEigenValuesAtPoint2D fe = new ForkEigenValuesAtPoint2D ();
             float [] slice2 = fe.computeEigenvalues(rgfIplus, sigma[s], this.threshold);
             for (int i = 0; i<slice.length; i++){
                 if (slice2[i]>slice[i])
                     slice[i]= slice2[i];

                if( slice[i] < minResult )
                        minResult = slice[i];
                if( slice[i] > maxResult )
                        maxResult = slice[i];
             }
        }

        FloatProcessor fp = new FloatProcessor(width, height);
        fp.setPixels(slice);
        stack.addSlice(null, fp);

        ImagePlus result=new ImagePlus("processed " + original.getTitle(), stack);
        result.setCalibration(calibration);

        result.getProcessor().setMinAndMax(minResult,maxResult);
        result.updateAndDraw();

        return result;
    }
}