package scf.model.command;


import java.util.ArrayList;
import scf.model.GameListEntry;



/**
 *
 * @author Ferdinand Sauer
 */
public class GamesList extends Command
{

    public static final String NAME = "GAMESLIST";
    
    private ArrayList<GameListEntry> games;



    public GamesList()
    {
        protocolRepresentation = NAME + " "; // + args and/or something
        games = new ArrayList<>();
    }
    
    
    
    public void addGame(String gameID, String challengerID, String opponentID) 
    {
        if (opponentID == null || opponentID.trim().equals("")) {
            games.add(new GameListEntry(gameID, challengerID));
        } else {
            games.add(new GameListEntry(gameID, challengerID, opponentID));
        }
    }
    
    public ArrayList<GameListEntry> getGames()
    {
        return games;
    }



    public void setGames(ArrayList<GameListEntry> games)
    {
        this.games = games;
    }
    
    
    @Override
    public String toString()
    {
        String out = "GAMESLIST [";
        
        for (GameListEntry game : this.games) {
            out += game.toString() + ",";
        }
        
        out += "]";
        
        return out;
    }
}


