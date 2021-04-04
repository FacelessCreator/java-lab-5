package database;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.xml.sax.SAXException;

import movie.*;

/**
 * Data base that stores in a local computer memory
 * @author Alexandr Shchukin
 * @version 1.0
 */
public class InternalDataBase implements DataBase {
    
    /** last used identifier */
    private static long lastId = 0;

    /** movies set */
    private TreeSet<Movie> movies;

    private String saveFilePath;

    /**
     * Constructor
     */
    public InternalDataBase(String saveFilePath)
    {
        movies = new TreeSet<Movie>();
        this.saveFilePath = saveFilePath;
    }

    public DataBaseAnswer<String> about()
    {
        return new DataBaseAnswer<String>(DataBaseAnswer.CODE_SUCCESS, "Internal data base. Uses " + movies.getClass().getName() + " as container");
    }

    public DataBaseAnswer<String> getStatus()
    {
        return new DataBaseAnswer<String>(DataBaseAnswer.CODE_SUCCESS, "Available");
    }

    public DataBaseAnswer<Integer> length()
    {
        return new DataBaseAnswer<Integer>(DataBaseAnswer.CODE_SUCCESS, movies.size());
    }

    public DataBaseAnswer<Long> add(Movie movie)
    {
        Movie clonedMovie = movie.clone();
        if (movies.contains(clonedMovie)) {
            return new DataBaseAnswer<Long>(DataBaseAnswer.CODE_OBJECT_ALREADY_EXISTS, null);
        } else {
            clonedMovie.setId(++lastId);
            clonedMovie.setCreationTime(LocalDateTime.now());
            movies.add(clonedMovie);
            return new DataBaseAnswer<Long>(DataBaseAnswer.CODE_SUCCESS, lastId);
        }
    }

    public DataBaseAnswer<Void> remove(long id)
    {
        Optional<Movie> movieOptional = movies.stream().filter(m -> m.getId() == id).findFirst();
        if (movieOptional.isPresent()) {
            movies.remove(movieOptional.get());
            return new DataBaseAnswer<Void>(DataBaseAnswer.CODE_SUCCESS, null);
        } else {
            return new DataBaseAnswer<Void>(DataBaseAnswer.CODE_OBJECT_NOT_FOUND, null);
        }
    }

    public DataBaseAnswer<Movie> get(long id)
    {
        Optional<Movie> movieOptional = movies.stream().filter(m -> m.getId() == id).findFirst();
        if (movieOptional.isPresent()) {
            return new DataBaseAnswer<Movie>(DataBaseAnswer.CODE_SUCCESS, movieOptional.get());
        } else {
            return new DataBaseAnswer<Movie>(DataBaseAnswer.CODE_OBJECT_NOT_FOUND, null);
        }
    }

    public DataBaseAnswer<Void> replace(long id, Movie movie)
    {
        DataBaseAnswer<Movie> oldMovieAnswer = get(id);
        
        if (oldMovieAnswer.code != DataBaseAnswer.CODE_SUCCESS) {
            return new DataBaseAnswer<Void>(DataBaseAnswer.CODE_OBJECT_NOT_FOUND, null);
        }

        Movie clonedMovie = movie.clone();
        clonedMovie.setId(id);
        clonedMovie.setCreationTime(oldMovieAnswer.object.getCreationTime());
        movies.remove(oldMovieAnswer.object);
        movies.add(clonedMovie);
        return new DataBaseAnswer<Void>(DataBaseAnswer.CODE_SUCCESS, null);
    }

    public DataBaseAnswer<Void> clear()
    {
        movies.clear();
        return new DataBaseAnswer<Void>(DataBaseAnswer.CODE_SUCCESS, null);
    }

    public DataBaseAnswer<List<Long>> getIdList()
    {
        List<Long> res = movies.stream().map(m -> m.getId()).collect(Collectors.toList());
        return new DataBaseAnswer<List<Long>>(DataBaseAnswer.CODE_SUCCESS, res);
    }

    public DataBaseAnswer<Movie> getMaxElement()
    {
        Movie res;
        try {
            res = movies.last().clone();
        } catch (NoSuchElementException e) {
            res = null;
        }
        return new DataBaseAnswer<Movie>(DataBaseAnswer.CODE_SUCCESS, res);
    }

    public DataBaseAnswer<Movie> getMinElement()
    {
        Movie res;
        try {
            res = movies.first().clone();
        } catch (NoSuchElementException e) {
            res = null;
        }
        return new DataBaseAnswer<Movie>(DataBaseAnswer.CODE_SUCCESS, res);
    }

    public DataBaseAnswer<List<Movie>> searchByOperator(Person operator)
    { 
        List<Movie> res = movies.stream().filter(m -> m.getOperator().equals(operator)).map(m -> m.clone()).collect(Collectors.toList());
        return new DataBaseAnswer<List<Movie>>(DataBaseAnswer.CODE_SUCCESS, res);
    }

