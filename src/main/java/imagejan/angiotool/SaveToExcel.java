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

import ij.*;
import java.util.*;
import java.text.*;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.poifs.filesystem.*;
import java.io.*;
import javax.swing.JOptionPane;

public class SaveToExcel {
    
    private FileOutputStream out;
    private HSSFWorkbook wb;
    private HSSFSheet s;
    private HSSFRow r;
    
    private HSSFCellStyle headingCellStyle, plainCellStyle;
    
    private String filePath = null;
    private boolean _workBookChoice;   
    private String workSheetName = "Results";
    
    private static final String FILE_SEPARATOR = System.getProperty("file.separator");
    
    private static String DEFAULT_PATH = Prefs.getHomeDir()+ FILE_SEPARATOR;
    

    private Date today;
    private String dateOut;
    private DateFormat dateFormatter;
    private DateFormat timeFormatter;
    private String timeOut;

    private boolean areHeadingsWritten = false;

    private boolean excelFileExists = false; 

    String fileName;

    public SaveToExcel(String path, boolean workBookChoice) {

        dateFormatter = DateFormat.getDateInstance(DateFormat.DEFAULT, new Locale("en","US"));
        timeFormatter = DateFormat.getTimeInstance(DateFormat.DEFAULT, new Locale("en","US"));
        today = new Date();
        dateOut = dateFormatter.format(today);
        timeOut = timeFormatter.format(today);

        excelFileExists = this.checkFile(path);

        setFilePath (path);
        setWorkBookChoice (workBookChoice);
    }

    public SaveToExcel (){
        dateFormatter = DateFormat.getDateInstance(DateFormat.DEFAULT, new Locale("en","US"));
        timeFormatter = DateFormat.getTimeInstance(DateFormat.DEFAULT, new Locale("en","US"));
        today = new Date();
        dateOut = dateFormatter.format(today);
        timeOut = timeFormatter.format(today);

        setWorkBookChoice (true);
    }

    public void setFileName(String fileName){
        this.fileName = fileName;
    }

    public void setFilePath (String path){
        if (!path.endsWith(".xls")){
            path = path + ".xls";
        }else {
           ;
        }
        filePath = path;
    }

    public void setWorkBookChoice (boolean workBookChoice){
        _workBookChoice = workBookChoice;
    }
    
    private boolean initializeExcel2(){
        boolean result = false; 
        
        result = initializeHSSF();

        headingCellStyle = headingCellStyle();
        plainCellStyle = plainCellStyle();

        try{
            out = new FileOutputStream(filePath);
        }catch(IOException ioE){
        }
        return result;
    }         
    
    private boolean  initializeHSSF(){ 
        if (_workBookChoice){ 
            if (checkFile(filePath)){
                try{ 
                    POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(filePath));
                    wb = new HSSFWorkbook(fs);
                }catch(IOException ioE){
                }
                s = wb.getSheetAt(0);

            } else {
                wb = new HSSFWorkbook(); 
                s = wb.createSheet(workSheetName); 
            }
        } 
        
        if (wb != null && s != null){
            return true;
        }
        
