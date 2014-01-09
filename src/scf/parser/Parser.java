package scf.parser;

import scf.model.Player;
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

		switch (message[0].toUpperCase()) {

			// Commands recievable by servers
			case ClientHello.NAME:
                if (message.length == 2) {
                    String playerID = message[1];
                    
                    if (playerID.length() >= 2 && playerID.length() <= 30) {
                        cmd = new ClientHello(playerID);
                    } else {
                        cmd = null;
                    }
                    
                    
                }
				
				break;

			case GetGames.NAME:
				cmd = new GetGames();
				break;

			case JoinGame.NAME:
				cmd = new JoinGame(message[1]);
				break;

			case CreateGame.NAME:
				cmd = new CreateGame();
				break;

			case PlaceDisc.NAME:
				cmd = new PlaceDisc();
				break;

			case LeaveGame.NAME:
				cmd = new LeaveGame();
				break;

			case Reconnect.NAME:
				cmd = new Reconnect();
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
				cmd = new MoveResult();
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
				cmd = null; // TODO Best solution?
				break;
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

			case "102":
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
}
