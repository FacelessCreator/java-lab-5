package database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import movie.Movie;
import movie.Person;

/**
 * Interlayer between data base interface and another parts of program. Extends default methods of data base
 * @author Alexandr Shchukin
 * @version 1.0
 */
public class DataManipulator {
    
    /** data base to interract */
    private DataBase dataBase;

    /**
     * Constructor
     * @param dataBase
     */
    public DataManipulator(DataBase dataBase)
    {
        this.dataBase = dataBase;
    }

    /**
     * describe data base
     * @return description
     */
    public String getInfo()
    {
        StringBuilder res = new StringBuilder();
        res.append(dataBase.about())
        .append("\n")
        .append("Status: ")
        .append(dataBase.getStatus())
        .append("\nCurrent count of elements: ")
        .append(dataBase.length());
        return res.toString();
    }

    /**
     * get object with id
     * @param id
     * @return movie or null
     */
    public Movie get(long id)
    {
        return dataBase.get(id);
    }

    /**
     * get all objects
     * @return list of movies
     */
    public List<Movie> getAll()
    {
        List<Movie> res = new ArrayList<Movie>();
        List<Long> ids = dataBase.getIdList();
        for (Long id : ids)
        {
            res.add(dataBase.get(id));
        }
        return res;
    }

    /**
     * add movie to data base
     * @param movie
     * @return object identifier or message code
     */
    public long add(Movie movie)
    {
        return dataBase.add(movie);
    }

    /**
     * Add objects to data base
     * @param movies list of movies
     * @return list of identifiers or message codes
     */
    public List<Long> addAll(List<Movie> movies)
    {
        List<Long> res = new ArrayList<Long>();
        for (Movie m : movies)
        {
            long id = dataBase.add(m);
            res.add(Long.valueOf(id));
        }
        return res;
    }

    /**
     * replace object with another object (doesn't change id)
     * @param id id of existing object
     * @param movie another object
     * @return message code
     */
    public int replace(long id, Movie movie)
    {
        return dataBase.replace(id, movie);
    }

    /**
     * remove object with id
     * @param id
     * @return message code
     */
    public int remove(long id)
    {
        return dataBase.remove(id);
    }

    /** 
     * remove all objects from data base
     */
    public void clear()
    {
        dataBase.clear();
    }

    /**
     * Add object if it's higher than max element in data base
     * @param movie
     * @return result
     */
    public boolean addIfMax(Movie movie)
    {
        Movie maxElement = dataBase.getMaxElement();
        if ((maxElement == null) || (movie.compareTo(maxElement) > 0))
        {
            dataBase.add(movie);
            return true;
        }
        return false;
    }

    /**
     * Remove all objects lower than this object
     * @param movie
     */
    public void removeLower(Movie movie)
    {
        List<Movie> elements = getAll();
        for (Movie el : elements)
        {
            if (el.compareTo(movie) < 0)
            {
                dataBase.remove(el.getId());
            }
        }
    }

    /**
     * Remove all movies with this operator
     * @param operator
     */
    public void removeAllByOperator(Person operator)
    {
        List<Movie> elements = dataBase.searchByOperator(operator);
        for (Movie el : elements)
        {
            dataBase.remove(el.getId());
        }
    }

    /**
     * Calculate summ of oscars for data base elements
     * @return summ
     */
    public long getSumOfOscarsCount()
    {
        List<Movie> elements = getAll();
        long res = 0;
        for (Movie el : elements)
        {
            res += el.getOscarsCount();
        }
        return res;
    }

    /**
     * Count of movies with the same oscars count
     * @return hash map where keys are oscars count and values are movies count
     */
    public HashMap<Long, Long> getGroupCountingByOscarsCount()
    {
        List<Movie> elements = getAll();
        HashMap<Long, Long> groups = new HashMap<Long, Long>();
        for (Movie el : elements)
        {
            long elOscars = el.getOscarsCount();
            boolean positionFound = false;
            for (Long count : groups.keySet())
            {
                if (count.longValue() == elOscars)
                {
                    groups.put(count, Long.valueOf(groups.get(count) + 1));
                    positionFound = true;
                    break;
                }
            }
            if (!positionFound)
            {
                groups.put(Long.valueOf(elOscars), Long.valueOf(1));
            }
        }
        return groups;
    }

}
