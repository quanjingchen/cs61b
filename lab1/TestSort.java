import org.junit.Test;
import static org.junit.Assert.*;

public class TestSort {
    @Test
    public void testSort() {
        String[] input = {"i","have","an","egg"};
        String[] expected = {"n","egg","have","i"};
        Sort.sort(input);
        assertArrayEquals(expected,input);

    }

    @Test
    public void testfindSmallest() {
        String[] input = {"i","have","an","egg"};
        int expected = 2;
        int index = Sort.findSmallest(input,0);
        /*assertEquals(expected,index);*/
        assertEquals(expected,index);


    }


}
