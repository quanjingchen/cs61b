import java.lang.*;
public class HelloWorld {
    public static void main(String[] args) {
        System.out.println("Hello World");
        int[] LL = {1,2,3,4,5,6,7,8,9,10,0,0};
        int[] RR = {11,12,13,14,15,16,17,18,19,20};
        int[][] S = {
                {0,0,0,0,0},
                {0,0,0,0,0},
                {0,0,0,0,0},
                {0,0,0,0,0},
                {0,0,0,0,0},
        };
        fillGrid(LL, RR, S);

	}



    public static void fillGrid(int[] LL, int[] RR, int[][] S) {
        int N = S.length;
        int KL, KR;
        KL = KR = 0;
        for (int i = 0; i < N; i += 1) {
            for (int j = 0; j < N; j += 1) {
                if (i > j) {
                    S[i][j] = LL[KL];
                    KL ++;
                } else if (i < j) {
                    S[i][j] = RR[KR];
                    KR ++;
                }

            }
        }
    }
}
