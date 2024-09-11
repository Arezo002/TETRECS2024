package uk.ac.soton.comp1206.event;

import uk.ac.soton.comp1206.component.GameBlockCoordinate;

import java.util.HashSet;

public interface LineClearedListener {
    /**
     * this listener handles the clearing of blocks
     * @param linesCleared  - coordinates to be cleared
     */
    public void lineCleared(HashSet<GameBlockCoordinate> linesCleared);
}
