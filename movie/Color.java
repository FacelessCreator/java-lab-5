package movie;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "color")
@XmlRootElement
public enum Color {
    
    BLACK("black"), BLUE("blue"), ORANGE("orange"),
    WHITE("white"), BROWN("brown");

    private final String NAME;

    Color(String name)
    {
        this.NAME = name;
    }

    @Override
    public String toString()
    {
        return NAME;
    }
}