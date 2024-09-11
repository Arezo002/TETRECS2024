package uk.ac.soton.comp1206.component;

import uk.ac.soton.comp1206.game.GamePiece;

/**
 * this class is used to create a board to display an upcoming piece
 */
public class PieceBoard extends GameBoard{
    /**
     * constructor from the super class creating a new grid to display an upcoming piece
     * @param cols
     * @param rows
     * @param width
     * @param height
     */
    public PieceBoard(int cols, int rows, double width, double height) {
        super(cols, rows, width, height);
    }

    /**
     * this method is used to display a piece on the piece boards
     * @param gamePiece
     */
    public void showPiece(GamePiece gamePiece){
        //2d array to store x and y coordinates of a game block
        int[][] blocks = gamePiece.getBlocks();
        // 2 loops to search for all the blocks in gameBlock.getBlock()
        for(int x = 0; x < blocks.length; x++){
            for(int y = 0; y < blocks[x].length; y++){
                //sets the blocks onto a grid (x,y and value)
                grid.set(x,y,blocks[x][y]);
            }
        }
    }





}


