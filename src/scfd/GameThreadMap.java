package scfd;

import java.util.concurrent.ConcurrentHashMap;



/**
 * Game table singleton maps gameIDs to game thread objects
 *
 * @author markus
 */
public class GameThreadMap extends ConcurrentHashMap<String, GameThread>
{

    private static GameThreadMap instance = null;



    private GameThreadMap()
    {
        // Private constructor
    }



    public static GameThreadMap getInstance()
    {
        if (instance == null) {
            instance = new GameThreadMap();
        }
        return instance;
    }
}


