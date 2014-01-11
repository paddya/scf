package scf.parser;

import scf.model.Board;
import scf.model.command.ClientHello;
import scf.model.command.Command;
import scf.model.command.CreateGame;
import scf.model.command.GameStart;
import scf.model.command.GamesList;
import scf.model.command.GetGames;
import scf.model.command.JoinGame;
import scf.model.command.LeaveGame;
import scf.model.command.MoveResult;
import scf.model.command.OpponentLeft;
import scf.model.command.Ping;
import scf.model.command.PlaceDisc;
import scf.model.command.Pong;
import scf.model.command.Reconnect;
import scf.model.command.Victory;
import scf.model.command.error.Err_Badcolumn;
import scf.model.command.error.Err_Badcommand;
import scf.model.command.error.Err_Badparams;
import scf.model.command.error.Err_Badsyntax;
import scf.model.command.error.Err_Gamefull;
import scf.model.command.error.Err_Nicknameinuse;
import scf.model.command.error.Err_Nicknamenotvalid;
import scf.model.command.error.Err_Nosuchgame;
import scf.model.command.error.Err_Playeralive;
import scf.model.command.error.Err_Unknownplayer;
import scf.model.command.response.Rpl_Discplaced;
import scf.model.command.response.Rpl_Gamecreated;
import scf.model.command.response.Rpl_Joinedgame;
import scf.model.command.response.Rpl_Leftgame;
import scf.model.command.response.Rpl_Reconnected;
import scf.model.command.response.Rpl_Serverhello;
import scf.parser.exception.ParserCommandNotFoundException;
import scf.parser.exception.ParserException;
import scf.parser.exception.ParserIllegalColumnException;
import scf.parser.exception.ParserIllegalPlayerNameException;
import scf.parser.exception.ParserInvalidParamsException;



/**
 *
 * @author Ferdinand Sauer
 */
public class Parser
{

    private static final String PARSE_ERR_PLAYERID = "PlayerID is either too long or to short.";
    private static final String PARSE_ERR_PARAM_1 = " requires exactly one parameter.";
    private static final String PARSE_ERR_PARAM_2 = " requires exactly two parameters.";
    private static final String PARSE_ERR_PARAM_3 = " requires exactly three parameters.";



    public static Command parse(String toParse) throws ParserException
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

        switch (message[0].toUpperCase()) {

            // Commands recievable by servers
            case ClientHello.NAME:
                if (message.length == 2) {
                    String playerID = message[1];

                    if (playerID.length() >= 2 && playerID.length() <= 30) {
                        cmd = new ClientHello(playerID);
                    } else {
                        throw new ParserIllegalPlayerNameException(PARSE_ERR_PLAYERID);
                    }
                } else {
                    throw new ParserInvalidParamsException("ClientHello " + PARSE_ERR_PARAM_1);
                }

                break;

            case GetGames.NAME:
                cmd = new GetGames();
                break;

            case JoinGame.NAME:
                if (message.length == 2) {
                    cmd = new JoinGame(message[1]);
                } else {
                    throw new ParserInvalidParamsException("JoinGame " + PARSE_ERR_PARAM_1);
                }

                break;

            case CreateGame.NAME:
                cmd = new CreateGame();
                break;

            case PlaceDisc.NAME:
                if (message.length == 2) {
                    Integer column = Integer.parseInt(message[1]);

                    if (column > 0 && column <= 6) {
                        cmd = new PlaceDisc(column);
                    } else {
                        throw new ParserIllegalColumnException("The column must be an integer value between 0 and 6.");
                    }
                } else {
                    throw new ParserInvalidParamsException("PlaceDisc " + PARSE_ERR_PARAM_1);
                }

                break;

            case LeaveGame.NAME:
                cmd = new LeaveGame();
                break;

            case Reconnect.NAME:
                if (message.length == 2) {
                    String playerID = message[1];

                    if (playerIdMatchesRequirements(playerID)) {
                        cmd = new Reconnect(playerID);
                    } else {
                        throw new ParserIllegalPlayerNameException(PARSE_ERR_PLAYERID);
                    }
                } else {
                    throw new ParserInvalidParamsException("Reconnect " + PARSE_ERR_PARAM_1);
                }

                break;

            case Pong.NAME:
                cmd = new Pong();
                break;

            // Commands recievable by clients
            case GamesList.NAME:
                cmd = new GamesList();
                break;

            case GameStart.NAME:
                cmd = new GameStart();
                break;

            case MoveResult.NAME:
                if (message.length != 3) {
                    throw new ParserInvalidParamsException("MoveResult " + PARSE_ERR_PARAM_2);
                }
                String board = message[1];
                String playerWithToken = message[2];
                if (!playerIdMatchesRequirements(playerWithToken)) {
                    throw new ParserIllegalPlayerNameException(PARSE_ERR_PLAYERID);
                }
                cmd = new MoveResult(parseBoardString(board), playerWithToken);
                break;

            case Victory.NAME:
                cmd = new Victory();
                break;

            case Ping.NAME:
                cmd = new Ping();
                break;

            case OpponentLeft.NAME:
                cmd = new OpponentLeft();
                break;

            // Replies
            case "RESPONSE":
                if (message.length <= 1) {
                    throw new IllegalArgumentException("The response number must be specidified"); // TODO No exception but error?
                }
                cmd = getReply(message[1]);
                break;

            // Errors
            case "ERROR":
                if (message.length <= 1) {
                    throw new IllegalArgumentException("The error number must be specidified"); // TODO No exception but error?
                }
                cmd = getError(message[1]);
                break;

            // No command could be parsed
            default:
                throw new ParserCommandNotFoundException("This command does not exist.");
        }

