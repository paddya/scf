package scf.model.command;

import scf.model.Player;





/**
 *
 * @author Ferdinand Sauer
 */
public class Victory extends Command
{

    public static final String NAME = "VICTORY";
    private String winner;



    public Victory(String winner)
    {
        this.winner = winner;
        protocolRepresentation = NAME + " " + winner; // + args and/or something
    }
}


