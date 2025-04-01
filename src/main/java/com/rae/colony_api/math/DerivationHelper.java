package com.rae.colony_api.math;


import net.createmod.catnip.data.Couple;
import org.joml.Vector3d;

public class DerivationHelper {

    public static Plane getDerivative(Vector3d A, Vector3d B, Vector3d C){

        // two equation :
        // B.z - A.z = df$dx*(B.x - A.x) + df$dy*(B.y - A.y)
        // C.z - A.z = df$dx*(C.x - A.x) + df$dy*(C.y - A.y)
        SquareMatrix M = new SquareMatrix((B.x - A.x), (B.y - A.y), (C.x - A.x), (C.y - A.y));
        Couple<Double> Z = Couple.create(B.z - A.z, C.z - A.z);
        Couple<Double> derivative = M.invert().rightMul(Z);
        return new Plane(derivative.getFirst(),derivative.getSecond(), A.x,A.y,A.z);
    }

    /**
     * define a plane where z = df$dx(x-x0) + df$dy(y-y0) + z0
     * @param df$dx
     * @param df$dy
     * @param x0
     * @param y0
     * @param z0
     */
    public record Plane(double df$dx, double df$dy, double x0, double y0, double z0) {
        public double getPoint(double x,double y){
            return  df$dx*(x-x0) + df$dy*(y-y0) + z0;
        }
        public double getPoint(Couple<Double> coordinate){
            return  getPoint(coordinate.getFirst(),coordinate.getSecond());
        }
    }

    public static class SquareMatrix {
        protected double m00;
        protected double m01;
        protected double m10;
        protected double m11;


        SquareMatrix(double m00, double m01, double m10, double m11){
            this.m00 = m00;
            this.m01 = m01;
            this.m10 = m10;
            this.m11 = m11;
        }
        public double det(){
            return m00*m11-m01*m10;
        }

        /**
         * @return facto * this
         */
        public SquareMatrix mul(double factor){
            return new SquareMatrix(factor*m00,factor*m01,factor*m10,factor*m11);
        }

        /**
         * @return this * vector
         */
        public Couple<Double> rightMul(Couple<Double> vector){
            return Couple.create(m00 * vector.getFirst() + m01 * vector.getSecond(), m10 * vector.getFirst() + m11 * vector.getSecond());
        }
        public SquareMatrix invert(){
            if (this.det()==0){
                return null;
            }
            double factor = 1/this.det();
            SquareMatrix partialMatrix = this.mul(factor);
            return new SquareMatrix(partialMatrix.m11, -partialMatrix.m10, -partialMatrix.m01, partialMatrix.m00);
        }
    }
}
