package uk.ac.soton.comp1206.scene;
import javafx.application.Platform;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.App;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.game.MultiMedia;
import uk.ac.soton.comp1206.network.Communicator;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.ui.ScoreList;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a scene for displaying game over and high scores in a JavaFX application.
 * This scene handles both the local score display and interaction for new high scores, allowing
 * players to enter their names if they achieve a high score.
 */

public class ScoresScene extends BaseScene{
    private static final Logger logger = LogManager.getLogger(ScoresScene.class);
    private SimpleListProperty<Pair<String,Integer>> localScores;
    private ObservableList<Pair<String,Integer>> localList = FXCollections.observableArrayList();
    //private SimpleListProperty<Pair<String,Integer>> remoteScores;
    private ObservableList<Pair<String,Integer>> remoteList = FXCollections.observableArrayList();

    private ScoreList scoreList;
    private Game game;
    private VBox vBox;
    //private Communicator communicator;
    //private ScoreList onlineList;

    /**
     * Constructs a new ScoresScene.
     *
     * @param gameWindow the game window in which the scene is displayed
     * @param game       the game instance
     */
    public ScoresScene(GameWindow gameWindow, Game game) {
        super(gameWindow);

        this.game = game;

        //communicator = new Communicator("ws://ofb-labs.ecs.soton.ac.uk:9700");

        logger.info("ScoresScene initialized with game window and game reference.");
    }
    /**
     * Initialises the scene by checking high scores asynchronously.
     */
    @Override
    public void initialise() {
        Platform.runLater(this::checkHighScores);
        MultiMedia.playBackgroundMusic("end.wav");
        logger.info("Initializing ScoresScene and checking high scores.");
        //Platform.runLater(this::loadOnlineScores);
    }
    /**
     * Builds the UI elements of the scene.
     */
    @Override
    public void build() {
        root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());
        var scoresPane = new StackPane();
        scoresPane.setMaxWidth(gameWindow.getWidth());
        scoresPane.setMaxHeight(gameWindow.getHeight());
        scoresPane.getStyleClass().add("gameover-background");
        root.getChildren().add(scoresPane);

        vBox = new VBox(20);  // Add spacing for better layout
        vBox.setAlignment(Pos.CENTER);

        var gameOver = new Text("GAME OVER!");
        gameOver.getStyleClass().add("bigtitle");

        var highScores = new Text("HIGH SCORES");
        highScores.getStyleClass().add("title");

        vBox.getChildren().addAll(gameOver, highScores);
        scoresPane.getChildren().add(vBox);

        // Local and Online Score Headers
        var localOnlineScores = new HBox(100);  // Increased spacing
        localOnlineScores.setAlignment(Pos.CENTER);

        Text localScoresText = new Text("Local Scores");
        localScoresText.getStyleClass().add("heading");

        Text onlineScores = new Text("Online Scores");
        onlineScores.getStyleClass().add("heading");

        localOnlineScores.getChildren().addAll(localScoresText, onlineScores);
        vBox.getChildren().add(localOnlineScores);  // Add this under the title


        // Prepare score list component
        scoreList = new ScoreList();
        localList = FXCollections.observableArrayList(loadScores());
        localScores = new SimpleListProperty<>(localList);
        scoreList.scoreListProperty().bind(localScores);

//        onlineList = new ScoreList();
//        remoteList = FXCollections.observableArrayList(remoteScores);
//        remoteScores = new SimpleListProperty<>(remoteList);
//        onlineList.scoreListProperty().bind(remoteScores);

        // Display score list
        var localBox = new VBox();
        localBox.setAlignment(Pos.BOTTOM_LEFT);
        localBox.getChildren().add(scoreList);
        localBox.setTranslateX(-150);
        localBox.getStyleClass().add("heading");


        //online score box
//        var onlineBox = new VBox();
//        onlineBox.setAlignment(Pos.BOTTOM_RIGHT);
//        onlineBox.getChildren().add(onlineList);
//        onlineBox.setTranslateX(150);
//        onlineBox.getStyleClass().add("heading");
//
//        onlineBox.getChildren().add(onlineList);


        //exit game and back to menu buttons presented after game over
        var buttons = new HBox();
        buttons.setSpacing(100);
        buttons.getStyleClass().add("Button");
        buttons.setAlignment(Pos.BOTTOM_CENTER);

        Button Exit = new Button("EXIT GAME");
        Button menu = new Button("MENU");
        Exit.setOnAction(this::endGame);
        menu.setOnAction(this::openMenuScene);

        buttons.getChildren().addAll(menu, Exit);


