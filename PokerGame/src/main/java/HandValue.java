package Poker;

import java.util.Collections;
import java.util.List;

public class HandValue implements Comparable<HandValue> {

    private final int category;
    private final List<Integer> kickers;
    private final String handName;

    public HandValue(int category, List<Integer> kickers, String handName) {
        this.category = category;
        this.kickers = Collections.unmodifiableList(kickers);
        this.handName = handName;
    }

    public int getCategory() {
        return category;
    }

    public List<Integer> getKickers() {
        return kickers;
    }

    public String getHandName() {
        return handName;
    }

    @Override
    public int compareTo(HandValue other) {
        if (category != other.category) {
            return Integer.compare(category, other.category);
        }

        int max = Math.min(kickers.size(), other.kickers.size());
        for (int index = 0; index < max; index++) {
            int comparison = Integer.compare(kickers.get(index), other.kickers.get(index));
            if (comparison != 0) {
                return comparison;
            }
        }

        return Integer.compare(kickers.size(), other.kickers.size());
    }
}
