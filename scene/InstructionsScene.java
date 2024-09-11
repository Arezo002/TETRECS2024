package uk.ac.soton.comp1206.scene;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.App;
import uk.ac.soton.comp1206.component.PieceBoard;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

public class InstructionsScene extends BaseScene{

    private static final Logger logger = LogManager.getLogger(InstructionsScene.class);
    /**
     * Create a new scene, passing in the GameWindow the scene will be displayed in
     *
     * @param gameWindow the game window
     */
    public InstructionsScene(GameWindow gameWindow) {
        super(gameWindow);
    }

    @Override
    public void initialise() {
        addKeyboardListeners();
    }

    @Override
    public void build() {

        logger.info("Building " + this.getClass().getName());

        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());

        var instructionsPane = new StackPane();
        instructionsPane.setMaxWidth(gameWindow.getWidth());
        instructionsPane.setMaxHeight(gameWindow.getHeight());
        instructionsPane.getStyleClass().add("instructions");

        var box1 = new VBox();
        box1.setAlignment(Pos.TOP_CENTER);
        instructionsPane.setAlignment(box1,Pos.TOP_CENTER);

        var gameInstructions = new Text("Instructions");
        gameInstructions.getStyleClass().add("heading");
        box1.getChildren().add(gameInstructions);

        Image backgroundImage = new Image(InstructionsScene.class.getResource("/images/2.jpg").toExternalForm());
        ImageView backgroundImageView = new ImageView(backgroundImage);
        backgroundImageView.setFitHeight(gameWindow.getHeight()); // Set the height of the ImageView to the height of the image
        backgroundImageView.setFitWidth(gameWindow.getWidth()); // Set the width of the ImageView to the width of the image
        instructionsPane.getChildren().add(backgroundImageView);

        var instructions = new Image(InstructionsScene.class.getResource("/images/Instructions.png").toExternalForm());
        ImageView instructionsImage = new ImageView(instructions);
        instructionsImage.setFitHeight(300);
        instructionsImage.setFitWidth(500);
        instructionsImage.setTranslateY(-90);
        instructionsPane.getChildren().add(instructionsImage);

        // Add the background image behind the instructions image
        instructionsPane.getChildren().add(box1);

// Create a VBox to hold the game pieces
        VBox grid = new VBox();
        grid.setAlignment(Pos.BOTTOM_CENTER);
        grid.setSpacing(10);
        grid.setPadding(new Insets(0, 0, 20,0));

        // Create a Text element for the heading
        Text piecesText = new Text("Game Pieces");
        piecesText.getStyleClass().add("heading");
        grid.getChildren().add(piecesText);

// Dynamically generate game pieces
        for (int x = 0; x < 3; x++) {
            // Create an HBox for each row of game pieces
            HBox hBox = new HBox();
            grid.getChildren().add(hBox);
            hBox.setAlignment(Pos.BOTTOM_CENTER);
            hBox.setSpacing(10);

            for (int y = 0; y < 5; y++) {
                // Create a PieceBoard for each game piece
                PieceBoard pieceBoard = new PieceBoard(3, 3,50,50);

                // Create a game piece and display it on the PieceBoard
                GamePiece gamePiece = GamePiece.createPiece(x * 5 + y);
                pieceBoard.showPiece(gamePiece);

                // Add the PieceBoard to the HBox
                hBox.getChildren().add(pieceBoard);
            }
        }

        instructionsPane.getChildren().add(grid);

        // Add the StackPane to the root GamePane
        root.getChildren().add(instructionsPane);
    }
    private void addKeyboardListeners() {
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                gameWindow.startMenu();
            }
        });
    }
}
