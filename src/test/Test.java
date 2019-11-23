package test;

public class Test {

    static double[][] binomial_matrix = new double[5][5];



    public static void main(String[] args) {
        for (int i = 1; i < 5; i++) {
            for (int j = 0; j <= i; j++) {
                if (i == 1 || j == 0 || j == i) {
                    binomial_matrix[i][j] = 1;
                } else {
                    int up = 1;
                    int down = 1;
                    for (int m = 0; m < j; m++) {
                        up *= (i - m);
                    }
                    for (int m = 1; m <= j; m++) {
                        down *= m;
                    }
                    binomial_matrix[i][j] = up / down;
                }

            }

        }
        for (int i = 0; i < binomial_matrix.length; i++) {
            double[] dd = binomial_matrix[i];
            System.out.print("{");
            for (int i1 = 0; i1 < dd.length; i1++) {
                System.out.print(dd[i1]+",");
            }
            System.out.println("},");
        }
    }
}
