package uk.ac.soton.comp1206.scene;

import javafx.animation.FadeTransition;
import javafx.animation.RotateTransition;
import javafx.animation.SequentialTransition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import uk.ac.soton.comp1206.game.MultiMedia;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
/**
 * The IntroScene class configures an initial animation sequence for the application,
 * featuring a fade-in effect.
 */
public class IntroScene extends BaseScene {
    private ImageView logoView;
    private SequentialTransition animations;

    /**
     * Constructs the IntroScene associated with the given GameWindow.
     *
     * @param gameWindow The display window for this scene.
     */
    public IntroScene(GameWindow gameWindow) {
        super(gameWindow);
    }

    /**
     * Initializes the key press events to transition from the intro scene to the main menu.
     */
    @Override
    public void initialise() {
        MultiMedia.playAudio("intro.mp3");
        scene.setOnKeyPressed(event -> {
            gameWindow.startMenu();
            if (animations != null) {
                animations.stop();
            }
        });
    }

    /**
     * Assembles the components of the scene, including adding an image with a fade-in effect.
     */
    @Override
    public void build() {
        GamePane gamePane = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());
        root = gamePane;

        StackPane centerPane = new StackPane();
        centerPane.setMaxWidth(gameWindow.getWidth());
        centerPane.setMaxHeight(gameWindow.getHeight());
        centerPane.getStyleClass().add("intro-pane");
        gamePane.getChildren().add(centerPane);

        logoView = new ImageView(new Image(getClass().getResource("/images/ECSGames.png").toExternalForm()));
        logoView.setPreserveRatio(true);
        logoView.setFitWidth(400);
        centerPane.getChildren().add(logoView);

        // Set up the fade-in transition
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(2), logoView);
        fadeTransition.setFromValue(0.0);  // Start fully transparent
        fadeTransition.setToValue(1.0);    // Fade to fully opaque

        animations = new SequentialTransition(fadeTransition);
        animations.play();
        animations.setOnFinished(event -> gameWindow.startMenu());
    }
}
