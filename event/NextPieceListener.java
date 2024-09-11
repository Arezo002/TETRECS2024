package uk.ac.soton.comp1206.event;

import uk.ac.soton.comp1206.game.GamePiece;

public interface NextPieceListener {
    /**
     * The NextPieceListener interface allows for the communication between the game model and UI components.
     * @param currentPiece shows the current piece which needs to be placed
     * @param followingPiece shows the following piece after the current piece is placed
     */

    public void nextPiece(GamePiece currentPiece, GamePiece followingPiece);
}
