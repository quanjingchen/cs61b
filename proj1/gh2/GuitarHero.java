package gh2;
import edu.princeton.cs.algs4.StdAudio;
import edu.princeton.cs.algs4.StdDraw;

public class GuitarHero {
    public static void main(String[] args) {

        // create guitar strings, for 37 keys
        double concertA = 440.0;
        //setup the keyboard
        String keyboard = "q2we4r5ty7u8i9op-[=zxdcfvgbnjmk,.;/' ";
        GuitarString[] strings = new GuitarString[37];
        for (int i = 0; i < 37; i++) {
            double frequency = concertA * Math.pow(2, (i - 24) / 12.);
            System.out.println(frequency);
            strings[i] = new GuitarString(frequency);
        }

        GuitarString string = strings[0];

        while (true) {

            // check if the user has typed a key; if so, process it
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                int index = keyboard.indexOf(key);
                if (index != -1) {
                    string = strings[index];
                    string.pluck();
                }
            }

            // compute the superposition of samples
            double sample = string.sample();

            // play the sample on standard audio
            // note: this is just playing the double value YOUR GuitarString
            //       class is generating.
            StdAudio.play(sample);

            // advance the simulation of each guitar string by one step
            string.tic();
        }
    }
}
