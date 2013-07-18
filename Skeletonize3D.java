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
 * May, 2011
 * angiotool.nci.nih.gov
 *
 * Based on Ignacio Arganda-Carreras
 * http://imagejdocu.tudor.lu/doku.php?id=plugin:morphology:skeletonize3d:start
 *
 */

package Skeleton;

import java.util.ArrayList;

import ij.ImagePlus;
import ij.ImageStack;
import ij.process.ImageProcessor;

public class Skeletonize3D {

    private ImagePlus imRef;

    private int width = 0;
    private int height = 0;
    private int depth = 0;
    private ImageStack inputImage = null;

    public void setup(String arg, ImagePlus imp){
        this.imRef = imp;
        SUM.setImage(imRef);
    } 

    public ImageProcessor  run(ImageProcessor ip) 	{
        this.width = this.imRef.getWidth();
        this.height = this.imRef.getHeight();
        this.depth = this.imRef.getStackSize();
        this.inputImage = this.imRef.getStack();

        prepareData(this.inputImage);

        computeThinImage(this.inputImage);

        for(int i = 1; i <= this.inputImage.getSize(); i++)
                this.inputImage.getProcessor(i).multiply(255);

        this.inputImage.update(ip);

        return inputImage.getProcessor(1);

    }

    private void prepareData(ImageStack outputImage) {
        for (int z = 0; z < depth; z++)
                for (int x = 0; x < width; x++)
                        for (int y = 0; y < height; y++)
                                if ( ((byte[]) this.inputImage.getPixels(z + 1))[x + y * width] != 0 )
                                        ((byte[]) outputImage.getPixels(z + 1))[x + y * width] = 1;
    }

    public void computeThinImage(ImageStack outputImage){

        ImagePlus result = new ImagePlus();

        ArrayList <int[]> simpleBorderPoints = new ArrayList<int[]>();

        int eulerLUT[] = new int[256];
        SUM.fillEulerLUT(eulerLUT);

        int iter = 1;

        int unchangedBorders = 0;
        while( unchangedBorders < 6 ) { 
            unchangedBorders = 0;
            for( int currentBorder = 1; currentBorder <= 6; currentBorder++) {
                ForkJoinSkeletonize2 fs2 =  new ForkJoinSkeletonize2 ();
                simpleBorderPoints = fs2.thin(outputImage, currentBorder, eulerLUT);

                boolean noChange = true;
                int[] index = null;
                for(int i = 0; 	i < simpleBorderPoints.size() ; i++){
                    index = simpleBorderPoints.get(i);
                        SUM.setPixel( outputImage, index[0], index[1], index[2], (byte) 0);
                    if( !SUM.isSimplePoint( SUM.getNeighborhood(outputImage, index[0], index[1], index[2]) ) ){
                        SUM.setPixel( outputImage, index[0], index[1], index[2], (byte) 1);
                    }
                    else{
                            noChange = false;
                    }
                }
                if( noChange )
                    unchangedBorders++;
                simpleBorderPoints.clear();

            } 
            iter++;
        }
    } 
}
