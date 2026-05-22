package Poker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HandEvaluator {

    public HandValue evaluateBestHand(List<Card> cards) {
        if (cards.size() < 5) {
            throw new IllegalArgumentException("At least 5 cards are required");
        }

        HandValue best = null;
        int size = cards.size();

        for (int a = 0; a < size - 4; a++) {
            for (int b = a + 1; b < size - 3; b++) {
                for (int c = b + 1; c < size - 2; c++) {
                    for (int d = c + 1; d < size - 1; d++) {
                        for (int e = d + 1; e < size; e++) {
                            List<Card> fiveCards = Arrays.asList(
                                    cards.get(a), cards.get(b), cards.get(c), cards.get(d), cards.get(e));
                            HandValue current = evaluateFiveCardHand(fiveCards);
                            if (best == null || current.compareTo(best) > 0) {
                                best = current;
                            }
                        }
                    }
                }
            }
        }

        return best;
    }

    private HandValue evaluateFiveCardHand(List<Card> cards) {
        List<Integer> values = new ArrayList<>();
        Map<Integer, Integer> counts = new HashMap<>();
        Map<Card.Suit, Integer> suitCounts = new HashMap<>();

        for (Card card : cards) {
            int value = card.getRankValue();
            values.add(value);
            counts.put(value, counts.getOrDefault(value, 0) + 1);
            suitCounts.put(card.getSuit(), suitCounts.getOrDefault(card.getSuit(), 0) + 1);
        }

        Collections.sort(values, Collections.reverseOrder());

        boolean flush = false;
        for (Integer count : suitCounts.values()) {
            if (count == 5) {
                flush = true;
                break;
            }
        }

        Integer straightHigh = getStraightHigh(values);
        List<Integer> sortedGroups = getSortedGroupValues(counts);

        if (flush && straightHigh != null) {
            return new HandValue(8, Collections.singletonList(straightHigh), "Straight Flush");
        }

        if (sortedGroups.get(0) == 4) {
            int fourKind = getValueByCount(counts, 4).get(0);
            int kicker = getValueByCount(counts, 1).get(0);
            return new HandValue(7, Arrays.asList(fourKind, kicker), "Four of a Kind");
        }

        if (sortedGroups.get(0) == 3 && sortedGroups.size() > 1 && sortedGroups.get(1) == 2) {
            int trips = getValueByCount(counts, 3).get(0);
            int pair = getValueByCount(counts, 2).get(0);
            return new HandValue(6, Arrays.asList(trips, pair), "Full House");
        }

        if (flush) {
            return new HandValue(5, new ArrayList<>(values), "Flush");
        }

        if (straightHigh != null) {
            return new HandValue(4, Collections.singletonList(straightHigh), "Straight");
        }

        if (sortedGroups.get(0) == 3) {
            int trips = getValueByCount(counts, 3).get(0);
            List<Integer> kickers = getValueByCount(counts, 1);
            List<Integer> kickersSorted = new ArrayList<>();
            kickersSorted.add(trips);
            kickersSorted.addAll(kickers);
            return new HandValue(3, kickersSorted, "Three of a Kind");
        }

        if (sortedGroups.get(0) == 2 && sortedGroups.size() > 1 && sortedGroups.get(1) == 2) {
            List<Integer> pairs = getValueByCount(counts, 2);
            int kicker = getValueByCount(counts, 1).get(0);
            List<Integer> result = new ArrayList<>(pairs);
            result.add(kicker);
            return new HandValue(2, result, "Two Pair");
        }

        if (sortedGroups.get(0) == 2) {
            int pair = getValueByCount(counts, 2).get(0);
            List<Integer> kickers = getValueByCount(counts, 1);
            List<Integer> result = new ArrayList<>();
            result.add(pair);
            result.addAll(kickers);
            return new HandValue(1, result, "One Pair");
        }

        return new HandValue(0, new ArrayList<>(values), "High Card");
    }

    private Integer getStraightHigh(List<Integer> values) {
        List<Integer> unique = new ArrayList<>();
        for (Integer value : values) {
            if (!unique.contains(value)) {
                unique.add(value);
            }
        }

        if (unique.contains(14)) {
            unique.add(1);
        }

        Collections.sort(unique, Collections.reverseOrder());

        int runLength = 1;
        int bestHigh = -1;

        for (int index = 0; index < unique.size() - 1; index++) {
            if (unique.get(index) - 1 == unique.get(index + 1)) {
                runLength++;
                if (runLength >= 5) {
                    bestHigh = unique.get(index - 3);
                }
            } else if (unique.get(index).equals(unique.get(index + 1))) {
                continue;
            } else {
                runLength = 1;
            }
        }

        if (bestHigh == -1) {
            return null;
        }

        return bestHigh == 1 ? 5 : bestHigh;
    }

    private List<Integer> getSortedGroupValues(Map<Integer, Integer> counts) {
        List<Integer> groups = new ArrayList<>(counts.values());
        groups.sort(Collections.reverseOrder());
        return groups;
    }

    private List<Integer> getValueByCount(Map<Integer, Integer> counts, int count) {
        List<Integer> values = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : counts.entrySet()) {
            if (entry.getValue() == count) {
                values.add(entry.getKey());
            }
        }
        values.sort(Collections.reverseOrder());
        return values;
    }
}