        return cmd;
    }



    private static Command getReply(String rplNum)
    {
        Command cmd;

        switch (rplNum) {
            case Rpl_Serverhello.CODE:
                cmd = new Rpl_Serverhello();
                break;

            case Rpl_Gamecreated.CODE:
                cmd = new Rpl_Gamecreated();
                break;

            case Rpl_Joinedgame.CODE:
                cmd = new Rpl_Joinedgame();
                break;

            case Rpl_Discplaced.CODE:
                cmd = new Rpl_Discplaced();
                break;

            case Rpl_Leftgame.CODE:
                cmd = new Rpl_Leftgame();
                break;

            case Rpl_Reconnected.CODE:
                cmd = new Rpl_Reconnected();
                break;

            default:
                cmd = null;
                break;
        }

        return cmd;
    }



    private static Command getError(String errNum)
    {
        Command cmd;

        switch (errNum) {
            case Err_Badcommand.CODE:
                cmd = new Err_Badcommand();
                break;

            case Err_Badsyntax.CODE:
                cmd = new Err_Badsyntax();
                break;

            case Err_Badparams.CODE:
                cmd = new Err_Badparams();
                break;

            case Err_Nicknameinuse.CODE:
                cmd = new Err_Nicknameinuse();
                break;

            case Err_Nicknamenotvalid.CODE:
                cmd = new Err_Nicknamenotvalid();
                break;

            case Err_Nosuchgame.CODE:
                cmd = new Err_Nosuchgame();
                break;

            case Err_Gamefull.CODE:
                cmd = new Err_Gamefull();
                break;

            case Err_Badcolumn.CODE:
                cmd = new Err_Badcolumn();
                break;

            case Err_Unknownplayer.CODE:
                cmd = new Err_Unknownplayer();
                break;

            case Err_Playeralive.CODE:
                cmd = new Err_Playeralive();
                break;

            default:
                cmd = null;
                break;
        }

        return cmd;
    }



    private static boolean playerIdMatchesRequirements(String playerID)
    {
        return playerID.length() >= 2 && playerID.length() <= 30;
    }



    private static String[][] parseBoardString(String board) throws ParserIllegalColumnException
    {
        // Separating rows
        String[] rows = board.trim().split(",\\],\\[");

        // Everything alright?
        if (rows.length != Board.NUM_ROWS) {
            throw new ParserIllegalColumnException("What is up with them rows?"); // TODO a new kind of exception
        }

        // Repair the mess of the first and last row
        rows[0] = rows[0].replaceFirst("\\[\\[", "");
        rows[Board.NUM_ROWS - 1] = rows[5].replaceFirst(",\\],\\]", ""); // Comma ftw -.-"

        // Lets get them columns!
        String[][] parsedBoard = new String[Board.NUM_ROWS][Board.NUM_COLUMNS];

        for (int i = 0; i < Board.NUM_ROWS; ++i) {
            String[] parsedRow = rows[i].split(",");

            if (parsedRow.length != Board.NUM_COLUMNS) {
                throw new ParserIllegalColumnException("What is up with them columns?"); // TODO a new kind of exception
            }
            
            parsedBoard[i] = parsedRow;
            
        }

        return parsedBoard;
    }
}


