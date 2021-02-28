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

    public String about()
    {
        return "Internal data base. Uses " + movies.getClass().getName() + " as container";
    }

    public String getStatus()
    {
        return "Available";
    }

    public int length()
    {
        return movies.size();
    }

    public long add(Movie movie)
    {
        Movie clonedMovie = movie.clone();
        if (movies.contains(clonedMovie)) {
            return MESSAGE_OBJECT_ALREADY_EXISTS;
        } else {
            clonedMovie.setId(++lastId);
            clonedMovie.setCreationTime(LocalDateTime.now());
            movies.add(clonedMovie);
            return lastId;
        }
    }

    public int remove(long id)
    {
        Iterator<Movie> it = movies.iterator();
        while (it.hasNext())
        {
            Movie m = it.next();
            if (m.getId() == id)
            {
                it.remove();
                return 0;
            }
        }
        return MESSAGE_OBJECT_NOT_FOUND;
    }

    public Movie get(long id)
    {
        Iterator<Movie> it = movies.iterator();
        while (it.hasNext())
        {
            Movie m = it.next();
            if (m.getId() == id)
            {
                return m.clone();
            }
        }
        return null;
    }

    public int replace(long id, Movie movie)
    {
        Movie oldMovie = get(id);
        if (oldMovie != null)
        {
            Movie clonedMovie = movie.clone();
            clonedMovie.setId(id);
            clonedMovie.setCreationTime(oldMovie.getCreationTime());
            movies.remove(oldMovie);
            movies.add(clonedMovie);
            return 0;
        }
        return MESSAGE_OBJECT_NOT_FOUND;
    }

    public void clear()
    {
        movies.clear();
    }

    public List<Long> getIdList()
    {
        List<Long> res = new ArrayList<Long>();
        Iterator<Movie> it = movies.iterator();
        while (it.hasNext())
        {
            Movie m = it.next();
            res.add(m.getId());
        }
        return res;
    }

    public Movie getMaxElement()
    {
        Movie res;
        try {
            res = movies.last().clone();
        } catch (NoSuchElementException e) {
            res = null;
        }
        return res;
    }

    public Movie getMinElement()
    {
        Movie res;
        try {
            res = movies.first().clone();
        } catch (NoSuchElementException e) {
            res = null;
        }
        return res;
    }

    public List<Movie> searchByOperator(Person operator)
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
        return res;
    }

}
