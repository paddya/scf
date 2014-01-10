package scf.model.command;



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

}


