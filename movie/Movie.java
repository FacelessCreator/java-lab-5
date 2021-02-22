package movie;

import java.time.LocalDateTime;

import javax.xml.bind.annotation.*;

@XmlType(name = "movie")
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class Movie implements Comparable<Movie> {
    
    private long id = -1;
    @XmlElement
    private String name;
    @XmlElement
    private Coordinates coordinates;

    private LocalDateTime creationTime;
    @XmlElement
    private long oscarsCount;
    @XmlElement
    private long length;
    @XmlElement
    private MovieGenre genre;
    @XmlElement
    private MpaaRating mpaaRating;
    @XmlElement
    private Person operator;

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

    public Movie() {

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(LocalDateTime time) {
        creationTime = time;
    }

    public Person getOperator() {
        return operator;
    }

    public long getOscarsCount() {
        return oscarsCount;
    }

    public long getLength() {
        return length;
    }

    public String getName() {
        return name;
    }

    public MovieGenre getGenre() {
        return genre;
    }

    public MpaaRating getMpaaRating() {
        return mpaaRating;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

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

    public int compareTo(Movie another)
    {
        return this.getName().compareTo(another.getName());
    }

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
