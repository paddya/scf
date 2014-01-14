/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scf.model;



/**
 *
 * @author paddya
 */
public class GameListEntry
{
    
    private String gameID;
    private String challengerName;
    private String opponentName;



    public String getGameID()
    {
        return gameID;
    }



    public String getChallengerName()
    {
        return challengerName;
    }



    public String getOpponentName()
    {
        return opponentName;
    }



    public void setGameID(String gameID)
    {
        this.gameID = gameID;
    }



    public void setChallengerName(String challengerName)
    {
        this.challengerName = challengerName;
    }



    public void setOpponentName(String opponentName)
    {
        this.opponentName = opponentName;
    }



    public GameListEntry()
    {
    }
    
    
    public GameListEntry(String gameID, String challengerName)
    {
        this.gameID = gameID;
        this.challengerName = challengerName;
    }



    public GameListEntry(String gameID, String challengerName, String opponentName)
    {
        this.gameID = gameID;
        this.challengerName = challengerName;
        this.opponentName = opponentName;
    }



    @Override
    public String toString()
    {
        if (opponentName != null) {
            return "[" + gameID + "," + challengerName + "," + opponentName + "]";
        } else {
            return "[" + gameID + "," + challengerName + "]";
        }
    }
    
    
    
}


