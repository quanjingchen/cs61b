package deque;

public class MaxArrayDeque<T> extends ArrayDeque<T>{

    private Comparator<T> comp;

    public MaxArrayDeque(Comparator<T> c) {

        comp = c;
    }


    public T max() {
        return max(comp);
    }

    public T max(Comparator<T> c) {
        if (size() == 0) {
            return null;
        }
        T MaxElement =get(0);
        for (int i = 0; i < size(); i ++) {
            T curr = get(i);
            if (c.compare(curr,MaxElement) > 0) {
                MaxElement = curr;
            }
        }
        return MaxElement;
    }

    public static class NameComparator<T> implements Comparator<T> {
        @Override
        public int compare(T o1, T o2) {
                return o1.toString().compareTo(o2.toString());
        }
    }

}
