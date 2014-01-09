/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scfd;

import java.util.concurrent.ConcurrentHashMap;





/**
 * Player table singleton
 * maps playerIDs to player thread objects
 * @author markus
 */
public class PlayerThreadMap extends ConcurrentHashMap<String, PlayerThread>
{
    private static PlayerThreadMap instance = null;
    
    
    
    private PlayerThreadMap() 
    {
        // Private constructor
    }
    
    
    
    public static PlayerThreadMap getInstance()
    {
        if (instance == null) {
        instance = new PlayerThreadMap();
        }
        return instance;
    }
}


