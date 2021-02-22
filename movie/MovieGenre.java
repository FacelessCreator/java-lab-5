package movie;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "moviegenre")
@XmlRootElement
public enum MovieGenre {
    
    COMEDY("comedy"), MUSICAL("musical"), THRILLER("thriller"), 
    HORROR("horror");

    private final String NAME;

    MovieGenre(String name)
    {
        this.NAME = name;
    }

    @Override
    public String toString()
    {
        return NAME;
    }
}
