package movie;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import userapp.Localizer;

/**
 * Movie genre to describe movie
 * @author Alexandr Shchukin
 * @version 1.0
 */
@XmlType(name = "moviegenre")
@XmlRootElement
public enum MovieGenre {
    
    COMEDY("genre.comedy"), MUSICAL("genre.musical"), THRILLER("genre.thriller"), 
    HORROR("genre.horror");

    /** readable name */
    private final String NAME;

    /**
     * Constructor
     * @param name
     */
    MovieGenre(String name)
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
