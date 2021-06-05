package movie;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import userapp.Localizer;

/**
 * Country do describe objects
 * @author Alexandr Shchukin
 * @version 1.0
 */
@XmlType(name = "country")
@XmlRootElement
public enum Country {
    
    RUSSIA("country.russia"), USA("country.usa"), ITALY("country.italy"),
    THAILAND("country.thailand"), NORTH_KOREA("country.north_korea");
    
    /** readable name */
    private final String NAME;

    /**
     * Constructor
     * @param name
     */
    Country(String name)
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
