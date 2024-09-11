package uk.ac.soton.comp1206.game;

import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBlockCoordinate;
import uk.ac.soton.comp1206.event.*;
import uk.ac.soton.comp1206.ui.GameWindow;
import java.util.HashSet;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * The Game class handles the main logic, state and properties of the TetrECS game. Methods to manipulate the game state
 * and to handle actions made by the player should take place inside this class.
 */
public class Game {

    private static final Logger logger = LogManager.getLogger(Game.class);

    private GameWindow gameWindow;
    /**
     * Number of rows
     */
    protected final int rows;
    /**
     * Number of columns
     */
    protected final int cols;
    /**
     * The grid model linked to the game
     */
    private final Grid grid;
    protected ScheduledFuture<?> gameLoop;
    /**
     * Calls a gameLoop method
     */
    protected ScheduledExecutorService executorService;
    /**
     * initialising the lives, score, multiplier and, level simple integer properties
     */

    private SimpleIntegerProperty lives = new SimpleIntegerProperty(3);
    private SimpleIntegerProperty score = new javafx.beans.property.SimpleIntegerProperty(0);
    private SimpleIntegerProperty multiplier = new SimpleIntegerProperty(1);
    private SimpleIntegerProperty level = new SimpleIntegerProperty(0);
    /**
     * field variable for the line cleared listener
     */
    private LineClearedListener lineClearedListener = null;
    /**
     * field variable for the next piece listener
     */
    private NextPieceListener nextPieceListener = null;
    private GameLoopListener gameLoopListener = null;

    private GameOverListener gameOverListener = null;
    private Random random = new Random();
    private boolean isGameLoopScheduled = false;
    /**
     * current piece being placed
     */
    private GamePiece currentPiece;
    /**
     * piece to be played
     */
    private GamePiece followingPiece;

    //private ScoresScene scoresScene;
    private HighScoreListener highScoreListener;


    /**
     * Create a new game with the specified rows and columns. Creates a corresponding grid model.
     *
     * @param cols number of columns
     * @param rows number of rows
     */
    public Game(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;

        //Create a new grid model to represent the game state
        this.grid = new Grid(cols, rows);
        this.executorService = Executors.newSingleThreadScheduledExecutor();
    }

    /**
     * Start the game
     */
    public void start() {
        logger.info("Starting game");
        initialiseGame();
        gameLoop = executorService.schedule(this::gameLoop, getTimerDelay(), TimeUnit.MILLISECONDS);
        gameLoopListener();
    }

    /**
     * method to set line cleared listeners
     * @param listener - the listeners
     */
    public void setLineClearedListener(LineClearedListener listener) {
        lineClearedListener = listener;
    }

    /**
     * method which notifies the listener when a line has been cleared
     * @param coordinates - of the blocks cleared
     */
    public void lineCleared(HashSet<GameBlockCoordinate> coordinates) {
        if (lineClearedListener != null) {
            lineClearedListener.lineCleared(coordinates);
        }
    }

    /**
     * Sets the listener for game loop events.
     * @param listener - the listener to be set
     */

    public void setGameLoopListener(GameLoopListener listener) {
        gameLoopListener = listener;
    }

    /**
     *  Triggers the game loop listener's action based on the current game loop delay.
     */
    public void gameLoopListener() {
        if (gameLoopListener != null) {
            gameLoopListener.gameLoop(getTimerDelay());
        }
    }

    /**
     * sets the listener for game over so that the scene can be switched to the score/game over scene
     * @param listener = the listener which is going to be updated
     */
    public void setGameOverListener(GameOverListener listener){
        gameOverListener = listener;
    }

    /**
     *  Notifies the registered listener that the game has ended.
     */
    public void gameOverListener(){
        if (gameOverListener != null){
            gameOverListener.gameOver();
        }
    }

    /**
     * sets the next piece listener for the game - notified when new piece is available
     * @param nextListener
     */
    public void setNextPiece(NextPieceListener nextListener) {
        nextPieceListener = nextListener;
    }

    public void setHighScoreListener(HighScoreListener listener){highScoreListener = listener;}

    public void highScoreListener(){
        if(highScoreListener != null){
            highScoreListener.highScore();
        }
    }


    /**
     * Initialise a new game and set up anything that needs to be done at the start
     */
    public void initialiseGame() {
        logger.info("Initialising game");
        currentPiece = spawnPiece();
        logger.info("creating current piece");
        followingPiece = spawnPiece();
        logger.info("creating following piece");
        nextPiece();
    }

    public void nextPiece() {
        logger.info("The next piece is: {}", currentPiece);
        currentPiece = followingPiece;
        followingPiece = spawnPiece();
        if (nextPieceListener != null) {
            nextPieceListener.nextPiece(currentPiece, followingPiece);
        }
    }


    public GamePiece spawnPiece() {
        var maxPieces = GamePiece.PIECES;
        var randomPiece = random.nextInt(maxPieces);
        logger.info("picking random piece: {}", randomPiece);
        var piece = GamePiece.createPiece(randomPiece);
        return piece;
    }

    /**
     * Handle what should happen when a particular block is clicked
     *
     * @param gameBlock the block that was clicked
     */
    public void blockClicked(GameBlock gameBlock) {
        //Get the position of this block
        int x = gameBlock.getX();
        int y = gameBlock.getY();

        if (grid.canPlayPiece(currentPiece, x, y)) {
            //place piece
            grid.playPiece(currentPiece, x, y);
            afterPiece();
            nextPiece();
            gameLoop.cancel(false);
            scheduleGameLoop();
            gameLoopListener();
            MultiMedia.playAudio("place.wav");
        } else {
            //cannot place piece
            MultiMedia.playAudio("fail.wav");
        }
    }

