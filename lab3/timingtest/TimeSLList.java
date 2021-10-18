package timingtest;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeSLList {
    private static void printTimingTable(AList<Integer> Ns, AList<Double> times, AList<Integer> opCounts) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.printf("------------------------------------------------------------\n");
        for (int i = 0; i < Ns.size(); i += 1) {
            int N = Ns.get(i);
            double time = times.get(i);
            int opCount = opCounts.get(i);
            double timePerOp = time / opCount * 1e6;
            System.out.printf("%12d %12.2f %12d %12.2f\n", N, time, opCount, timePerOp);
        }
    }

    public static void main(String[] args) {
        timeGetLast();
    }

    public static void timeGetLast() {
        // TODO: YOUR CODE HERE
        AList K = new AList() {};
        K.addLast(1000);
        K.addLast(2000);
        K.addLast(4000);
        K.addLast(8000);
        K.addLast(16000);
        K.addLast(32000);
        K.addLast(64000);
        K.addLast(128000);

        AList time = new AList();
        AList opCounts = new AList();
        int ops = 10000;

        for (int j = 0; j < K.size(); j ++) {
            int K_length = (int) K.get(j);
            SLList Ns = new  SLList();

            for (int i = 0; i < K_length; i ++) {
                Ns.addLast(0);
            }

            Stopwatch sw = new Stopwatch();
            for (int i = 0; i < ops; i ++) {
                Ns.getLast();
            }
            double timeInSeconds = sw.elapsedTime();

            time.addLast(timeInSeconds);
            opCounts.addLast(ops);
        }

        printTimingTable(K, time, opCounts);

    }

}