    /**
     * Save movies to local XML file
     * @param out output stream
     * @param filePath path to the file
     * @param movies movies list 
     */
    private DataBaseAnswer<Void> saveXML(String filePath, List<Movie> movies) {
        Movies wrappedMovies = new Movies();
        wrappedMovies.setMovies(movies);
        try {
            JAXBContext context = JAXBContext.newInstance(Movies.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            File xmlFile = new File(filePath);
            marshaller.marshal(wrappedMovies, xmlFile);
        } catch (JAXBException e) {
            Throwable realException = e.getLinkedException();
            if (realException == null) {
                return new DataBaseAnswer<Void>(DataBaseAnswer.CODE_INTERNAL_ERROR, null);
            } else {
                if (realException instanceof FileNotFoundException) {
                    return new DataBaseAnswer<Void>(DataBaseAnswer.CODE_PERMISSION_DENIED, null);
                } else {
                    return new DataBaseAnswer<Void>(DataBaseAnswer.CODE_INTERNAL_ERROR, null);
                }
            }
        }
        return new DataBaseAnswer<Void>(DataBaseAnswer.CODE_SUCCESS, null);
    }

    /**
     * Check paramenters of coordinates object
     * @param coordinates
     * @return are parameters correct
     */
    private boolean checkCoordinates(Coordinates coordinates) {
        return coordinates.getX() > -746 && coordinates.getY() > -951;
    }

    /**
     * Check paramenters of operator
     * @param operator
     * @return are parameters correct
     */
    private boolean checkOperator(Person operator) {
        String name = operator.getName();
        String passportId = operator.getPassportId();
        Country nationality = operator.getNationality();
        return 
            name != null 
            && name.length() > 0 
            && (passportId.length() == 0 || passportId.length() >= 4)
            && nationality != null;
    }

    /**
     * Check paramenters of movie
     * @param movie
     * @return are parameters correct
     */
    private boolean checkMovie(Movie movie) {
        String name = movie.getName();
        long oscarsCount = movie.getOscarsCount();
        long length = movie.getLength();
        Coordinates coordinates = movie.getCoordinates();
        Person operator = movie.getOperator();
        return
            name != null
            && name.length() > 0
            && coordinates != null
            && checkCoordinates(coordinates)
            && oscarsCount > 0
            && length > 0
            && (operator == null || checkOperator(operator));
    }

    /**
     * Load movies from XML file
     * @param out output stream
     * @param filePath path to the file
     * @return list of movies
     */
    private DataBaseAnswer<List<Movie>> loadXML(String filePath) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Movies.class);
            Unmarshaller un = jaxbContext.createUnmarshaller();
            File xmlFile = new File(filePath);
            if (!xmlFile.exists()) {
                return new DataBaseAnswer<List<Movie>>(DataBaseAnswer.CODE_INTERNAL_ERROR, null);
            }
            if (!xmlFile.canRead()) {
                return new DataBaseAnswer<List<Movie>>(DataBaseAnswer.CODE_INTERNAL_ERROR, null);
            }
            Movies wrappedMovies = (Movies) un.unmarshal(xmlFile);
            List<Movie> movies = new ArrayList<Movie>();
            int countOfBrokenObjects = 0;
            List<Movie> uncheckedMovies = wrappedMovies.getMovies();
            if (uncheckedMovies == null) {
                return new DataBaseAnswer<List<Movie>>(DataBaseAnswer.CODE_SUCCESS, movies);
            }
            for (Movie movie : uncheckedMovies) {
                if (checkMovie(movie)) {
                    movies.add(movie);
                } else {
                    ++countOfBrokenObjects;
                }
            }
            if (countOfBrokenObjects > 0) {
                // nothing to worry about
            }
            return new DataBaseAnswer<List<Movie>>(DataBaseAnswer.CODE_SUCCESS, movies);
        } catch (JAXBException e) {
            Throwable realException = e.getLinkedException();
            if (realException == null) {
                return new DataBaseAnswer<List<Movie>>(DataBaseAnswer.CODE_INTERNAL_ERROR, null);
            } else {
                if (realException instanceof SAXException) {
                    return new DataBaseAnswer<List<Movie>>(DataBaseAnswer.CODE_INTERNAL_ERROR, null);
                } else {
                    return new DataBaseAnswer<List<Movie>>(DataBaseAnswer.CODE_INTERNAL_ERROR, null);
                }
            }
        }
    }

    public DataBaseAnswer<Void> load() {
        DataBaseAnswer<Void> clearAnswer = clear();
        if (clearAnswer.code != 0) {
            return clearAnswer;
        }
        DataBaseAnswer<List<Movie>> loadXMLAnswer = loadXML(saveFilePath);
        if (loadXMLAnswer.code != 0) {
            return new DataBaseAnswer<Void>(loadXMLAnswer.code, null);
        }
        loadXMLAnswer.object.forEach(m -> add(m));
        return new DataBaseAnswer<Void>(DataBaseAnswer.CODE_SUCCESS, null);
    }

    public DataBaseAnswer<Void> save() {
        List<Movie> moviesList = movies.stream().collect(Collectors.toList());
        return saveXML(saveFilePath, moviesList);
    }

}
