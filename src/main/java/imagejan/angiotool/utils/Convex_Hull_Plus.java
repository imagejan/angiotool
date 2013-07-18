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
 * Based on Gabriel Landini
 * http://www.dentistry.bham.ac.uk/landinig/software/software.html
 */

package Utils;

import ij.*;
import ij.process.*;
import ij.gui.*;

public class Convex_Hull_Plus {
    ImagePlus imp;
    int counter=0;
    private PolygonRoi convexHullPolygonRoi = null;

    public void run(ImageProcessor ip) {

        this.imp = new ImagePlus ("", ip);

        String myMode = "Convex Hull selection";
        boolean white = true;

        int i, j, k=0, m, colour=0;

        if (white)
            colour=255;

        for (j=0;j<imp.getHeight();j++){
            for (i=0;i<imp.getWidth();i++){
                if (ip.getPixel(i,j)== colour)
                    counter++;
            }
        }

        int[] x = new int[counter+1];
        int[] y = new int[counter+1];

        for (j=0;j<imp.getHeight();j++){
            for (i=0;i<imp.getWidth();i++){
                if (ip.getPixel(i,j) == colour){
                    x[k] = i;
                    y[k] = j;
                    k++;
                }
            }
        }

        int n=counter, min = 0, ney=0, px, py, h, h2, dx, dy, temp, ax, ay;
        double minangle, th, t, v, zxmi=0;

        for (i=1;i<n;i++){
            if (y[i] < y[min])
                min = i;
        }

        temp = x[0]; x[0] = x[min]; x[min] = temp;
        temp = y[0]; y[0] = y[min]; y[min] = temp;
        min = 0;

        for (i=1;i<n;i++){
            if (y[i] == y[0]){
                ney ++;
                if (x[i] < x[min]) min = i;
            }
        }
        temp = x[0]; x[0] = x[min]; x[min] = temp;
        temp = y[0]; y[0] = y[min]; y[min] = temp;
        ip.setColor(127);

        px = x[0];
        py = y[0];

        min = 0;
        m = -1;
        x[n] = x[min];
        y[n] = y[min];
        if (ney > 0)
            minangle = -1;
        else
            minangle = 0;

        while (min != n+0 ){
            m = m + 1;
            temp = x[m]; x[m] = x[min]; x[min] = temp;
            temp = y[m]; y[m] = y[min]; y[min] = temp;

            min = n ;
            v = minangle;
            minangle = 360.0;
            h2 = 0;

            for (i = m + 1;i<n+1;i++){
                dx = x[i] - x[m];
                ax = Math.abs(dx);
                dy = y[i] - y[m];
                ay = Math.abs(dy);

                if (dx == 0 && dy == 0)
                    t = 0.0;
                else
                    t = (double)dy / (double)(ax + ay);

                if (dx < 0)
                    t = 2.0 - t;
                else {
                    if (dy < 0)
                        t = 4.0 + t;
                }
                th = t * 90.0;

                if(th > v){
                    if(th < minangle){
                        min = i;
                        minangle = th;
                        h2 = dx * dx + dy * dy;
                    }
                    else{
                        if (th == minangle){
                            h = dx * dx + dy * dy;
                            if (h > h2){
                                min = i;
                                h2 = h;
                            }
                        }
                    }
                }
            }
            if (myMode.equals("Draw Convex Hull") || myMode.equals("Draw both"))
                ip.drawLine(px,py,x[min],y[min]);
            px = x[min];
            py = y[min];
            zxmi = zxmi + Math.sqrt(h2);
        }
        m++;

        int[] hx = new int[m];
        int[] hy = new int[m];

        for (i=0;i<m;i++){
            hx[i] =  x[i];
            hy[i] =  y[i];
        }

        if (myMode.equals("Convex Hull selection")) {
            convexHullPolygonRoi = new PolygonRoi(hx, hy, hx.length, Roi.POLYGON);
            imp.setRoi(convexHullPolygonRoi);
        }

        double [] d = new double [(m *(m-1))/2];
        int[] p1 = new int [(m *(m-1))/2];
        int[] p2 = new int [(m *(m-1))/2];

        k=0;
        for (i=0;i<m-1;i++){
            for (j=i+1;j<m;j++){
                d[k]= Math.sqrt(Math.pow(hx[i]-hx[j], 2.0) + Math.pow(hy[i]-hy[j], 2.0));
                p1[k]=i;
                p2[k]=j;
                k++;
            }
        }
        k--;

        boolean sw = true;
        double tempd;
        int centre, cw, pc, p3;
        double tt, tttemp, radius, cx, cy;

        while (sw){
            sw=false;
            for(i=0;i<k-1;i++){
                if (d[i]<d[i+1]){
                    tempd = d[i]; d[i] = d[i+1]; d[i+1] = tempd;
                    temp = p1[i]; p1[i] = p1[i+1]; p1[i+1] = temp;
                    temp = p2[i]; p2[i] = p2[i+1]; p2[i+1] = temp;
                    sw = true;
                }
            }
        }

        radius=d[0]/2.0;

        cx=(hx[p1[0]]+hx[p2[0]])/2.0;
        cy=(hy[p1[0]]+hy[p2[0]])/2.0;

        p3=-1;
        tt=radius;
        for (i=0;i<m;i++){
            tttemp=Math.sqrt(Math.pow(hx[i]-cx, 2.0) + Math.pow(hy[i]-cy, 2.0));
            if(tttemp>tt){
                tt=tttemp;
                p3=i;
            }
        }

        if (p3>-1){
                double [] op1 = new double [2];
                double [] op2 = new double [2];
                double [] op3 = new double [2];
                double [] circ = new double [3];
                double tD=Double.MAX_VALUE;
                int tp1=0, tp2=0, tp3=0, z;

                for (i=0; i<m-2; i++){
                    for (j=i+1; j<m-1; j++){
                        for (k=j+1; k<m; k++){
                            op1[0]=hx[i];
                            op1[1]=hy[i];
                            op2[0]=hx[j];
                            op2[1]=hy[j];
                            op3[0]=hx[k];
                            op3[1]=hy[k];
                            osculating(op1, op2, op3, circ);
                            if (circ[2]>0){
                                sw=true;
                                for (z=0;z<m;z++){
                                    tttemp=(float)Math.sqrt(Math.pow(hx[z]-circ[0], 2.0) + Math.pow(hy[z]-circ[1], 2.0));
                                    if(tttemp>circ[2]){
                                        sw=false; 
                                        break;
                                    }
                                }
                                if(sw){
                                    if (circ[2]<tD){
                                        tp1=i;
                                        tp2=j;
                                        tp3=k;
                                        tD=circ[2];
                                    }
                                }
                            }
                        }
                    }
                }
                op1[0]=hx[tp1];
                op1[1]=hy[tp1];
                op2[0]=hx[tp2];
                op2[1]=hy[tp2];
                op3[0]=hx[tp3];
                op3[1]=hy[tp3];
                osculating(op1, op2, op3, circ);
                radius=circ[2];
                if (myMode.equals("Minimal Bounding Circle selection")){
                    if (circ[2]>0)
                        IJ.makeOval((int) Math.floor((circ[0]-circ[2])+.5), (int) Math.floor((circ[1]-circ[2])+.5),(int)Math.floor((radius*2)+.5), (int)Math.floor((radius*2)+.5));
                }
                if (myMode.equals("Draw Minimal Bounding Circle") || myMode.equals("Draw both")){
                    if (circ[2]>0){
                        IJ.makeOval((int) Math.floor((circ[0]-circ[2])+.5), (int) Math.floor((circ[1]-circ[2])+.5), (int)Math.floor((radius*2)+.5), (int)Math.floor((radius*2)+.5));
                        IJ.run("Unlock Image");
                        IJ.run("Draw");
                    }
                }
        }
        else{
            if (myMode.equals("Minimal Bounding Circle selection"))
                IJ.makeOval((int)Math.floor(cx-radius+.5), (int)Math.floor(cy-radius+.5), (int)(Math.floor(d[0]+.5)),(int)(Math.floor(d[0]+.5)));
            if (myMode.equals("Draw Minimal Bounding Circle") || myMode.equals("Draw both")){
                IJ.makeOval((int)(cx-radius), (int)(cy-radius), (int)(Math.floor(d[0]+.5)),(int)(Math.floor(d[0]+.5)));
                IJ.run("Unlock Image");
                IJ.run("Draw");
            }
        }
    }

