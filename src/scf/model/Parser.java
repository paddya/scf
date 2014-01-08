package scf.model;

import scf.model.command.Command;
import scf.model.command.GameStart;
import scf.model.command.GamesList;
import scf.model.command.MoveResult;
import scf.model.command.OpponentLeft;
import scf.model.command.Ping;
import scf.model.command.Victory;
import scf.model.command.error.Err_Badsyntax;

/**
 *
 * @author Ferdinand Sauer
 */
public class Parser
{

    public static Command parse(String toParse)
    {
        if (toParse == null) {
            throw new IllegalArgumentException("The String to parse may not be null!");
        }

        // Some preparations
        String[] message = toParse.trim().split(" ");

        if (message.length <= 0) {
            throw new IllegalArgumentException("The String to parse may not be empty!");
        }

        Command cmd = null;

        switch (message[0].toLowerCase()) {

            // Commands recievable by servers
            case "clienthello":
            case "getgames":
            case "joingame":
            case "creategame":
            case "placedisc":
            case "leavegame":
            case "reconnect":
            case "pong":
                break;

            // Commands recievable by clients
            case "gameslist":
                cmd = new GamesList();
                break;

            case "gamestart":
                cmd = new GameStart();
                break;

            case "moveresult":
                cmd = new MoveResult();
                break;

            case "victory":
                cmd = new Victory();
                break;

            case "ping":
                cmd = new Ping();
                break;

            case "opponentleft":
                cmd = new OpponentLeft();
                break;

            default:
                cmd = new Err_Badsyntax();
                break;
        }

        return cmd;
    }
}
