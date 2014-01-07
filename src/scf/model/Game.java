package scf.model;

import java.util.LinkedList;

/**
 *
 * @author paddya
 */
public class Game {
    
    private Board board;
    
    private Player challenger;
    private Player opponent;

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public Player getChallenger()
    {
        return challenger;
    }

    public void setChallenger(Player challenger)
    {
        this.challenger = challenger;
    }

    public Player getOpponent()
    {
        return opponent;
    }

    public void setOpponent(Player opponent)
    {
        this.opponent = opponent;
    }
    
    public Player getWinner()
    {
        if (getBoard() != null) {
            Player[][] board = getBoard().getBoard();
            
        }
        
        
        return null;
    }
    
    private LinkedList<Player> scanColumns(Player[][] board)
    { 
        LinkedList<Player> winners = new LinkedList<>();
        int sameInRow = 0;
        
        for (int i = 0; i < Board.NUM_COLUMNS; i++) {
            Player previousPlayer = null;
            for (int k = 0; k < Board.NUM_ROWS; k++) {
                Player currentPlayer = board[i][k];
                
                if (currentPlayer != null) {
                    if (currentPlayer == previousPlayer) {
                        sameInRow++;
                        
                        if (sameInRow == 4) {
                            winners.add(currentPlayer);
                        }
                        
                    } else {
                        sameInRow = 0;
                        previousPlayer = currentPlayer;
                    }
                } else {
                    previousPlayer = null;
                    sameInRow = 0;
                }
            }
        }
        
        return winners;
        
    }
    
    private LinkedList<Player>  scanRows(Player[][] board)
    {
        LinkedList<Player> winners = new LinkedList<>();
        int sameInRow = 0;
        
        for (int k = 0; k < Board.NUM_ROWS; k++) {
            Player previousPlayer = null;
            for (int i = 0; i < Board.NUM_COLUMNS; i++) {    
                Player currentPlayer = board[i][k];
                
                if (currentPlayer != null) {
                    if (currentPlayer == previousPlayer) {
                        sameInRow++;
                        
                        if (sameInRow == 4) {
                            winners.add(currentPlayer);
                        }
                        
                    } else {
                        sameInRow = 0;
                        previousPlayer = currentPlayer;
                    }
                } else {
                    previousPlayer = null;
                    sameInRow = 0;
                }
            }
        }
        
        return winners;
        
    }
}
