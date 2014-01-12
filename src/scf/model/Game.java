package scf.model;

import java.util.HashSet;
import java.util.Set;
import static scf.model.Board.NUM_COLUMNS;
import static scf.model.Board.NUM_ROWS;
import scf.util.StringUtil;



/**
 *
 * @author paddya
 */
public class Game
{
    private String id;
    private Board board;
    private Player challenger;
    private Player opponent;



    public Game()
    {
        this.board = new Board();
    }



    public String getId()
    {
        return id;
    }



    public void setId(String id)
    {
        this.id = id;
    }
    
    public Board getBoard()
    {
        return board;
    }



    public void setBoard(Board board)
    {
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

            HashSet<Player> winners = new HashSet<>();

            winners.addAll(scanRows(board));
            winners.addAll(scanColumns(board));
            winners.addAll(scanDiagonals(board));

            if (winners.size() == 1) {
                return winners.contains(challenger) ? challenger : opponent;
            } else if (winners.size() == 2) {
                return opponent.hasToken() ? challenger : opponent;
            } else {
                return null;
            }

        }

        return null;
    }



    private Set<Player> scanColumns(Player[][] board)
    {
        HashSet<Player> winners = new HashSet<>();
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
                        sameInRow = 1;
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



    private Set<Player> scanRows(Player[][] board)
    {
        HashSet<Player> winners = new HashSet<>();
        int sameInRow = 0;

        for (int k = 0; k < Board.NUM_ROWS; k++) {
            Player previousPlayer = null;
            sameInRow = 0;
            for (int i = 0; i < Board.NUM_COLUMNS; i++) {
                Player currentPlayer = board[i][k];

                if (currentPlayer != null) {
                    if (currentPlayer == previousPlayer) {
                        sameInRow++;

                        if (sameInRow == 4) {
                            winners.add(currentPlayer);
                        }

                    } else {
                        sameInRow = 1;
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



    private Set<Player> scanDiagonals(Player[][] board)
    {
        HashSet<Player> winners = new HashSet<>();
        int sameInRow = 0;

        int m = Board.NUM_ROWS;
        int n = Board.NUM_COLUMNS;

        for (int slice = 0; slice < m + n - 1; ++slice) {
            Player previousPlayer = null;
            sameInRow = 0;
            System.out.println(String.format("Slice %d: ", slice));
            int z1 = slice < n ? 0 : slice - n + 1;
            int z2 = slice < m ? 0 : slice - m + 1;
            for (int j = slice - z2; j >= z1; --j) {
                Player currentPlayer = board[slice - j][j];

                if (currentPlayer != null) {
                    if (currentPlayer == previousPlayer) {
                        sameInRow++;
                        System.out.println(" (same as previous)");
                        if (sameInRow == 4) {
                            winners.add(currentPlayer);
                        }

                    } else {
                        sameInRow = 1;
                        previousPlayer = currentPlayer;
                    }

                } else {
                    sameInRow = 0;
                    previousPlayer = null;
                    //System.out.println("- ");
                }
                System.out.println(String.format("%d,%d ", slice - j, j));
            }
            System.out.println("\n");
        }

        for (int slice = m + n - 1; slice > 0; --slice) {
            Player previousPlayer = null;
            sameInRow = 0;
            System.out.println(String.format("Slice %d: ", slice));
            int z1 = slice < n ? 0 : slice - n + 1;
            int z2 = slice < m ? 0 : slice - m + 1;

            for (int j = z1; j <= slice - z2; ++j) {
                Player currentPlayer = board[slice - j][m - j - 1];

                if (currentPlayer != null) {
                    if (currentPlayer == previousPlayer) {
                        sameInRow++;
                        System.out.println(" (same as previous)");
                        if (sameInRow == 4) {
                            winners.add(currentPlayer);
                        }

                    } else {
                        sameInRow = 1;
                        previousPlayer = currentPlayer;
                    }
                    //System.out.println(String.format("%d,%d ", slice - j, j));
                } else {
                    sameInRow = 0;
                    previousPlayer = null;
                    //System.out.println("- ");
                }
                System.out.println(String.format("%d,%d ", slice - j, j));
            }
            System.out.println("\n");
        }

        return winners;
    }



    public Player[][] parseStringBoard(String[][] stringBoard)
    {
        // Everything correct?
        if (stringBoard.length != Board.NUM_COLUMNS) {
            throw new IllegalArgumentException("Board out of bounds (columns)");
        }
        for (int i = 0; i < Board.NUM_COLUMNS; ++i) {
            if (stringBoard[i].length != Board.NUM_ROWS) {
                throw new IllegalArgumentException("Board out of bounds (column " + i + ")");
            }
        }

        // Everything correct! Lets get this party started.
        Player[][] playerBoard = new Player[Board.NUM_COLUMNS][Board.NUM_ROWS];

        for (int column = 0; column < Board.NUM_COLUMNS; ++column) {
            for (int row = 0; row < Board.NUM_ROWS; ++row) {
                switch (stringBoard[column][row].toLowerCase()) {
                    case "x":
                        playerBoard[column][row] = challenger;
                        break;
                    case "o":
                        playerBoard[column][row] = opponent;
                        break;
                    case "_":
                        playerBoard[column][row] = null; // Just for readability
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown symbol on board: " + stringBoard[column][row]);
                }
            }
        }

        return playerBoard;
    }
    
    /**
     * Returns the board as an array of "x", "o" and "_".
     * 
     * @return 
     */
    public String[][] getStringBoard()
    {

        String[][] translatedBoard = new String[NUM_ROWS][NUM_COLUMNS];
        
        Player[][] boardArray = board.getBoard();
        
        for (int col = 0; col < Board.NUM_COLUMNS; col++) {
            for (int row = 0; row < Board.NUM_ROWS; row++) {
                translatedBoard[row][col] = translateToSymbol(boardArray[col][row]);
            }
        }
        
        return translatedBoard;
    }
    
    private String translateToSymbol(Player player)
    {
        if (player == null) {
            return "_";
        }
        
        return player == challenger ? "x" : "o";
    }



    
    
// TODO: Write a test instead of this
//    public static void main(String[] args)
//    {
//        Game game = new Game();
//        
//        game.setBoard(new Board());
//        
//        Player player1 = new Player();
//        Player player2 = new Player();
//        
//        player1.setName("Player 1");
//        player2.setName("Player 2");
//        
//        player2.setToken(true);
//        
//        game.setChallenger(player1);
//        game.setOpponent(player2);
//        
//        game.getBoard().insertIntoColumn(player2, 3);
//        game.getBoard().insertIntoColumn(player1, 3);
//        game.getBoard().insertIntoColumn(player2, 3);
//        game.getBoard().insertIntoColumn(player1, 3);
//        
//        game.getBoard().insertIntoColumn(player2, 2);
//        game.getBoard().insertIntoColumn(player1, 2);
//        game.getBoard().insertIntoColumn(player2, 1);
//        game.getBoard().insertIntoColumn(player1, 2);
//        
//        game.getBoard().insertIntoColumn(player2, 4);
//        game.getBoard().insertIntoColumn(player1, 1);
//        
//        game.getBoard().insertIntoColumn(player2, 5);
//        game.getBoard().insertIntoColumn(player1, 0);
//        
//        System.out.println(game.getWinner().getName());
//        
//    }



    public Player getPlayerWithToken()
    {
        return challenger.hasToken() ? challenger : opponent;
    }
    
    public Player getPlayerWithoutToken()
    {
        return challenger.hasToken() ? opponent : challenger;
    }
}


