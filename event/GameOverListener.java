package uk.ac.soton.comp1206.event;

//import uk.ac.soton.comp1206.scene.ScoresScene;

/**
 * interface to switch scenes when the timer runs out
 */
public interface GameOverListener {
    /**
     * method called when the score scene is called
     */
    void gameOver();
}