        // Add everything to the main pane
        vBox.getChildren().addAll(localBox,buttons);
    }

    /**
     * created this method to show the high scores after a new high score is added
     */
    private void buildScoreListUI() {
        var gameOver = new Text("GAME OVER!");
        gameOver.getStyleClass().add("bigtitle");

        var highScores = new Text("HIGH SCORES");
        highScores.getStyleClass().add("title");

        vBox.getChildren().addAll(gameOver, highScores);

        // Local and Online Score Headers
        var localOnlineScores = new HBox(100);  // Increased spacing
        localOnlineScores.setAlignment(Pos.CENTER);

        Text localScoresText = new Text("Local Scores");
        localScoresText.getStyleClass().add("heading");

        Text onlineScores = new Text("Online Scores");
        onlineScores.getStyleClass().add("heading");

        localOnlineScores.getChildren().addAll(localScoresText, onlineScores);
        vBox.getChildren().add(localOnlineScores);  // Add this under the title


        // Prepare score list component
        scoreList = new ScoreList();
        localList = FXCollections.observableArrayList(loadScores());
        localScores = new SimpleListProperty<>(localList);
        scoreList.scoreListProperty().bind(localScores);

//        onlineList = new ScoreList();
//        remoteList = FXCollections.observableArrayList(remoteScores);
//        remoteScores = new SimpleListProperty<>(remoteList);
//        onlineList.scoreListProperty().bind(remoteScores);

        // Display score list
        var localBox = new VBox();
        localBox.setAlignment(Pos.BOTTOM_LEFT);
        localBox.getChildren().add(scoreList);
        localBox.setTranslateX(-150);
        localBox.getStyleClass().add("heading");


        //online score box
//        var onlineBox = new VBox();
//        onlineBox.setAlignment(Pos.BOTTOM_RIGHT);
//        onlineBox.getChildren().add(onlineList);
//        onlineBox.setTranslateX(150);
//        onlineBox.getStyleClass().add("heading");
//
//        onlineBox.getChildren().add(onlineList);

        //exit game and back to menu buttons presented after game over
        var buttons = new HBox();
        buttons.setSpacing(100);
        buttons.getStyleClass().add("Button");
        buttons.setAlignment(Pos.BOTTOM_CENTER);

        Button Exit = new Button("EXIT GAME");
        Button menu = new Button("MENU");
        Exit.setOnAction(this::endGame);
        menu.setOnAction(this::openMenuScene);

        buttons.getChildren().addAll(menu, Exit);


        // Add everything to the main pane
        vBox.getChildren().addAll(localBox, buttons);
    }

    /**
     * Writes the scores to a local file, sorting them by descending order.
     *
     * @param firstScores list of scores to be written
     */
    public static void writeScores(List<Pair<String, Integer>> firstScores){
        //sorts the scores - compares score 2 to score 1 which will return the scores in descending order
        firstScores.sort((score1, score2) -> (score2.getValue().compareTo(score1.getValue())));
        try {
            if (new File("scores.txt").createNewFile()) {
                logger.info("File created");
            } else {
                logger.info("File already exists");
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.info("File creation error");
        }
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("scores.txt"));
            //keep track of how many scores are shown on screen
            int totalScores = 0;
            for (Pair<String, Integer> scores : firstScores){
                bufferedWriter.write(scores.getKey() + ":" + scores.getValue() + "\n");
                totalScores++;
                if(totalScores > 7){
                    break;
                }
            }
            bufferedWriter.close();
            logger.info("Scores saved");
        } catch (IOException e) {
            logger.info("scores cannot be saved...");
            e.printStackTrace();
        }

    }
    /**
     * Loads scores from a local file.
     *
     * @return a list of scores
     */
    public static ArrayList<Pair<String, Integer>>loadScores(){

        ArrayList<Pair<String, Integer>> scores = new ArrayList<>();

        File file = new File("scores.txt");

        BufferedReader bufferedReader = null;

        String line;

        if(!file.exists()) {

            ArrayList<Pair<String, Integer>> defaultScores = new ArrayList<>();

            defaultScores.add(new Pair<>("player 1", 100));
            defaultScores.add(new Pair<>("player 2", 30));
            defaultScores.add(new Pair<>("player 3", 30));
            defaultScores.add(new Pair<>("player 4", 70));
            defaultScores.add(new Pair<>("player 5", 40));
            defaultScores.add(new Pair<>("player 6", 50));
            defaultScores.add(new Pair<>("player 7", 130));
            defaultScores.add(new Pair<>("player 8", 150));
            defaultScores.add(new Pair<>("player 9", 170));

            writeScores(defaultScores);
        }
        try{
            bufferedReader = new BufferedReader(new FileReader(file));
            logger.info("buffered reader created");
        } catch (FileNotFoundException e){
            e.printStackTrace();
        }
        try {
            while ((line = bufferedReader.readLine()) != null){
                String[] newLine = line.split(":");
                scores.add(new Pair<>(newLine[0], Integer.parseInt(newLine[1])));
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        try {
            bufferedReader.close();
        }catch (IOException e){
            e.printStackTrace();
        }
        scores.sort((score1,score2) -> (score2.getValue().compareTo(score1.getValue())));
        return scores;
    }
    /**
     * Checks if the current score is a high score and updates the UI accordingly.
     */
    public void checkHighScores() {
        int currentScore = game.getScore();
        logger.debug("Checking if current score " + currentScore + " is a new high score.");
        // Determine if it's a new high score before calling Platform.runLater
        boolean isNewHighScore = isANewHighScore(currentScore);
        boolean onlineHighScore = isARemoteHighScore(currentScore);

        // Use Platform.runLater with a method reference or a lambda that doesn't modify the variable
        Platform.runLater(() -> updateUIBasedOnScore(isNewHighScore, currentScore));
        //Platform.runLater(()->updateOnlineScore(onlineHighScore,currentScore));

    }
    /**
     * Updates the UI based on whether a new high score was achieved.
     *
     * @param currentScore the current score
     */
    private boolean isANewHighScore(int currentScore) {
        for (Pair<String, Integer> score : localList) {
            if (currentScore > score.getValue()) {
                logger.info("new score is a new high score!");
                return true;
            }
        }
        return false;
    }

    private boolean isARemoteHighScore(int currentScore){
        for(Pair<String, Integer> remoteScore : remoteList){
            if(currentScore > remoteScore.getValue()){
                logger.info("new remote score is a High score!");
                return true;
            }
        }
        return false;
    }
    /**
     * Updates the UI based on whether a new high score was achieved.
     *
     * @param isNewHighScore whether the current score is a new high score
     * @param currentScore   the current score
     */
    private void updateUIBasedOnScore(boolean isNewHighScore, int currentScore) {
        if (isNewHighScore) {
            promptForNewHighScore(currentScore);
        } else {
            scoreList.reveal();
        }
    }

//    private void updateOnlineScore(boolean onlineHighScore, int currentScore) {
//        if (onlineHighScore) {
//            promptForNewHighScore(currentScore);
//        } else
//            scoreList.reveal();
//    }
    /**
     * Prompts for the user's name for a new high score entry.
     *
     * @param currentScore the score to be recorded
     */
    private void promptForNewHighScore(int currentScore) {
        vBox.getChildren().clear(); // Clear any previous elements
        logger.info("clearing previous elements");

        Text highScoreText = new Text("NEW HIGH SCORE");
        highScoreText.getStyleClass().add("heading");

        TextField userName = new TextField();
        userName.setPromptText("ENTER YOUR USERNAME:");
        userName.getStyleClass().add("heading");
        userName.setMaxWidth(400);

        Button submitButton = new Button("Submit");
        submitButton.getStyleClass().add("button");

        // Add new UI components
        vBox.getChildren().addAll(highScoreText, userName, submitButton);

        // Handle submission of new high score
        submitButton.setOnAction(event -> {
            String name = userName.getText().trim();
            if (!name.isEmpty()) {
                localList.add(new Pair<>(name, currentScore));
                writeScores(localList);
                vBox.getChildren().remove(highScoreText);
                vBox.getChildren().remove(userName);
                vBox.getChildren().remove(submitButton);
                buildScoreListUI();
            }
            //communicator.send(name + ":" + currentScore);
            remoteList.add(new Pair<String, Integer>(name,currentScore));

            //writeOnlineScore(name,currentScore);
            //loadOnlineScores();

            //communicator.send("HISCORES");
            scoreList.reveal();
//            onlineList.reveal();
        });
    }

    /**
     * method to end the game when button is pressed
     * @param event
     */
    private void endGame(ActionEvent event){
        App.getInstance().shutdown();
    }

    /**
     * method to go back to the menu when the button is pressed
     * @param event
     */
    private void openMenuScene(ActionEvent event){
        gameWindow.startMenu();
    }
//    /**
//     *
//     */
//    public void loadOnlineScores(){
//        communicator.send("HISCORES");
//    }
//    public void writeOnlineScore(String username, Integer highScore){
//        communicator.send(username + (":") + highScore );
//    }
}

