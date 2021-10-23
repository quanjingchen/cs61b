package deque;

public class MaxArrayDeque<Bibimbap> extends ArrayDeque<Bibimbap>{

    private Comparator<Bibimbap> comp;

    public MaxArrayDeque(Comparator<Bibimbap> c) {

        comp = c;
    }


    public Bibimbap max() {
        return max(comp);
    }

    public Bibimbap max(Comparator<Bibimbap> c) {
        if (size() == 0) {
            return null;
        }
        Bibimbap max_element =get(0);
        for (int i = 0; i < size(); i ++) {
            Bibimbap curr_element =get(i);
            if (c.compare(curr_element,max_element) > 0) {
                max_element = curr_element;
            }
        }
        return max_element;
    }

    public static class NameComparator<Bibimbap> implements Comparator<Bibimbap> {
        @Override
        public int compare(Bibimbap o1, Bibimbap o2) {
                return o1.toString().compareTo(o2.toString());
        }
    }

}
