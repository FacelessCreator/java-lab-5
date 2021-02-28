package movie;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessType;

/**
 * Container to store movies in xml file
 * @author Alexandr Shchukin
 * @version 1.0
 */
@XmlRootElement(name = "movies")
@XmlAccessorType (XmlAccessType.FIELD)
public class Movies {

    /** movies array */
    @XmlElement(name = "movie")
    private List<Movie> movies = null;

    /**
     * Get movies list
     * @return movies list
     */
    public List<Movie> getMovies() {
        return movies;
    }
 
    /**
     * Put movies in
     * @param movies
     */
    public void setMovies(List<Movie> movies) {
        this.movies = movies;
    }
}
