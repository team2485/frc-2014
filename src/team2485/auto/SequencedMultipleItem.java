package team2485.auto;

/**
 * Creates complete sequence of all array items
 * @author W.A.R.Lords
 */
public class SequencedMultipleItem implements SequencedItem {
    private SequencedItem[] items;

    public SequencedMultipleItem(SequencedItem[] itemsParam) {
        items = new SequencedItem[itemsParam.length];
        for(int x = 0; x < itemsParam.length; x++) {
            items[x] = itemsParam[x];
        }
    }

    public void run() {
        for(int x = 0; x < items.length; x++) {
            items[x].run();
        }
    }

    public double duration() {
        double max = 0;
        for(int x = 0; x < items.length; x++) {
            if(items[x].duration() > max)
                max = items[x].duration();
        }

        return max;
    }
}
