package movie;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessType;

@XmlRootElement(name = "movies")
@XmlAccessorType (XmlAccessType.FIELD)
public class Movies {

    @XmlElement(name = "movie")
    private List<Movie> movies = null;

    public List<Movie> getMovies() {
        return movies;
    }
 
    public void setMovies(List<Movie> movies) {
        this.movies = movies;
    }
}
