package scf.model.command;

/**
 *
 * @author Ferdinand Sauer
 */
public class PlaceDisc extends Command
{

    public static final String NAME = "PLACEDISC";
    
    private Integer column;
    
    public PlaceDisc(Integer column)
    {
        this.column = column;
        protocolRepresentation = String.format("%s %d", NAME, column);
    }



    public Integer getColumn()
    {
        return column;
    }



    public void setColumn(Integer column)
    {
        this.column = column;
    }
    
    
}
