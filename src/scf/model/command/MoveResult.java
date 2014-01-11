package scf.model.command;

import java.util.Arrays;
import java.util.Objects;





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


