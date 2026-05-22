package Poker;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class PokerGameApp extends Application {

    private static final int STARTING_CHIPS = 1000;
    private static final int ANTE = 20;

    private final Deck deck = new Deck();
    private final HandEvaluator evaluator = new HandEvaluator();

    private final List<Card> playerCards = new ArrayList<>();
    private final List<Card> dealerCards = new ArrayList<>();
    private final List<Card> communityCards = new ArrayList<>();

    private HBox playerCardRow;
    private HBox dealerCardRow;
    private HBox communityRow;
    private Label playerHandLabel;
    private Label dealerHandLabel;
    private Label potLabel;
    private Label chipsLabel;
    private Label statusLabel;
    private Button flopButton;
    private Button turnButton;
    private Button riverButton;
    private Button showdownButton;
    private Button newHandButton;
    private Button foldButton;

    private int playerChips = STARTING_CHIPS;
    private int dealerChips = STARTING_CHIPS;
    private int pot;
    private int communityRevealCount;
    private boolean handActive;
    private boolean showdownComplete;

    @Override
    public void start(Stage stage) {
        Label title = new Label("Royal River Poker");
        title.getStyleClass().add("title-text");

        Label subtitle = new Label("Texas Hold'em style showdown with one clean round at a time.");
        subtitle.getStyleClass().add("subtitle-text");

        playerHandLabel = new Label("Your Hand");
        playerHandLabel.getStyleClass().add("section-title");

        dealerHandLabel = new Label("Dealer Hand");
        dealerHandLabel.getStyleClass().add("section-title");

        potLabel = new Label();
        potLabel.getStyleClass().add("stat-value");

        chipsLabel = new Label();
        chipsLabel.getStyleClass().add("stat-value");

        statusLabel = new Label("Press Deal to start a hand.");
        statusLabel.getStyleClass().add("status-label");
        statusLabel.setWrapText(true);

        playerCardRow = createCardRow();
        dealerCardRow = createCardRow();
        communityRow = createCommunityRow();

        VBox playerPanel = createCardPanel("Player", playerHandLabel, playerCardRow);
        VBox dealerPanel = createCardPanel("Dealer", dealerHandLabel, dealerCardRow);
        VBox boardPanel = createBoardPanel();

        VBox statsPanel = new VBox(10,
                createStatBlock("Pot", potLabel),
                createStatBlock("Chips", chipsLabel),
                statusLabel
        );
        statsPanel.getStyleClass().add("stats-panel");

        newHandButton = createActionButton("Deal New Hand", event -> startNewHand());
        flopButton = createActionButton("Reveal Flop", event -> revealCommunity(3));
        turnButton = createActionButton("Reveal Turn", event -> revealCommunity(4));
        riverButton = createActionButton("Reveal River", event -> revealCommunity(5));
        showdownButton = createActionButton("Showdown", event -> showdown());
        foldButton = createActionButton("Fold", event -> fold());

        HBox controls = new HBox(12, newHandButton, flopButton, turnButton, riverButton, showdownButton, foldButton);
        controls.setAlignment(Pos.CENTER);
        controls.getStyleClass().add("controls-bar");

        HBox panelsBox = new HBox(18, playerPanel, boardPanel, dealerPanel);
        panelsBox.setAlignment(Pos.CENTER);

        VBox root = new VBox(12, title, subtitle, statsPanel, controls, panelsBox);
        root.getStyleClass().add("app-root");
        root.setPadding(new Insets(18));
        root.setAlignment(Pos.TOP_CENTER);

        Scene scene = new Scene(root, 1200, 860);
        URL stylesheet = PokerGameApp.class.getResource("/poker.css");
        if (stylesheet != null) {
            scene.getStylesheets().add(stylesheet.toExternalForm());
        }

        stage.setTitle("Royal River Poker");
        stage.setScene(scene);
        stage.setMinWidth(980);
        stage.setMinHeight(720);
        stage.show();

        refreshUi();
        updateButtonState();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private VBox createCardPanel(String title, Label handLabel, HBox cardsRow) {
        Label header = new Label(title);
        header.getStyleClass().add("panel-heading");

        VBox panel = new VBox(12, header, handLabel, cardsRow);
        panel.getStyleClass().add("card-panel");
        panel.setAlignment(Pos.CENTER_LEFT);
        return panel;
    }

    private VBox createBoardPanel() {
        Label header = new Label("Community Cards");
        header.getStyleClass().add("panel-heading");

        VBox panel = new VBox(12, header, communityRow);
        panel.getStyleClass().add("board-panel");
        panel.setAlignment(Pos.CENTER);
        return panel;
    }

    private HBox createCardRow() {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private HBox createCommunityRow() {
        HBox row = new HBox(14);
        row.setAlignment(Pos.CENTER);
        return row;
    }

    private VBox createStatBlock(String label, Label value) {
        Label heading = new Label(label);
        heading.getStyleClass().add("stat-label");
        VBox block = new VBox(6, heading, value);
        block.getStyleClass().add("stat-block");
        return block;
    }

    private Button createActionButton(String text, javafx.event.EventHandler<javafx.event.ActionEvent> handler) {
        Button button = new Button(text);
        button.getStyleClass().add("action-button");
        button.setOnAction(handler);
        return button;
    }

    private void startNewHand() {
        if (playerChips < ANTE || dealerChips < ANTE) {
            statusLabel.setText("A player is out of chips. Restart the app for a fresh table.");
            handActive = false;
            showdownComplete = false;
            updateButtonState();
            return;
        }

        playerCards.clear();
        dealerCards.clear();
        communityCards.clear();

        deck.reset();
        pot = 0;
        communityRevealCount = 0;
        handActive = true;
        showdownComplete = false;

        playerChips -= ANTE;
        dealerChips -= ANTE;
        pot = ANTE * 2;

        for (int index = 0; index < 2; index++) {
            playerCards.add(deck.draw());
            dealerCards.add(deck.draw());
        }

        for (int index = 0; index < 5; index++) {
            communityCards.add(deck.draw());
        }

        statusLabel.setText("Hand dealt. Reveal the board or fold.");
        refreshUi();
        updateButtonState();
    }

    private void revealCommunity(int targetCount) {
        if (!handActive || showdownComplete) {
            return;
        }

        communityRevealCount = Math.max(communityRevealCount, targetCount);
        statusLabel.setText(switch (communityRevealCount) {
            case 3 ->
                    "Flop is on the board.";
            case 4 ->
                    "Turn card revealed.";
            case 5 ->
                    "River card revealed.";
            default ->
                    "";
        });
        refreshUi();
        updateButtonState();
    }

    private void showdown() {
        if (!handActive || showdownComplete || communityRevealCount < 5) {
            return;
        }

        showdownComplete = true;

        List<Card> playerBestCards = new ArrayList<>(communityCards);
        playerBestCards.addAll(playerCards);

        List<Card> dealerBestCards = new ArrayList<>(communityCards);
        dealerBestCards.addAll(dealerCards);

        HandValue playerValue = evaluator.evaluateBestHand(playerBestCards);
        HandValue dealerValue = evaluator.evaluateBestHand(dealerBestCards);
        playerHandLabel.setText("You: " + playerValue.getHandName());
        dealerHandLabel.setText("Dealer: " + dealerValue.getHandName());

        int result = playerValue.compareTo(dealerValue);
        if (result > 0) {
            playerChips += pot;
            statusLabel.setText("You win the pot with " + playerValue.getHandName() + ".");
        } else if (result < 0) {
            dealerChips += pot;
            statusLabel.setText("Dealer wins with " + dealerValue.getHandName() + ".");
        } else {
            playerChips += pot / 2;
            dealerChips += pot - (pot / 2);
            statusLabel.setText("Split pot. Both players made " + playerValue.getHandName() + ".");
        }

        pot = 0;
        handActive = false;
        refreshUi();
        updateButtonState();
    }

    private void fold() {
        if (!handActive || showdownComplete) {
            return;
        }

        dealerChips += pot;
        pot = 0;
        handActive = false;
        showdownComplete = true;
        statusLabel.setText("You folded. Dealer takes the pot.");
        refreshUi();
        updateButtonState();
    }

    private void refreshUi() {
        potLabel.setText("$" + pot);
        chipsLabel.setText("You $" + playerChips + "  |  Dealer $" + dealerChips);

        renderCards(playerCardRow, playerCards, false);
        renderCards(dealerCardRow, dealerCards, !showdownComplete);
        renderCommunityCards();

        if (!showdownComplete) {
            playerHandLabel.setText("Your Hand");
            dealerHandLabel.setText("Dealer Hand");
        }
    }

    private void renderCards(HBox row, List<Card> cards, boolean hideSecond) {
        row.getChildren().clear();

        for (int index = 0; index < cards.size(); index++) {
            boolean hidden = hideSecond;
            row.getChildren().add(createCardView(hidden ? null : cards.get(index), hidden));
        }
    }

    private void renderCommunityCards() {
        communityRow.getChildren().clear();

        for (int index = 0; index < 5; index++) {
            boolean visible = index < communityRevealCount;
            communityRow.getChildren().add(createCardView(visible ? communityCards.get(index) : null, !visible));
        }
    }

    private StackPane createCardView(Card card, boolean faceDown) {
        StackPane cardPane = new StackPane();
        cardPane.getStyleClass().addAll("poker-card");
        if (faceDown) {
            cardPane.getStyleClass().add("card-back");
        } else if (card != null) {
            cardPane.getStyleClass().add(card.getColorClass());
        }

        Text rankText = new Text(faceDown || card == null ? "?" : card.getRank().getShortName());
        rankText.getStyleClass().add("card-rank");

        Text suitText = new Text(faceDown || card == null ? "" : card.getSuit().getSymbol());
        suitText.getStyleClass().add("card-suit");

        VBox content = new VBox(4, rankText, suitText);
        content.setAlignment(Pos.CENTER);
        cardPane.getChildren().add(content);
        return cardPane;
    }

    private void updateButtonState() {
        boolean canStart = playerChips >= ANTE && dealerChips >= ANTE;
        newHandButton.setDisable(!canStart);

        boolean canAct = handActive && !showdownComplete;
        flopButton.setDisable(!canAct || communityRevealCount >= 3);
        turnButton.setDisable(!canAct || communityRevealCount < 3 || communityRevealCount >= 4);
        riverButton.setDisable(!canAct || communityRevealCount < 4 || communityRevealCount >= 5);
        showdownButton.setDisable(!canAct || communityRevealCount < 5);
        foldButton.setDisable(!canAct);
    }
}
