package team2485.auto;

/**
 * Contains three {@code SequencedItem}s. All three will run until they finish.
 * @author Bryce Matsumori
 */
public class SequencedTripleItem implements SequencedItem {
    private final SequencedItem item1, item2, item3;

    public SequencedTripleItem(SequencedItem item1, SequencedItem item2, SequencedItem item3) {
        this.item1 = item1;
        this.item2 = item2;
        this.item3 = item3;
    }

    public void run() {
        item1.run();
        item2.run();
        item3.run();
    }

    public double duration() { return Math.max(Math.max(item1.duration(), item2.duration()), item3.duration()); }
}
