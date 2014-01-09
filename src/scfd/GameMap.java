/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scfd;
import java.util.concurrent.*;

/**
 * Game table singleton
 * maps gameIDs to game thread objects
 * @author markus
 */
public class GameMap extends ConcurrentHashMap<String, GameThread>
{
    private static GameMap instance = null;
    
    
    
    private GameMap() 
    {
        // Can not access constructor from outside
    }
    
    
    
    public static GameMap getInstance() 
    {
        if (instance == null) {
        instance = new GameMap();
        }
        return instance;
    }
}
