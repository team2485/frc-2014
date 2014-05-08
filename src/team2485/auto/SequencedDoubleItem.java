package team2485.auto;

/**
 * Contains two {@code SequencedItem}s. Both are run until both finish.
 * @author Bryce Matsumori
 */
public class SequencedDoubleItem implements SequencedItem {
    private final SequencedItem item1, item2;

    public SequencedDoubleItem(SequencedItem item1, SequencedItem item2) {
        this.item1 = item1;
        this.item2 = item2;
    }

    public void run() {
        item1.run();
        item2.run();
    }

    public double duration() { return Math.max(item1.duration(), item2.duration()); }
}
