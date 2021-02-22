package movie;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "person")
@XmlRootElement
public class Person {
    
    @XmlElement
    private String name;
    @XmlElement
    private String passportId;
    @XmlElement
    private Color eyeColor;
    @XmlElement
    private Country nationality;

    public Person(String name, String passportId, Color eyeColor, Country nationality)
    {
        this.name = name;
        this.passportId = passportId;
        this.eyeColor = eyeColor;
        this.nationality = nationality;
    }

    public Person()
    {

    }

    public String getName()
    {
        return name;
    }

    public String getPassportId()
    {
        return passportId;
    }

    public Color getEyeColor()
    {
        return eyeColor;
    }

    public Country getNationality()
    {
        return nationality;
    }

    @Override
    public String toString()
    {
        StringBuilder res = new StringBuilder("[");
        res.append(name)
        .append("; passport Id: ")
        .append(passportId)
        .append("; eye color: ")
        .append(eyeColor)
        .append("; nationality: ")
        .append(nationality)
        .append("]");
        return res.toString();
    }

    @Override
    public boolean equals(Object another)
    {
        if (another.getClass() != this.getClass())
            return false;
        Person anotherPerson = (Person) another;
        return this.getPassportId().equals(anotherPerson.getPassportId());
    }

    public Person clone()
    {
        Person res = new Person(this.name, this.passportId, this.eyeColor, this.nationality);
        return res;
    }
}
