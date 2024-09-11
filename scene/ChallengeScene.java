package uk.ac.soton.comp1206.scene;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.App;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBlockCoordinate;
import uk.ac.soton.comp1206.component.GameBoard;
import uk.ac.soton.comp1206.component.PieceBoard;
import uk.ac.soton.comp1206.event.HighScoreListener;
import uk.ac.soton.comp1206.event.NextPieceListener;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.game.MultiMedia;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

import java.util.HashSet;
import java.util.List;

import static javafx.scene.paint.Color.BLUE;

/**
 * The Single Player challenge scene. Holds the UI for the single player challenge mode in the game.
 */
public class ChallengeScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(MenuScene.class);

    private Game game;
    private PieceBoard currentPieceBoard;
    private PieceBoard nextPieceBoard;
    private Rectangle timeBar;
    protected HBox timer;

    private GameBoard board;
    private int x;
    private int y;

    /**
     * Create a new Single Player challenge scene
     * @param gameWindow the Game Window
     */
    public ChallengeScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating Challenge Scene");
    }

    /**
     * Build the Challenge window
     */
    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());

        setupGame();

        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());

        var challengePane = new StackPane();
        challengePane.setMaxWidth(gameWindow.getWidth());
        challengePane.setMaxHeight(gameWindow.getHeight());
        challengePane.getStyleClass().add("menu-background");
        root.getChildren().add(challengePane);

        var mainPane = new BorderPane();
        challengePane.getChildren().add(mainPane);

        board = new GameBoard(game.getGrid(),gameWindow.getWidth()/2,gameWindow.getWidth()/2);
        mainPane.setCenter(board);

        //Hbox to set lives and score horizontally at the top
        HBox topBox = new HBox();
        topBox.setAlignment(Pos.TOP_CENTER);
        topBox.setHgrow(topBox,Priority.ALWAYS);
        topBox.setSpacing(50);
        mainPane.setTop(topBox);

        //this is where the scores are shown in the UI
        VBox scoreBoard = new VBox();
        scoreBoard.setPadding(new Insets(10, 10, 0, 90));
        scoreBoard.setAlignment(Pos.TOP_LEFT);
        scoreBoard.setTranslateX(30);

        Label scoreLabel = new Label();
        scoreLabel.textProperty().bind(game.scoreProperty().asString("Score: %d"));
        scoreLabel.setFont(new Font(42));
        scoreLabel.getStyleClass().add("smalltitle");

        //adding these elements to the stackpane

        scoreBoard.getChildren().add(scoreLabel);

        //TITLE
        Text bigTitle = new Text("CHALLENGE MODE");
        bigTitle.getStyleClass().add("title");
        bigTitle.setTranslateY(10);
        bigTitle.setTranslateX(50);


        //LIVES
        VBox livesBoard = new VBox();
        livesBoard.setPadding(new Insets(10,0,0,0));
        livesBoard.setAlignment(Pos.TOP_RIGHT);
        livesBoard.setTranslateX(-10);

        Label livesLabel = new Label("Lives Left: 3");
        livesLabel.textProperty().bind(game.livesProperty().asString("Lives: %d"));
        livesLabel.setFont(new Font(42));
        livesLabel.getStyleClass().add("smalltitle");

        livesBoard.getChildren().add(livesLabel);


        //adding lives to the top of the game screen - HBox
        topBox.getChildren().addAll(livesBoard, bigTitle, scoreBoard);


        Label highscore = new Label("High Score:");
        highscore.getStyleClass().add("title");
        Label newHighScore = new Label("           " + getHighScore());
        newHighScore.getStyleClass().add("hiscore");

        VBox highScoreBOX = new VBox();
        highScoreBOX.setAlignment(Pos.TOP_LEFT);
        highScoreBOX.getChildren().add(highscore);
        highScoreBOX.getChildren().add(newHighScore);

        VBox levelsBoard = new VBox();
        levelsBoard.setAlignment(Pos.TOP_RIGHT);
        //LEVELS
        Label levelLabel = new Label();
        levelLabel.textProperty().bind(game.levelProperty().asString("Level: %d"));
        levelLabel.setFont(new Font(42));
        levelLabel.getStyleClass().add("heading");

        levelsBoard.getChildren().addAll(highScoreBOX, levelLabel);

        VBox multBoard = new VBox();
        multBoard.setAlignment(Pos.TOP_RIGHT);
        //MULTIPLIER
        Label multLable = new Label("multiplier: %d");
        multLable.textProperty().bind(game.getMultiplierProperty().asString("Multiplier: %d"));
        multLable.setFont(new Font(42));
        multLable.getStyleClass().add("heading");

        multBoard.getChildren().add(multLable);

        //button to exit and shut down the game
        Button button = new Button("Exit");
        button.getStyleClass().add("menuItem");
        button.setOnAction(this::endGame);
        button.setTranslateY(10);
        button.setTranslateX(10);

        //PIECEBOARDS
        currentPieceBoard = new PieceBoard(3,3,150,150);
        //getBlock at 1,1 because the size of the game board is 3,3, so 1,1 is the centre.
        currentPieceBoard.getBlock(1,1).centerCircle();
        currentPieceBoard.setPadding(new Insets(15,0,0,0)); //top,right,bottom,left
        nextPieceBoard = new PieceBoard(3,3,75,75);
        nextPieceBoard.setPadding(new Insets(10));
        nextPieceBoard.setTranslateX(nextPieceBoard.getTranslateX() - 28);
        nextPieceBoard.setTranslateY(-10);
        currentPieceBoard.setTranslateY(-10);
        //mouse click actions
        currentPieceBoard.setOnMouseClicked(e -> this.rotateRight());
        nextPieceBoard.setOnMouseClicked(e -> this.swap());


        VBox pieceBoards = new VBox();
        pieceBoards.setAlignment(Pos.CENTER_RIGHT);
        pieceBoards.setPadding(new Insets(20,30,0,0));
        pieceBoards.getChildren().addAll(highScoreBOX,levelsBoard,multBoard, currentPieceBoard, nextPieceBoard,button);

        mainPane.setRight(pieceBoards);

        game.setLineClearedListener(board::fadeOut);

        //TIMER
        // Countdown bar
        timer = new HBox();
        timeBar = new Rectangle();
        timeBar.setHeight(20);
        timer.getChildren().add(timeBar);

        mainPane.setBottom(timer);
        //Handle block on gameboard grid being clicked
        board.setOnBlockClick(this::blockClicked);

    }
    protected void timer(int time) {
        // Ensure the timer bar is properly initialized
        timeBar.setWidth(gameWindow.getWidth()); // Reset the width for a new game or round
        timeBar.setFill(Color.GREEN); // Start with green color
        timeBar.setHeight(10); // Set a fixed height

        // Create a timeline for smooth transition of color and width
        Timeline timeline = new Timeline();

        // Create key values for the width and color at different stages of the countdown
        KeyValue widthAtHalf = new KeyValue(timeBar.widthProperty(), gameWindow.getWidth() * 0.5, Interpolator.LINEAR);
        KeyValue colorAtHalf = new KeyValue(timeBar.fillProperty(), Color.YELLOW, Interpolator.LINEAR);
        KeyValue widthAtEnd = new KeyValue(timeBar.widthProperty(), 0, Interpolator.LINEAR);
        KeyValue colorAtEnd = new KeyValue(timeBar.fillProperty(), Color.RED, Interpolator.LINEAR);

        // Create key frames and add them to the timeline
        KeyFrame startFrame = new KeyFrame(Duration.ZERO,
                new KeyValue(timeBar.widthProperty(), gameWindow.getWidth()),
                new KeyValue(timeBar.fillProperty(), Color.GREEN));
        KeyFrame halfFrame = new KeyFrame(Duration.millis(time * 0.5), widthAtHalf, colorAtHalf);
        KeyFrame endFrame = new KeyFrame(Duration.millis(time), widthAtEnd, colorAtEnd);

        timeline.getKeyFrames().addAll(startFrame, halfFrame, endFrame);
        timeline.play();
    }


    /**
     * Handle when a block is clicked
     * @param gameBlock the Game Block that was clocked
     */
    private void blockClicked(GameBlock gameBlock) {
        game.blockClicked(gameBlock);
    }
    /**
     * Setup the game object and model
     */
    public void setupGame() {
        logger.info("Starting a new challenge");

        //Start new game
        game = new Game(5, 5);
    }
    public void nextPiece(GamePiece piece, GamePiece followingPiece) {
        currentPieceBoard.showPiece(game.getCurrentPiece());
        nextPieceBoard.showPiece(game.getFollowingPiece());
    }


    /**
     * Initialise the scene and start the game
     */
    @Override
    public void initialise() {
        logger.info("Initialising Challenge");
        MultiMedia.playBackgroundMusic("game_start.wav");
        game.setGameLoopListener(this::timer);
        game.setNextPiece(this::nextPiece);
        game.setGameOverListener(this::gameOver); // Ensure this is called
        game.setHighScoreListener(this::getHighScore);
        logger.debug("Game Over Listener set"); // Debug statement
        addKeyboardListeners();

        // Ensure the root element can receive focus and request focus
        scene.getRoot().setFocusTraversable(true);
        scene.getRoot().requestFocus();

        game.start();
    }

    private void gameOver() {
        logger.info("Game Over Triggered");
        gameWindow.scoreScene(game);
    }

    /**
     * keyboard listener to handle keyboard input
     */
    public void addKeyboardListeners() {
        scene.setOnKeyPressed(event -> {
            KeyCode code = event.getCode();
            switch (code) {
                case ESCAPE:
                    gameWindow.startMenu();
                    game.stopGame();
                    break;
                case UP:
                case W:
                    if (y > 0) {
                        y--;
                        board.hoveredTile(board.getBlock(x, y));  //calls hover tile which paints a tile if the keyboard is hovering over it
                    }
                    break;
                case DOWN:
                case S:
                    if (y < game.getRows() - 1) {
                        y++;
                        board.hoveredTile(board.getBlock(x, y));
                    }
                    break;
                case LEFT:
                case A:
                    if (x > 0) {
                        x--;
                        board.hoveredTile(board.getBlock(x, y));
                    }
                    break;
                case RIGHT:
                case D:
                    if (x < game.getCols() - 1) {
                        x++;
                        board.hoveredTile(board.getBlock(x, y));
                    }
                    break;
                case ENTER:
                case X:
                    blockClicked(board.getBlock(x, y));
                    break;
                case Q:
                case E:
                    rotateRight();
                    break;
                case Z:
                case C:
                    rotateLeft();
                    break;
                case SPACE:
                case R:
                    swap();
                    break;
                default:
                    break;
            } event.isConsumed();
        });
    }
    /**
     * method to exit game when exit button is clicked
     * @param event
     */
    private void endGame(ActionEvent event){
        App.getInstance().shutdown();
    }

    /**
     * method to rotate a block right
     */
    private void rotateRight(){
        MultiMedia.playAudio("rotate.wav");
        game.rotateCurrentPiece(1);
        currentPieceBoard.showPiece(game.getCurrentPiece());
        logger.info("rotating right");
    }

    private void rotateLeft(){
        MultiMedia.playAudio("rotate.wav");
        game.rotateCurrentPiece(3);
        currentPieceBoard.showPiece(game.getCurrentPiece());
        logger.info("rotating left");
    }
    private void swap(){
        MultiMedia.playAudio("rotate.wav");
        game.swapCurrentPiece();
        currentPieceBoard.showPiece(game.getCurrentPiece());
        nextPieceBoard.showPiece(game.getFollowingPiece());
        logger.info("swapping");
    }
    /**
     * Retrieves the highest score from the score list as a string.
     * Ensures safe handling of cases where no scores are available.
     *
     * @return The highest score in string format, or "No scores" if the list is empty.
     */
    public String getHighScore() {
        // Load scores using a static method; ensure this method is accessible and correctly declared.
        List<Pair<String, Integer>> scores = ScoresScene.loadScores();

        // Check if the scores list is empty to avoid IndexOutOfBoundsException
        if (scores.isEmpty()) {
            return "No scores";
        }

        // Sort the scores in descending order based on the score value
        scores.sort((pair1, pair2) -> pair2.getValue().compareTo(pair1.getValue()));

        // Return the highest score converted to String
        return Integer.toString(scores.get(0).getValue());
    }

}
