package uk.ac.soton.comp1206.component;

import javafx.animation.AnimationTimer;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The Visual User Interface component representing a single block in the grid.
 * <p>
 * Extends Canvas and is responsible for drawing itself.
 * <p>
 * Displays an empty square (when the value is 0) or a coloured square depending on value.
 * <p>
 * The GameBlock value should be bound to a corresponding block in the Grid model.
 */
public class GameBlock extends Canvas {

    /**
     * The set of colours for different pieces
     */
    public static final Color[] COLOURS = {
            Color.TRANSPARENT,
            Color.DEEPPINK,
            Color.RED,
            Color.ORANGE,
            Color.YELLOW,
            Color.YELLOWGREEN,
            Color.LIME,
            Color.GREEN,
            Color.DARKGREEN,
            Color.DARKTURQUOISE,
            Color.DEEPSKYBLUE,
            Color.AQUA,
            Color.AQUAMARINE,
            Color.BLUE,
            Color.MEDIUMPURPLE,
            Color.PURPLE
    };
    private static final Logger logger = LogManager.getLogger(GameBlock.class);
    private final GameBoard gameBoard;
    private final double width;
    private final double height;
    /**
     * The column this block exists as in the grid
     */
    private final int x;
    /**
     * The row this block exists as in the grid
     */
    private final int y;
    /**
     * The value of this block (0 = empty, otherwise specifies the colour to render as)
     */
    private final IntegerProperty value = new SimpleIntegerProperty(0);
    private boolean center = false;
    private boolean hover = false;

    /**
     * Create a new single Game Block
     *
     * @param gameBoard the board this block belongs to
     * @param x         the column the block exists in
     * @param y         the row the block exists in
     * @param width     the width of the canvas to render
     * @param height    the height of the canvas to render
     */
    public GameBlock(GameBoard gameBoard, int x, int y, double width, double height) {
        this.gameBoard = gameBoard;
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;

        //A canvas needs a fixed width and height
        setWidth(width);
        setHeight(height);

        //Do an initial paint
        paint();

        //When the value property is updated, call the internal updateValue method
        value.addListener(this::updateValue);
    }

    /**
     * When the value of this block is updated,
     *
     * @param observable what was updated
     * @param oldValue   the old value
     * @param newValue   the new value
     */
    private void updateValue(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        paint();
    }

    /**
     * Handle painting of the block canvas
     */
    public void paint() {
        //If the block is empty, paint as empty
        if (value.get() == 0) {
            paintEmpty();
        } else {
            //If the block is not empty, paint with the colour represented by the value
            paintColor(COLOURS[value.get()]);
        }
        //painting the center of the piece
        if (this.center) {
            GraphicsContext centerCircle = getGraphicsContext2D();
            centerCircle.setFill(Color.rgb(255, 255, 255, 0.5));

            centerCircle.fillOval(width / 4, height / 4, width / 2, height / 2);

        }
        //hover effect when mouse enters or keyboard
        if (this.hover) {
            var HoveredTile = getGraphicsContext2D();
            HoveredTile.fillRect(0, 0, width, height);
            HoveredTile.setFill(Color.rgb(255, 255, 255, 0.6));
        }
    }

    /**
     * Paint this canvas empty
     */
    private void paintEmpty() {
        var gc = getGraphicsContext2D();

        //Clear
        gc.clearRect(0, 0, width, height);

        //Fill
        gc.setFill(Color.rgb(0, 0, 0, 0.5));
        gc.fillRect(0, 0, width, height);

        //Border
        gc.setStroke(Color.gray(0.5));
        gc.strokeRect(0, 0, width, height);

    }

    /**
     * Paint this canvas with the given colour
     *
     * @param colour the colour to paint
     */
    private void paintColor(Paint colour) {
        var gc = getGraphicsContext2D();

        //Clear
        gc.clearRect(0, 0, width, height);

        //Colour fill
        gc.setFill(colour);
        gc.fillRect(0, 0, width, height);

        // Creates 3D effect on piece
        // Setting semi-transparent color
        gc.setFill(Color.color(59 / 255.0, 59 / 255.0, 59 / 255.0, 0.2));
        // Setting another semi-transparent color
        gc.setFill(Color.color(161 / 255.0, 161 / 255.0, 161 / 255.0, 0.3));
        // Setting another semi-transparent color
        gc.setFill(Color.color(255 / 255.0, 255 / 255.0, 255 / 255.0, 0.3));

        // Drawing a polygon
        gc.fillPolygon(new double[]{0, 0, width}, new double[]{0, height, height}, 3);
        // Drawing a filled rectangle
        gc.fillRect(0, 0, 3, height);
        // Drawing another filled rectangle
        gc.fillRect(0, 0, width, 3);

        //Border
        gc.setStroke(Color.BLACK);
        gc.strokeRect(0, 0, width, height);
    }

    /**
     * Get the column of this block
     *
     * @return column number
     */
    public int getX() {
        return x;
    }

    /**
     * Get the row of this block
     *
     * @return row number
     */
    public int getY() {
        return y;
    }

    /**
     * Get the current value held by this block, representing it's colour
     *
     * @return value
     */
    public int getValue() {
        return this.value.get();
    }

    /**
     * Bind the value of this block to another property. Used to link the visual block to a corresponding block in the Grid.
     *
     * @param input property to bind the value to
     */
    public void bind(ObservableValue<? extends Number> input) {
        value.bind(input);
    }


    @Override
    public String toString() {
        return "GameBlock{" +
                "x=" + x +
                ", y=" + y +
                ", value=" + value.get() +
                '}';
    }

    /**
     * paint a circle in the middle of the piece
     */
    public void centerCircle() {
        this.center = true;
    }

    /**
     * if a block is being hovered over - calls the paint method which colours it in as it is hovered over
     * @param hovering
     */

    public void hovering(boolean hovering) {
        this.hover = hovering;
        paint();
    }

    /**
     * fade out when lines are cleared
     */
    public void fadeOut(){
        logger.info("Fading Out");
        //first paint empty
        paintEmpty();
        //line to be cleared - paint blue
        paintColor(Color.BLUE);

        AnimationTimer timer = new AnimationTimer() {
            //iniatial opacity of the blocks
            double opacity = 1;
            @Override
            public void handle(long l) {
                paintColor(Color.rgb(0,0,255,opacity));
                //opacity slowly decreases - fade out effect
                opacity-=0.05;
                if(opacity <= 0){
                    logger.info("opacity reached zero - blocks faded out");
                    stop();
                    //paint those blocks empty
                    paintEmpty();

                }
            }
        };
        timer.start();

    }
}
