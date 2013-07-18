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

package AngioTool;

import java.io.File;
import java.util.ArrayList;

public class Results {
    public File image;
    public boolean isResized;
    public int resizingFactor;
    public int thresholdLow;
    public int thresholdHigh;
    public int removeSmallParticles;
    public int fillHoles; 
    public ArrayList <Integer> sigmas;
    public int totalNJunctions;
    public double allantoisPixelsArea;
    public double LinearScalingFactor;
    public double AreaScalingFactor;
    public double allantoisMMArea;  
    public double JunctionsPerArea;
    public double JunctionsPerScaledArea;
    public double totalLength;
    public double averageBranchLength;
    public int totalNEndPoints; 
    public long vesselPixelArea;
    public double vesselMMArea;
    public double vesselPercentageArea;
    public boolean computeLacunarity = false;
    public double meanEl;



    public Results(){
        sigmas = new ArrayList <Integer>();
    }

    public void clear (){
        thresholdLow = Integer.MAX_VALUE;
        thresholdHigh = Integer.MIN_VALUE;
        sigmas.clear();
        totalNJunctions = Integer.MIN_VALUE;
        allantoisPixelsArea = Integer.MIN_VALUE;
        LinearScalingFactor = Double.NaN;
        AreaScalingFactor = Double.NaN;
        allantoisMMArea = Double.NaN;
        JunctionsPerArea = Double.NaN;
        JunctionsPerScaledArea = Double.NaN;
        totalLength = Double.NaN;
        averageBranchLength = Double.NaN;
        totalNEndPoints = 0;
    }


    public String getSigmas (){
        String s = "";
        for (int i = 0; i< sigmas.size()-1; i++){
            s+= sigmas.get(i) + ",";
        }
        s+=sigmas.get(sigmas.size()-1);
        return s;
    }

    public String getImageFilePath (){
        return image.getParentFile() + "/";
    }
}