    /**
     * method to clear lines after a block has been placed
     */
    public void afterPiece() {
        //HashSet<SimpleIntegerProperty> clearingBlocks = new HashSet<>(); // Hashset of game blocks to be cleared
        HashSet<GameBlockCoordinate> blocksCleared = new HashSet<>(); //hashset of game block coordinates (as shown in the UI) so that they are not counted twice
        //counts number of full lines and blocks
        int fullLines = 0;
        int clearingBlocks = 0;
        //checks each row and whether the row is full
        for (int y = 0; y < rows; y++) {
            boolean isRowFull = true;
            for (int x = 0; x < cols; x++) {
                logger.info("checking if row is full..");
                //if at any point the iterator reaches a point where the array is zero, the loop breaks
                if (grid.get(x, y) == 0) {
                    logger.info("row is not full");
                    isRowFull = false;
                    break;
                }
            }
            //if row is full, coordinates are added to the hashset.
            if (isRowFull) {
                logger.info("row is full");
                fullLines++;
                for (int x = 0; x < cols; x++) {
                    blocksCleared.add(new GameBlockCoordinate(x, y));
                }
            }
        }

        for (int x = 0; x < cols; x++) {
            boolean isColumnFull = true;
            logger.info("checking if column is full");
            for (int y = 0; y < rows; y++) {
                if (grid.get(x, y) == 0) {
                    logger.info("column is not full");
                    isColumnFull = false;
                    break;
                }
            }
            if (isColumnFull) {
                logger.info("column is full");
                fullLines++;
                for (int y = 0; y < rows; y++) {
                    blocksCleared.add(new GameBlockCoordinate(x, y));
                }
            }
        }

        // Clears the blocks
        for (GameBlockCoordinate coordinate : blocksCleared) {
            clearingBlocks++;
            logger.info("clearing blocks...");
            grid.set(coordinate.getX(), coordinate.getY(), 0);
        }
        score(fullLines, clearingBlocks);

        lineCleared(blocksCleared);
    }

    /**
     * method to calculate the score
     *
     * @param lines  number of lines cleared
     * @param blocks number of blocks cleared
     */

    public void score(int lines, int blocks) {
        int thisScore = (lines * blocks * 10 * multiplier.get());
        //method to update the total score
        logger.info("increasing score...");
        increaseScore(thisScore);

        //calculates the multiplier
        if (thisScore > 0) {
            multiplier.set(multiplier.get() + 1);
        } else {
            multiplier.set(1);
        }
        //calculates the level
        level.set(score.get() / 1000);
        MultiMedia.playAudio("level.wav");
    }


    /**
     * getter method to get the value of score
     *
     * @return
     */
    public int getScore() {
        return score.get();
    }

    public int getLevel() {
        return level.get();
    }


    /**
     * method to increase the total score
     *
     * @param scoreIncrease - increase score by this
     */
    public void increaseScore(int scoreIncrease) {
        score.set(score.add(scoreIncrease).get());
    }

    public int getLives() {
        return lives.get();
    }

    /**
     * rotate the current piece
     *
     * @param rotations - number of rotations
     */
    public void rotateCurrentPiece(int rotations) {
        currentPiece.rotate(rotations);
    }

    public void swapCurrentPiece() {
        logger.info("swapping current and following pieces...");
        GamePiece thisPiece = currentPiece;
        currentPiece = followingPiece;
        followingPiece = thisPiece;
    }

    /**
     * method to get the scoreProperty so that it can be used in the UI for binding purposes.
     *
     * @return
     */
    public IntegerProperty scoreProperty() {
        return score;
    }

    public IntegerProperty levelProperty() {
        return level;
    }

    public IntegerProperty livesProperty() {
        return lives;
    }

    public IntegerProperty getMultiplierProperty() {
        return multiplier;
    }

    /**
     * Get the grid model inside this game representing the game state of the board
     *
     * @return game grid model
     */
    public Grid getGrid() {
        return grid;
    }

    /**
     * Get the number of columns in this game
     *
     * @return number of columns
     */
    public int getCols() {
        return cols;
    }

    /**
     * Get the number of rows in this game
     *
     * @return number of rows
     */
    public int getRows() {
        return rows;
    }

    /**
     * method to get the following piece
     *
     * @returns following piece
     */

    public GamePiece getFollowingPiece() {
        return followingPiece;
    }

    /**
     * method to get the current piece
     *
     * @returns current piece
     */
    public GamePiece getCurrentPiece() {
        return currentPiece;
    }

    public int getTimerDelay() {
        return Math.max(2500, (12000-(500 * getLevel())));
    }
    private void scheduleGameLoop() {
        if (!isGameLoopScheduled) {
            isGameLoopScheduled = true;
        }
        gameLoop = executorService.schedule(this::gameLoop, getTimerDelay(), TimeUnit.MILLISECONDS);
    }

    public void gameLoop() {
        if (getLives() > 0) {
        Platform.runLater(() -> {
                lives.set(lives.get() - 1);
                MultiMedia.playAudio("lifelose.wav");
                // Ensure you only update UI components or properties from the FX thread.
                randomPiece();
                multiplier.set(1);
                gameLoopListener();
            });
                isGameLoopScheduled = false;
                scheduleGameLoop();
            } else {
            Platform.runLater(() -> {
                gameLoop.cancel(false); // Attempt to cancel the current scheduled task
                executorService.shutdown(); // Shutdown the executor service
                gameOverListener();
            });
        }
    }
    private void randomPiece(){
        currentPiece = spawnPiece();
        followingPiece = spawnPiece();
        nextPieceListener.nextPiece(currentPiece,followingPiece);
    }
    public void stopGame(){
        executorService.shutdownNow();
    }

}


