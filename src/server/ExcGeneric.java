package server;




/**
 *
 * @author ivan
 */
public class ExcGeneric extends Exception {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L; 
	
	private boolean isFatal = false;
    public ExcGeneric()
    {
        this("ExcGeneric");
    }

    public ExcGeneric(String message)
    {
        this(message, null);
    }

    public ExcGeneric(String message, Throwable cause)
    {
        this(message, cause, false);
    }

    public ExcGeneric(String message, Throwable cause,
                                   boolean isFatal)
    {
        super(message, cause);
        setFatal(isFatal);
    }

    //------------------ is fatal
    public boolean isFatal()
    {
        return isFatal;
    }

    public void setFatal(boolean isFatal)
    {
        this.isFatal = isFatal;
    }
}
    

