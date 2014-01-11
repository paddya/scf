/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scf.parser.exception;



/**
 *
 * @author paddya
 */
public class ParserIllegalPlayerNameException extends ParserException
{

    public ParserIllegalPlayerNameException()
    {
    }



    public ParserIllegalPlayerNameException(String message)
    {
        super(message);
    }



    public ParserIllegalPlayerNameException(String message, Throwable cause)
    {
        super(message, cause);
    }



    public ParserIllegalPlayerNameException(Throwable cause)
    {
        super(cause);
    }



    public ParserIllegalPlayerNameException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }


    
}


