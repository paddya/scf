package scf.model;

/**
 *
 * @author paddya
 */
public class Player {
    private String name;
    
    private boolean token;

    public boolean hasToken()
    {
        return token;
    }

    public void setToken(boolean hasToken)
    {
        this.token = hasToken;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
