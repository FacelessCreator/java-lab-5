package movie;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Color to describe objects
 * @author Alexandr Shchukin
 * @version 1.0
 */
@XmlType(name = "color")
@XmlRootElement
public enum Color {
    
    BLACK("black"), BLUE("blue"), ORANGE("orange"),
    WHITE("white"), BROWN("brown");

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