    public PolygonRoi getConvexHullPolygonRoi(){
        return convexHullPolygonRoi;
    }

    void osculating( double [] pa, double [] pb, double [] pc, double [] centrad){
        double a, b, c, d, e, f, g;

        if ((pa[0]==pb[0] && pb[0]==pc[0]) || (pa[1]==pb[1] && pb[1]==pc[1])){ //colinear coordinates
            centrad[0]=0; 
            centrad[1]=0; 
            centrad[2]=-1;
            return;
        }

        a = pb[0] - pa[0];
        b = pb[1] - pa[1];
        c = pc[0] - pa[0];
        d = pc[1] - pa[1];

        e = a*(pa[0] + pb[0]) + b*(pa[1] + pb[1]);
        f = c*(pa[0] + pc[0]) + d*(pa[1] + pc[1]);

        g = 2.0*(a*(pc[1] - pb[1])-b*(pc[0] - pb[0]));

        if (g==0.0){
            centrad[0]=0; 
            centrad[1]=0; 
            centrad[2]=-1; 
        }
        else {
            centrad[0] = (d * e - b * f) / g;
            centrad[1] = (a * f - c * e) / g;
            centrad[2] = (float)Math.sqrt(Math.pow((pa[0] - centrad[0]),2) + Math.pow((pa[1] - centrad[1]),2));
        }
    }
}



