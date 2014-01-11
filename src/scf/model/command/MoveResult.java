package scf.model.command;

import java.util.Arrays;
import java.util.Objects;
import scf.model.Board;
import scf.util.StringUtil;





/**
 *
 * @author Ferdinand Sauer
 */
public class MoveResult extends Command
{

    private String playerWithToken;
    private String[][] board;

    public static final String NAME = "MOVERESULT";



    public MoveResult(String[][] board, String playerWithToken)
    {
        this.board = board;
        this.playerWithToken = playerWithToken;

        protocolRepresentation = NAME + " "; // + args and/or something
    }

    public String getPlayerWithToken()
    {
        return playerWithToken;
    }



    public void setPlayerWithToken(String playerWithToken)
    {
        this.playerWithToken = playerWithToken;
    }



    public String[][] getBoard()
    {
        return board;
    }



    public void setBoard(String[][] board)
    {
        this.board = board;
    }
    
    public String getBoardString()
    {
        return StringUtil.getStringBoardString(board);
    }


    @Override
    public int hashCode()
    {
        int hash = 3;
        hash = 61 * hash + Objects.hashCode(this.playerWithToken);
        hash = 61 * hash + Arrays.deepHashCode(this.board);
        return hash;
    }



    @Override
    public boolean equals(Object obj)
    {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MoveResult other = (MoveResult) obj;
        if (!Objects.equals(this.playerWithToken, other.playerWithToken)) {
            return false;
        }
        if (!Arrays.deepEquals(this.board, other.board)) {
            return false;
        }
        return true;
    }
    
    

}


