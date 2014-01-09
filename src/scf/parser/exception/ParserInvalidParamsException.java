/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scf.parser.exception;



/**
 *
 * @author paddya
 */
public class ParserInvalidParamsException extends ParserException
{



    public ParserInvalidParamsException()
    {
    }



    public ParserInvalidParamsException(String message)
    {
        super(message);
    }



    public ParserInvalidParamsException(String message, Throwable cause)
    {
        super(message, cause);
    }



    public ParserInvalidParamsException(Throwable cause)
    {
        super(cause);
    }



    public ParserInvalidParamsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
    
}


