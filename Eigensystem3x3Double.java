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

public class Eigensystem3x3Double {

    double [][] m;

    double [] eigenVectors;
    double [] eigenValues;

    public Eigensystem3x3Double(double [][] symmetricMatrix) {
        this.m = symmetricMatrix;
        if( m[0][1] != m[1][0] || m[0][2] != m[2][0] || m[1][2] != m[2][1] ) {
                throw new RuntimeException("Eigensystem3x3Double only works with symmetric matrices");
        }
    }

    public void getEvalues(double [] eigenValues) {
        eigenValues[0] = this.eigenValues[0];
        eigenValues[1] = this.eigenValues[1];
        eigenValues[2] = this.eigenValues[2];
    }

    public double [] getEvaluesCopy() {
        return eigenValues.clone();
    }

    public double [] getEvalues() {
        return eigenValues;
    }

    public boolean findEvalues() {

        eigenValues = new double[3];

        double A = (double)m[0][0];
        double B = (double)m[0][1];
        double C = (double)m[0][2];
        double D = (double)m[1][1];
        double E = (double)m[1][2];
        double F = (double)m[2][2];

        double a = -1;

        double b =
                + A
                + D
                + F;

        double c =
                + B * B
                + C * C
                + E * E
                - A * D
                - A * F
                - D * F;

        double d =
                + A * D * F
                - A * E * E
                - B * B * F
                + 2 * B * C * E
                - C * C * D;

        final double third = 0.333333333333333333333333333333333333;

        double q = (3*a*c - b*b) / (9*a*a);
        double r = (9*a*b*c - 27*a*a*d - 2*b*b*b) / (54*a*a*a);

        double discriminant = q*q*q + r*r;

        if( discriminant > 0 ) {
            return false;
        } else if( discriminant < 0 ) {

            double rootThree = 1.7320508075688772935;

            double innerSize = Math.sqrt( r*r - discriminant );
            double innerAngle;

            if( r > 0 )
                    innerAngle = Math.atan( Math.sqrt(-discriminant) / r );
            else
                    innerAngle = ( Math.PI - Math.atan( Math.sqrt(-discriminant) / -r ) );

            double stSize = Math.pow(innerSize,third);

            double sAngle = innerAngle / 3;
            double tAngle = - innerAngle / 3;

            double sPlusT = 2 * stSize * Math.cos(sAngle);

            eigenValues[0] = (double)( sPlusT - (b / (3*a)) );

            double firstPart = - (sPlusT / 2) - (b / 3*a);

            double lastPart = - rootThree * stSize * Math.sin(sAngle);

            eigenValues[1] = (double)( firstPart + lastPart );
            eigenValues[2] = (double)( firstPart - lastPart );

            return true;

        } else {

            double sPlusT;
            if( r >= 0 )
                    sPlusT = 2 * Math.pow(r,third);
            else
                    sPlusT = -2 * Math.pow(-r,third);

            double bOver3A = b / (3 * a);

            eigenValues[0] = (double)( sPlusT - bOver3A );
            eigenValues[1] = (double)( - sPlusT / 2 - bOver3A );
            eigenValues[2] = eigenValues[1];

            return true;
        }
    }
}
