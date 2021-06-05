package movie;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import userapp.Localizer;

/**
 * Mpaa rating to describe movies
 * @author Alexandr Shchukin
 * @version 1.0
 */
@XmlType(name = "mpaarating")
@XmlRootElement
public enum MpaaRating {
    
    G("mpaa_rating.g"), PG_13("mpaa_rating.pg_13"), R("mpaa_rating.r");

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
