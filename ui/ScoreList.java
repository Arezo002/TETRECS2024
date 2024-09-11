package uk.ac.soton.comp1206.ui;

import javafx.animation.*;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.ListChangeListener;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

/**
 * class to hold a list of names and associated scores
 * extends {@link VBox} to hold a vertical list of scores
 */

public class ScoreList extends VBox {
    /**
     * logger for logging messages
     */
    private static final Logger logger = LogManager.getLogger(ScoreList.class);
    /**
     * observable list - allows listeners to track to the lists content (e.g.new highscore) this allows the UI to change.
     */

    private final SimpleListProperty<Pair<String, Integer>> scoreList;
    /**
     * an array list of {@link VBox} vertical boxes which all hold a score -
     * I did it this way so it looks better in the UI and the animation can be applied more easily
     */
    private ArrayList<VBox> arrayBox = new ArrayList<>();

    /**
     * constructor - initialises the scoreList and calls the updateScores method
     */
    public ScoreList() {
        scoreList = new SimpleListProperty<>();
        getStyleClass().add("hiscore");
        updateScores();
        logger.info("Score List initialised");
    }

    /**
     * updateScores Method:
     * Sets up the score list change listener and updates the UI whenever the score list changes.
     * It limits the number of displayed users to a maximum of eight.
     *
     */
    public void updateScores(){
        //add listener to score list - triggered whenever there is a change to the list.
        //used lambda to define the response to these changes
        scoreList.addListener((ListChangeListener<Pair<String, Integer>>) change -> {
            reset();
            //tracking how many users and corresponding scores to be displayed in the score scene
            int currentNumUsers = 0;
            int maxNumUsers = 8;
            //loops through all the scores pairs in the scoreList and increases the current number of users
            for (Pair<String, Integer> stringIntegerPair : scoreList) {
                logger.info("Creating Scores List");
                currentNumUsers++;
                //check to see if the current number of scores looped, through do not exceed 8
                if (currentNumUsers >= maxNumUsers) break;
                // create a score box holding the score pairs.
                VBox scoreBox = createScoreBox(stringIntegerPair);
                //add the VBox to the array list for animation
                arrayBox.add(scoreBox);
                //add the vbox to the class, which extends the vbox for display
                getChildren().add(scoreBox);
                //reveal method - animation
                reveal();
            }
        });

        logger.info("Creating Scores List");
    }

    /**
     * Resets the UI by clearing the list of score boxes and removing all children from the VBox.
     */
    private void reset(){
        arrayBox.clear();
        getChildren().clear();
    }

    /**
     * method to create a {@link VBox} - takes one score entry at a time
     * @param score
     * @return VBox containing the displayed score
     */
    private VBox createScoreBox(Pair<String, Integer> score) {
        VBox scoreBox = new VBox();
        scoreBox.setAlignment(Pos.CENTER);
        Text scoreText = new Text(score.getKey() + " : " + score.getValue());
        scoreBox.getChildren().add(scoreText);
        return scoreBox;
    }
    /**
     * reveal method - adds animation for the score reveal.
     */
    public void reveal() {
        logger.info("Revealing scores....");
        //array list to hold the
        ArrayList<Transition> transitionArrayList = new ArrayList<>();


        for (var score : arrayBox) {

            FadeTransition fade = new FadeTransition(Duration.millis(300), score);
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.setCycleCount(2);


            transitionArrayList.add(fade);
        }
        //sequential transition is a type of animation that manages a sequence of other animations, playing them in order.
        //once one score fades onto the screen fully, the next score will start it's fading on screen
        //Animation[]::new - concise and efficient way to create a new array of type Animation
        SequentialTransition transition = new SequentialTransition(transitionArrayList.toArray(Animation[]::new));
        transition.play();
    }


    public SimpleListProperty<Pair<String, Integer>> scoreListProperty() {
        return scoreList;
    }

}
