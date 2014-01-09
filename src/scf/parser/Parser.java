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

		Command cmd;

		switch (message[0].toUpperCase()) {

			// Commands recievable by servers
			case ClientHello.NAME:
				cmd = new ClientHello();
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
			case "001":
				cmd = new Rpl_Serverhello();
				break;

			case "002":
				cmd = new Rpl_Gamecreated();
				break;

			case "003":
				cmd = new Rpl_Joinedgame();
				break;

			case "004":
				cmd = new Rpl_Discplaced();
				break;

			case "005":
				cmd = new Rpl_Leftgame();
				break;

			case "006":
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
			case "101":
				cmd = new Err_Badcommand();
				break;

			case "102":
				cmd = new Err_Badsyntax();
				break;

			case "103":
				cmd = new Err_Badparams();
				break;

			case "110":
				cmd = new Err_Nicknameinuse();
				break;

			case "111":
				cmd = new Err_Nicknamenotvalid();
				break;

			case "120":
				cmd = new Err_Nosuchgame();
				break;

			case "121":
				cmd = new Err_Gamefull();
				break;

			case "122":
				cmd = new Err_Badcolumn();
				break;

			case "130":
				cmd = new Err_Unknownplayer();
				break;

			case "131":
				cmd = new Err_Playeralive();
				break;

			default:
				cmd = null;
				break;
		}
		
		return cmd;
	}
}
