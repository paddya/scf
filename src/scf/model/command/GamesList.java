package scf.model.command;


import java.util.ArrayList;



/**
 *
 * @author Ferdinand Sauer
 */
public class GamesList extends Command
{

    public static final String NAME = "GAMESLIST";
    
    private ArrayList<String> games;



    public GamesList()
    {
        protocolRepresentation = NAME + " "; // + args and/or something
        games = new ArrayList<>();
    }
    
    
    
    public void addGame(String gameID, String challengerID, String opponentID) 
    {
        if (opponentID == null || opponentID.trim().equals("")) {
            games.add("[" + gameID + "," + challengerID + "]");
        } else {
            games.add("[" + gameID + "," + challengerID + "," + opponentID + "]");
        }
    }



    @Override
    public String toString()
    {
        String out = "GAMESLIST [";
        
        for (String game : this.games) {
            out += game + ",";
        }
        
        out += "]";
        
        return out;
    }
}


