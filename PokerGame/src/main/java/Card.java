package Poker;

public class Card {

    public enum Suit {
        CLUBS("\u2663", "club"),
        DIAMONDS("\u2666", "diamond"),
        HEARTS("\u2665", "heart"),
        SPADES("\u2660", "spade");

        private final String symbol;
        private final String cssClass;

        Suit(String symbol, String cssClass) {
            this.symbol = symbol;
            this.cssClass = cssClass;
        }

        public String getSymbol() {
            return symbol;
        }

        public String getCssClass() {
            return cssClass;
        }
    }

    public enum Rank {
        TWO(2, "2"),
        THREE(3, "3"),
        FOUR(4, "4"),
        FIVE(5, "5"),
        SIX(6, "6"),
        SEVEN(7, "7"),
        EIGHT(8, "8"),
        NINE(9, "9"),
        TEN(10, "10"),
        JACK(11, "J"),
        QUEEN(12, "Q"),
        KING(13, "K"),
        ACE(14, "A");

        private final int value;
        private final String shortName;

        Rank(int value, String shortName) {
            this.value = value;
            this.shortName = shortName;
        }

        public int getValue() {
            return value;
        }

        public String getShortName() {
            return shortName;
        }
    }

    private final Rank rank;
    private final Suit suit;

    public Card(Rank rank, Suit suit) {
        this.rank = rank;
        this.suit = suit;
    }

    public Rank getRank() {
        return rank;
    }

    public Suit getSuit() {
        return suit;
    }

    public int getRankValue() {
        return rank.getValue();
    }

    public String getFaceText() {
        return rank.getShortName() + suit.getSymbol();
    }

    public String getColorClass() {
        return suit == Suit.HEARTS || suit == Suit.DIAMONDS ? "red-suit" : "black-suit";
    }

    @Override
    public String toString() {
        return getFaceText();
    }
}
