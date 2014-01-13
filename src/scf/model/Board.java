package scf.model;

import java.util.Collection;
import java.util.Iterator;



/**
 *
 * @author paddya
 */
public class Board
{

    public static final int NUM_ROWS = 6;
    public static final int NUM_COLUMNS = 7;
    private Player[][] board = new Player[NUM_COLUMNS][NUM_ROWS];



    public Player[][] getBoard()
    {
        return board;
    }



    public void setBoard(Player[][] board)
    {

        if (board.length != NUM_COLUMNS || (board.length > 0 && board[0].length != NUM_ROWS)) {
            throw new IllegalArgumentException(
                    String.format("Board does not match requirements: %d (required: %d) rows, %d (%d), columns",
                    board[0].length,
                    NUM_ROWS,
                    board.length,
                    NUM_COLUMNS));
        }

        this.board = board;
    }



    public Integer getNextFreeRowInColumn(int column)
    {

        Player[] col = board[column];

        for (int row = 0; row < NUM_ROWS; row++) {
            if (col[row] == null) {
                return row;
            }
        }

        return null;

    }



    public boolean hasFreeRowInColumn(int column)
    {
        return getNextFreeRowInColumn(column) != null;
    }



    public boolean hasTwoFreeRowsInColumn(int column)
    {
        return hasFreeRowInColumn(column) && (getNextFreeRowInColumn(column) + 2) < NUM_ROWS;
    }
    
    
    
    public boolean isSaturated() {
        for (int i = 0; i < NUM_COLUMNS; i++) {
            if (hasFreeRowInColumn(i)) {
                return false;
            }
        }
        
        return true;
    }



    synchronized public boolean insertIntoColumn(Player player, int column)
    {
        if (hasFreeRowInColumn(column)) {
            board[column][getNextFreeRowInColumn(column)] = player;
            return true;
        } else {
            return false;
        }
    }
}


