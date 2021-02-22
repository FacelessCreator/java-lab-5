package movie;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "country")
@XmlRootElement
public enum Country {
    
    RUSSIA("Russia"), USA("United States"), ITALY("Italy"),
    THAILAND("Thailand"), NORTH_KOREA("North Korea");
    
    private final String NAME;

    Country(String name)
    {
        this.NAME = name;
    }

    @Override
    public String toString()
    {
        return NAME;
    }
}
