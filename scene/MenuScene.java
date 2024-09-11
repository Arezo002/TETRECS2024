package uk.ac.soton.comp1206.scene;

import javafx.animation.RotateTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.App;
import uk.ac.soton.comp1206.game.MultiMedia;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * The main menu of the game. Provides a gateway to the rest of the game.
 */
public class MenuScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(MenuScene.class);

    /**
     * Create a new menu scene
     * @param gameWindow the Game Window this will be displayed in
     */
    public MenuScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating Menu Scene");
    }

    /**
     * Build the menu layout
     */
    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());

        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());

        var menuPane = new StackPane();
        menuPane.setMaxWidth(gameWindow.getWidth());
        menuPane.setMaxHeight(gameWindow.getHeight());
        menuPane.getStyleClass().add("menu-background");
        root.getChildren().add(menuPane);

        var mainPane = new BorderPane();
        menuPane.getChildren().add(mainPane);

        //Awful title
        ImageView image = new ImageView(getImage("TetrECS.png"));
        image.setFitHeight(100);
        image.setPreserveRatio(true);
        mainPane.setCenter(image);
        //rotate transtion for the tetrecs image
        RotateTransition rotate = new RotateTransition(Duration.millis(2000), image);
        rotate.setByAngle(5);
        rotate.setCycleCount(RotateTransition.INDEFINITE);
        rotate.setAutoReverse(true);
        rotate.play();

        //vbox to store the buttons
        var menu = new VBox();
        menu.setPadding(new Insets(20));
        menu.setSpacing(10);
        menu.setAlignment(Pos.CENTER);
        mainPane.setBottom(menu);
        //For now, let us just add a button that starts the game. I'm sure you'll do something way better.
        var play = new Button("Play");
        play.getStyleClass().add("menuItem");
        var multiPlayer = new Button("Multiplayer");
        multiPlayer.getStyleClass().add("menuItem");
        var instructions = new Button("How To Play");
        instructions.getStyleClass().add("menuItem");
        var exit = new Button("Exit");
        exit.getStyleClass().add("menuItem");

        //adding to UI
        menu.getChildren().add(play);
        menu.getChildren().add(multiPlayer);
        menu.getChildren().add(instructions);
        menu.getChildren().add(exit);


        //Bind the button action to the startGame method in the menu
        play.setOnAction(this::startGame);
        //multiPlayer.setOnAction();
        instructions.setOnAction(this::openInstScene);
        exit.setOnAction(this::endGame);


    }

    /**
     * Initialise the menu
     */
    @Override
    public void initialise() {
        MultiMedia.playBackgroundMusic("menu.mp3");
    }

    /**
     * Handle when the Start Game button is pressed
     * @param event event - user action
     */
    private void startGame(ActionEvent event) {
        gameWindow.startChallenge();
    }
    /**
     * Handle when the Exit Game button is pressed
     * @param event event - user action
     */
    private void endGame(ActionEvent event){
        App.getInstance().shutdown();
    }
    /**
     * Handle when the play Multiplayer Game button is pressed
     * @param event event - user action
     */
    private void startMult(ActionEvent event){
    }
    /**
     * Handle when the Open Instructions button is pressed
     * @param event event - user action
     */
    private void openInstScene(ActionEvent event){
        gameWindow.instScene();
    }

    /**
     * Retrieves an {@link Image} object from a specified image file within the application resources.
     * @param image
     * @return
     */
    private static Image getImage(String image) {
        return new Image(MenuScene.class.getResource("/images/" + image).toExternalForm());
    }
}
