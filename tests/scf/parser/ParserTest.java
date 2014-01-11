/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scf.parser;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import scf.model.command.Command;
import scf.model.command.MoveResult;



/**
 *
 * @author paddya
 */
public class ParserTest
{
    
    public ParserTest()
    {
    }
    
    /**
     * Test of parse method, of class Parser.
     */
    @Test
    public void testMoveResult() throws Exception
    {
        System.out.println("parse MoveResult");
        String toParse = "MOVERESULT [[x,o,x,_,_,o,_,],[x,o,o,_,_,x,_,],[o,_,_,_,_,x,_,],[_,_,_,_,_,_,_,],[_,_,_,_,_,_,_,],[_,_,_,_,_,_,_,],] KITstudent";
        
        String[][] board = {
            {"x", "x", "o", "_", "_", "_"},
            {"o", "o", "_", "_", "_", "_"},
            {"x", "o", "_", "_", "_", "_"},
            {"_", "_", "_", "_", "_", "_"},
            {"_", "_", "_", "_", "_", "_"},
            {"o", "x", "x", "_", "_", "_"},
            {"_", "_", "_", "_", "_", "_"},
        };
        
        Command expResult = new MoveResult(board, "KITstudent");
        Command result = Parser.parse(toParse);
        assertEquals(expResult, result);
    }
}