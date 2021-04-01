package movie;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.xml.bind.annotation.*;

/**
 * Movie - main object in data base
 * @author Alexandr Shchukin
 * @version 1.0
 */
@XmlType(name = "movie")
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class Movie implements Comparable<Movie>, Serializable {
    
    /** unique identifier */
    private long id = -1;

    /** readable name */
    @XmlElement
    private String name;

    /** coordinates of movie */
    @XmlElement
    private Coordinates coordinates;

    /** time of adding to data base */
    private LocalDateTime creationTime;

    /** count of oscars that film got */
    @XmlElement
    private long oscarsCount;
    
    /** length of film */
    @XmlElement
    private long length;

    /** genre of film */
    @XmlElement
    private MovieGenre genre;

    /** rating of film */
    @XmlElement
    private MpaaRating mpaaRating;

    /** operator description */
    @XmlElement
    private Person operator;

    /**
     * Constructor
     * @param name
     * @param coordinates
     * @param oscarsCount
     * @param length
     * @param genre
     * @param mpaaRating
     * @param operator
     */
    public Movie(String name, Coordinates coordinates, long oscarsCount, long length, MovieGenre genre, MpaaRating mpaaRating, Person operator)
    {
        this.name = name;
        this.coordinates = coordinates;
        this.oscarsCount = oscarsCount;
        this.length = length;
        this.genre = genre;
        this.mpaaRating = mpaaRating;
        this.operator = operator;
    }

    /**
     * Constructor
     */
    public Movie() {

    }

    /**
     * get identifier
     * @return identifier
     */
    public long getId() {
        return id;
    }

    /**
     * set identifier
     * @param id identifier
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * get time of adding to data base
     * @return time
     */
    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    /**
     * set time of adding to data base
     * @param time
     */
    public void setCreationTime(LocalDateTime time) {
        creationTime = time;
    }

    /**
     * get operator description
     * @return operator description
     */
    public Person getOperator() {
        return operator;
    }

    /**
     * get count of oscars
     * @return count
     */
    public long getOscarsCount() {
        return oscarsCount;
    }

    /**
     * get film length
     * @return length
     */
    public long getLength() {
        return length;
    }

    /**
     * get readable name
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * get film genre
     * @return genre
     */
    public MovieGenre getGenre() {
        return genre;
    }

    /** 
     * get film rating
     * @return rating
     */
    public MpaaRating getMpaaRating() {
        return mpaaRating;
    }

    /**
     * get film coordinates
     * @return coordinates
     */
    public Coordinates getCoordinates() {
        return coordinates;
    }

    /** 
     * get readable description. For example, [1: Film; coordinates [5,5]; creationDate 2021-02-28T12:53:26.510739002; oscars count 1; length 2; genre musical; rating G; operator [Fedor; passport Id: 1245; eye color: blue; nationality: Italy]]
     * @return readable description. 
     */
    @Override
    public String toString()
    {
        StringBuilder res = new StringBuilder("[");
        res.append(id)
        .append(": ")
        .append(name)
        .append("; coordinates ")
        .append(coordinates)
        .append("; creationDate ")
        .append(creationTime)
        .append("; oscars count ")
        .append(oscarsCount)
        .append("; length ")
        .append(length)
        .append("; genre ")
        .append(genre)
        .append("; rating ")
        .append(mpaaRating)
        .append("; operator ")
        .append(operator)
        .append("]");
        return res.toString();
    }

    /** 
     * Compare this movie to another movie
     * @param another another movie
     * @return result of comparation
     */
    public int compareTo(Movie another)
    {
        return this.getName().compareTo(another.getName());
    }

    /**
     * Get a copy of this object
     * @return copy
     */
    public Movie clone()
    {
        Coordinates clonedCoordinates = coordinates.clone();
        Person clonedOperator = null;
        if (operator != null) {
            clonedOperator = operator.clone();
        }
        Movie clonedMovie = new Movie(this.name, clonedCoordinates, this.oscarsCount, this.length, this.genre, this.mpaaRating, clonedOperator);
        clonedMovie.setCreationTime(this.creationTime);
        clonedMovie.setId(this.id);
        return clonedMovie;
    }
}
