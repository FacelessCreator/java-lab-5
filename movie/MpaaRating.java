package movie;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "mpaarating")
@XmlRootElement
public enum MpaaRating {
    
    G("G"), PG_13("PG 13"), R("R");

    private final String NAME;

    MpaaRating(String name)
    {
        this.NAME = name;
    }

    @Override
    public String toString()
    {
        return NAME;
    }
}
