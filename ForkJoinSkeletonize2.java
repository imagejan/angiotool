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
 */

package Skeleton;

import ij.ImageStack;
import java.util.ArrayList;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;


public class ForkJoinSkeletonize2 extends RecursiveTask <ArrayList<int[]>> {

    private int mLength;

    private int mStart;

    private int sThreshold = 250;

    private int depth = 1;

    private int width, height;

    private ImageStack outputImage;
    private ImageStack src;

    private int [] eulerLUT = new int[256];

    private int currentBorder;

    private ArrayList <int[]> mSimpleBorderPoints;

    public ForkJoinSkeletonize2 (ImageStack src, int start, int length, int currentBorder, int [] eulerLUT){
        this.src = src;
        this.outputImage = src;

        this.width = src.getWidth();
        this.height = src.getHeight();

        this.mStart = start;
        this.mLength = length;

        this.currentBorder = currentBorder;

        this.mSimpleBorderPoints = new ArrayList <int[]>();

        this.eulerLUT = eulerLUT;
    }


    ForkJoinSkeletonize2() {
    }

    public ArrayList <int []> computeDirectly (){
        for (int z = 0; z < depth; z++){
            for (int y = 0; y < height; y++){
                for (int x = mStart; x < mStart + mLength; x++){
                    if ( SUM.getPixel(outputImage, x, y, z) != 1 ){
                        continue;  
                    }

                    boolean isBorderPoint = false;

                    if( currentBorder == 1 && SUM.N(outputImage, x, y, z) <= 0 )
                      isBorderPoint = true;
                    if( currentBorder == 2 && SUM.S(outputImage, x, y, z) <= 0 )
                      isBorderPoint = true;
                    if( currentBorder == 3 && SUM.E(outputImage, x, y, z) <= 0 )
                      isBorderPoint = true;
                    if( currentBorder == 4 && SUM.W(outputImage, x, y, z) <= 0 )
                      isBorderPoint = true;
                    if( currentBorder == 5 && SUM.U(outputImage, x, y, z) <= 0 )
                      isBorderPoint = true;
                    if( currentBorder == 6 && SUM.B(outputImage, x, y, z) <= 0 )
                      isBorderPoint = true;

                    if( !isBorderPoint ){
                      continue;
                    }

                    int numberOfNeighbors = -1;  
                    byte[] neighbor = SUM.getNeighborhood(outputImage, x, y, z);
                    for( int i = 0; i < 27; i++ ) { 
                        if( neighbor[i] == 1 )
                            numberOfNeighbors++;
                    }

                    if( numberOfNeighbors == 1 ){
                        continue;        
                    }

                    if( !SUM.isEulerInvariant( neighbor, eulerLUT ) ) {
                      continue;         
                    }

                    if( !SUM.isSimplePoint(neighbor ) ) {
                      continue;        
                    }

                    int[] index = new int[3];
                    index[0] = x;
                    index[1] = y;
                    index[2] = z;
                    mSimpleBorderPoints.add(index);

                        }
                }
        }
        return mSimpleBorderPoints;
    }


    @Override
    protected ArrayList <int[]> compute() {
        if (mLength < sThreshold) {
            return computeDirectly();
            
        } else {
            int split = mLength / 2;
            int remainder = mLength % 2;
            int secondHalf = split + remainder;

            ForkJoinSkeletonize2 left  = new ForkJoinSkeletonize2 (src, mStart,         split,      currentBorder, eulerLUT);
            ForkJoinSkeletonize2 right = new ForkJoinSkeletonize2 (src, mStart + split, secondHalf, currentBorder, eulerLUT);

            right.fork();

            ArrayList <int []> a = (ArrayList<int[]>)left.compute();

            a.addAll ((ArrayList<int[]>)right.join());

            return a;
        }
    }


    public  ArrayList <int []> thin (ImageStack src, int currentBorder, int [] eulerLUT){

        ArrayList <int []> simpleBorderPoints = new ArrayList <int[]>();
        outputImage = src;

        ForkJoinSkeletonize2 fe = new ForkJoinSkeletonize2(outputImage, 0, outputImage.getWidth(), currentBorder, eulerLUT);

        ForkJoinPool pool = new ForkJoinPool();

        simpleBorderPoints = pool.invoke(fe);

        return simpleBorderPoints;
    }
}
