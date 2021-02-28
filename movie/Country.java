package movie;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Country do describe objects
 * @author Alexandr Shchukin
 * @version 1.0
 */
@XmlType(name = "country")
@XmlRootElement
public enum Country {
    
    RUSSIA("Russia"), USA("United States"), ITALY("Italy"),
    THAILAND("Thailand"), NORTH_KOREA("North Korea");
    
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
