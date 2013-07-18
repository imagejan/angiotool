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

package Tubeness.math3d;

public class Eigensystem2x2Float {

    float [][] m;

    float [] eigenVectors;
    float [] eigenValues;

    public Eigensystem2x2Float(float [][] symmetricMatrix) {
        this.m = symmetricMatrix;
        if( m[0][1] != m[1][0] ) {
            throw new RuntimeException("Eigensystem2x2Float only works with symmetric matrices");
        }
    }

    public void getEvalues(float [] eigenValues) {
        eigenValues[0] = this.eigenValues[0];
        eigenValues[1] = this.eigenValues[1];
    }

    public float [] getEvaluesCopy() {
        return eigenValues.clone();
    }

    public float [] getEvalues() {
        return eigenValues;
    }

    public boolean findEvalues() {

        eigenValues = new float[2];

        double A = (double)m[0][0];
        double B = (double)m[0][1];
        double C = (double)m[1][1];

        double a = 1;
        double b = -(A + C);
        double c = A * C - B * B;

        double discriminant = b * b - 4 * a * c;
        if( discriminant < 0 ) {
            return false;
        } else {
            eigenValues[0] = (float)( ( - b + Math.sqrt(discriminant) ) / (2 * a) );
            eigenValues[1] = (float)( ( - b - Math.sqrt(discriminant) ) / (2 * a) );

            return true;
        }
    }
}
