package scf.model.command;

import scf.model.Player;

/**
 *
 * @author Ferdinand Sauer
 */
public abstract class Command
{
    protected String protocolRepresentation = "";
    
	@Override
    public String toString() {
        return protocolRepresentation;
    }
}
