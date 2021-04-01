package database;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.TreeSet;

import movie.Movie;
import movie.Person;

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

    /**
     * Constructor
     */
    public InternalDataBase()
    {
        movies = new TreeSet<Movie>();
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
        Iterator<Movie> it = movies.iterator();
        while (it.hasNext())
        {
            Movie m = it.next();
            if (m.getId() == id)
            {
                it.remove();
                return new DataBaseAnswer<Void>(DataBaseAnswer.CODE_SUCCESS, null);
            }
        }
        return new DataBaseAnswer<Void>(DataBaseAnswer.CODE_OBJECT_NOT_FOUND, null);
    }

    public DataBaseAnswer<Movie> get(long id)
    {
        Iterator<Movie> it = movies.iterator();
        while (it.hasNext())
        {
            Movie m = it.next();
            if (m.getId() == id)
            {
                return new DataBaseAnswer<Movie>(DataBaseAnswer.CODE_SUCCESS, m.clone());
            }
        }
        return new DataBaseAnswer<Movie>(DataBaseAnswer.CODE_OBJECT_NOT_FOUND, null);
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
        List<Long> res = new ArrayList<Long>();
        Iterator<Movie> it = movies.iterator();
        while (it.hasNext())
        {
            Movie m = it.next();
            res.add(m.getId());
        }
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
        List<Movie> res = new ArrayList<Movie>();
        Iterator<Movie> it = movies.iterator();
        while (it.hasNext())
        {
            Movie m = it.next();
            Person mOperator = m.getOperator();
            if (mOperator != null && mOperator.equals(operator))
            {
                res.add(m.clone());
            }
        }
        return new DataBaseAnswer<List<Movie>>(DataBaseAnswer.CODE_SUCCESS, res);
    }

}
