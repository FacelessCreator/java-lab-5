package movie;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Mpaa rating to describe movies
 * @author Alexandr Shchukin
 * @version 1.0
 */
@XmlType(name = "mpaarating")
@XmlRootElement
public enum MpaaRating {
    
    G("G"), PG_13("PG 13"), R("R");

    /** readable name */
    private final String NAME;

    /**
     * Constructor
     * @param name
     */
    MpaaRating(String name)
    {
        this.NAME = name;
    }

    /**
     * Convertation to string
     * @return readable name
     */
    @Override
    public String toString()
    {
        return NAME;
    }
}
