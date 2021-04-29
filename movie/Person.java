package movie;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Object to describe persons
 * @author Alexandr Shchukin
 * @version 1.0
 */
@XmlType(name = "person")
@XmlRootElement
public class Person implements Serializable {
    
    /** name */
    @XmlElement
    private String name;

    /** passport identifier */
    @XmlElement
    private String passportId;

    /** color of eyes */
    @XmlElement
    private Color eyeColor;
    
    /** nationality */
    @XmlElement
    private Country nationality;

    /**
     * Constructor
     * @param name
     * @param passportId
     * @param eyeColor
     * @param nationality
     */
    public Person(String name, String passportId, Color eyeColor, Country nationality)
    {
        this.name = name;
        this.passportId = passportId;
        this.eyeColor = eyeColor;
        this.nationality = nationality;
    }

    /**
     * Constructor
     */
    public Person()
    {

    }

    /**
     * get name of person
     * @return name
     */
    public String getName()
    {
        return name;
    }

    /**
     * get passport identifier
     * @return passport identifier
     */
    public String getPassportId()
    {
        return passportId;
    }

    /**
     * get color of eyes
     * @return color
     */
    public Color getEyeColor()
    {
        return eyeColor;
    }

    /**
     * get nationality
     * @return nationality
     */
    public Country getNationality()
    {
        return nationality;
    }

    /**
     * Convertation to readable form. For example, [Fedor; passport Id: 1245; eye color: blue; nationality: Italy]
     * @return readable form
     */
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

    /**
     * Check if this object is equal to another object
     * @param another another object
     * @return result
     */
    @Override
    public boolean equals(Object another)
    {
        if (another.getClass() != this.getClass())
            return false;
        Person anotherPerson = (Person) another;
        return this.getPassportId().equals(anotherPerson.getPassportId());
    }

    /**
     * Get a copy of this object
     * @return copy
     */
    public Person clone()
    {
        Person res = new Person(this.name, this.passportId, this.eyeColor, this.nationality);
        return res;
    }
}
