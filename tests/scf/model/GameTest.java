/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scf.model;

import org.junit.Test;
import static org.junit.Assert.*;
import scf.model.command.MoveResult;
import scf.parser.Parser;
import scf.parser.exception.ParserException;
import scf.util.StringUtil;



/**
 *
 * @author paddya
 */
public class GameTest
{
    /**
     * Test of getStringBoard method, of class Game.
     */
    @Test
    public void testGetBoardString() throws ParserException
    {
        System.out.println("getBoardString");
        Game instance = new Game();
        String expResult = "";
        String[][] result = instance.getStringBoard();
        
        String toParse = "MOVERESULT " + StringUtil.getStringBoardString(result) + " KITstudent";
        
        String[][] afterParsing = ((MoveResult)Parser.parse(toParse)).getBoard();
        
        assertEquals(result, afterParsing);
        System.out.println(result);
        //assertEquals(expResult, result);
    }
}