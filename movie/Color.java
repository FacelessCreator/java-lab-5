package movie;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import userapp.Localizer;

/**
 * Color to describe objects
 * @author Alexandr Shchukin
 * @version 1.0
 */
@XmlType(name = "color")
@XmlRootElement
public enum Color {
    
    BLACK("color.black"), BLUE("color.blue"), ORANGE("color.orange"),
    WHITE("color.white"), BROWN("color.brown");

    /** readable name of color */
    private final String NAME;

    /**
     * Constructor
     * @param name
     */
    Color(String name)
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