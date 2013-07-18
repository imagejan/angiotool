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

import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;


public class PolygonPlus {
    private final int N;
    private final Point[] points;
    private Polygon p;

    public PolygonPlus(Point[] points) {
        N = points.length;
        this.points = new Point[N+1];
        for (int i = 0; i < N; i++){
            this.points[i] = points[i];
        }
        this.points[N] = points[0];
        p = new Polygon();
        for (int i = 0; i < this.points.length; i++){
            p.addPoint(this.points[i].x, this.points[i].y);
        }
    }

    public PolygonPlus(Polygon p) {
        this.p = p;
        N = p.npoints;
        this.points = new Point[N+1];
        for (int i = 0; i < N; i++)
            this.points[i] = new Point (p.xpoints[i], p.ypoints[i]);
        this.points[N] = this.points[0];
    }
    
    public Polygon polygon (){
        return this.p;
    }
    
    public double area() { 
        return Math.abs(signedArea()); 
    }

    public double signedArea() {
        double sum = 0.0;
        for (int i = 0; i < N; i++) {
            sum = sum + (points[i].x * points[i+1].y) - (points[i].y * points[i+1].x);
        }
        return 0.5 * sum;
    }

    public boolean contains (int x, int y){
        return this.p.contains(x, y);
    }

    public boolean contains (Point p){
        return this.p.contains(p);
    }

    public boolean intersects (Rectangle r){
        return p.intersects(r) ;
    }
      
    public double perimeter() {
        double sum = 0.0;
        for (int i = 0; i < N; i++)
            sum = sum + points[i].distance(points[i+1]);
        return sum;
    }
}