        return false;
    } 


    public void writeResultsToExcel(Results results){

        today = new Date();
        dateOut = dateFormatter.format(today);
        timeOut = timeFormatter.format(today);

        if (filePath==null) {
            filePath = results.getImageFilePath();
            this.setFilePath(filePath + "Results " + System.currentTimeMillis());
        }

        
        if (initializeExcel2()){
            try {
                if (!this.areHeadingsWritten)
                    writeHeadingAnalysisInfo(results);

                if (true){
                    r = s.createRow((short)(s.getLastRowNum() + 1));

                    createCell (r.getLastCellNum(), results.image.getName(), plainCellStyle);
                    createCell (r.getLastCellNum(), dateOut, plainCellStyle);
                    createCell (r.getLastCellNum(), timeOut, plainCellStyle);
                    createCell (r.getLastCellNum(), results.image.getAbsolutePath(), plainCellStyle);
                    createCell (r.getLastCellNum(), results.thresholdLow, plainCellStyle);
                    createCell (r.getLastCellNum(), results.thresholdHigh, plainCellStyle);
                    createCell (r.getLastCellNum(), results.getSigmas(), plainCellStyle);
                    createCell (r.getLastCellNum(), results.removeSmallParticles, plainCellStyle);
                    createCell (r.getLastCellNum(), results.fillHoles, plainCellStyle);
                    createCell (r.getLastCellNum(), results.LinearScalingFactor, plainCellStyle);

                    createCell (r.getLastCellNum(), "", plainCellStyle);
                    
                    createCell (r.getLastCellNum(), results.allantoisMMArea, plainCellStyle);
                    createCell (r.getLastCellNum(), results.vesselMMArea, plainCellStyle);
                    createCell (r.getLastCellNum(), results.vesselPercentageArea, plainCellStyle);
                    createCell (r.getLastCellNum(), results.totalNJunctions, plainCellStyle);
                    createCell (r.getLastCellNum(), results.JunctionsPerScaledArea, plainCellStyle);
                    createCell (r.getLastCellNum(), results.totalLength, plainCellStyle);
                    createCell (r.getLastCellNum(), results.averageBranchLength, plainCellStyle);
                    createCell (r.getLastCellNum(), results.totalNEndPoints, plainCellStyle);

                    if (results.computeLacunarity){
                        createCell (r.getLastCellNum(), results.meanEl, plainCellStyle);
                    }
                }
            } catch(Exception e){
            }

            try{
                out = new FileOutputStream(filePath);
                wb.write(out);
            }catch(IOException ioE){
                JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "Sorry, I could not write your results\n" +
                        "The file " + filePath + " is being used by another application", "Error", JOptionPane.ERROR_MESSAGE);
                ioE.printStackTrace();
            }
        }
    }


    private int writeHeadingAnalysisInfo (Results results){
        s.setColumnWidth(0, 8000);
        r = s.createRow((short)(0));
        createCell ((short)(r.getLastCellNum()+ 1), "AngioTool v " + Utils.Utils.VERSION, headingCellStyle);

        r = s.createRow((short)s.getLastRowNum()+ 2);

        r = s.createRow((short)s.getLastRowNum()+ 1);
        createCell (r.getLastCellNum()+ 1, "Image Name", headingCellStyle);
        createCell (r.getLastCellNum(), "Date", headingCellStyle);
        createCell (r.getLastCellNum(), "Time", headingCellStyle);
        createCell (r.getLastCellNum(), "Image Location", headingCellStyle);
        createCell (r.getLastCellNum(), "Low Threshold", headingCellStyle);
        createCell (r.getLastCellNum(), "High Threshold", headingCellStyle);
        createCell (r.getLastCellNum(), "Vessel Thickness", headingCellStyle);
        createCell (r.getLastCellNum(), "Small Particles", headingCellStyle);
        createCell (r.getLastCellNum(), "Fill Holes", headingCellStyle);
        createCell (r.getLastCellNum(), "Scaling factor", headingCellStyle);

        createCell (r.getLastCellNum(), "", plainCellStyle);

        createCell (r.getLastCellNum(), "Explant area", headingCellStyle);
        createCell (r.getLastCellNum(), "Vessels area", headingCellStyle);
        createCell (r.getLastCellNum(), "Vessels percentage area", headingCellStyle);
        createCell (r.getLastCellNum(), "Total Number of Junctions", headingCellStyle);
        createCell (r.getLastCellNum(), "Junctions density", headingCellStyle);
        createCell (r.getLastCellNum(), "Total Vessels Length", headingCellStyle);
        createCell (r.getLastCellNum(), "Average Vessels Length", headingCellStyle);
        createCell (r.getLastCellNum(), "Total Number of End Points", headingCellStyle);

        if (results.computeLacunarity){
            createCell (r.getLastCellNum(), "Average Lacunarity", headingCellStyle);
        }

        this.areHeadingsWritten = true;

        return s.getLastRowNum();
    }

    private HSSFCell createCell (int column, Object obj, HSSFCellStyle cellStyle){
        if (column<0)
            column = 0;
        HSSFCell cell = r.createCell(column);
        if (obj instanceof String) cell.setCellValue(new HSSFRichTextString((String)obj));
        else if (obj instanceof Double) cell.setCellValue((Double)obj);
        else if (obj instanceof Integer) cell.setCellValue((Integer) obj);
        else if (obj instanceof Long) cell.setCellValue((Long)obj);
        cell.setCellStyle(cellStyle);

        return cell;
    }

    private boolean checkFile(String path){
        return new File(path).isFile();
    }
    
    private HSSFCellStyle headingCellStyle(){
        HSSFFont font = wb.createFont(); // defaults to Arial on windows
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD); // bold
        HSSFCellStyle style = wb.createCellStyle(); // make style
        style.setFont(font); // set font
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // set alignment to centered
        return style;
    }

    private HSSFCellStyle plainCellStyle(){
        HSSFFont font = wb.createFont(); // defaults to Arial on windows
        font.setBoldweight(HSSFFont.DEFAULT_CHARSET); // bold
        HSSFCellStyle style = wb.createCellStyle(); // make style
        style.setFont(font); // set font
        style.setAlignment(HSSFCellStyle.ALIGN_LEFT); // set alignment to centered
        return style;
    }
}
