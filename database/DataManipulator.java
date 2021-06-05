package database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import movie.Movie;

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
    public DataBaseAnswer<String> getInfo()
    {
        StringBuilder res = new StringBuilder();
        DataBaseAnswer<String> about = dataBase.about();
        DataBaseAnswer<String> status = dataBase.getStatus();
        DataBaseAnswer<Integer> length = dataBase.length();
        if (about.code != 0 || status.code != 0 || length.code != 0) {
            return new DataBaseAnswer<String>(about.code, null);
        }
        res.append(about.object)
        .append("\n")
        .append("Status: ")
        .append(status.object)
        .append("\nCurrent count of elements: ")
        .append(length.object);
        return new DataBaseAnswer<String>(0, res.toString());
    }

    /**
     * get object with id
     * @param id
     * @return movie or null
     */
    public DataBaseAnswer<Movie> get(long id)
    {
        return dataBase.get(id);
    }

    /**
     * get all objects
     * @return list of movies
     */
    public DataBaseAnswer<List<Movie>> getAll()
    {
        List<Movie> res = new ArrayList<Movie>();
        DataBaseAnswer<List<Long>> ids = dataBase.getIdList();
        if (ids.code != 0) {
            return new DataBaseAnswer<List<Movie>>(ids.code, null);
        }
        for (Long id : ids.object)
        {
            DataBaseAnswer<Movie> answer = dataBase.get(id);
            if (answer.code != 0) {
                return new DataBaseAnswer<List<Movie>>(answer.code, null);
            }
            res.add(answer.object);
        }
        return new DataBaseAnswer<List<Movie>>(0, res);
    }

    /**
     * add movie to data base
     * @param movie
     * @return object identifier or message code
     */
    public DataBaseAnswer<Long> add(Movie movie)
    {
        return dataBase.add(movie);
    }

    /**
     * Add objects to data base
     * @param movies list of movies
     * @return list of identifiers or message codes
     */
    public DataBaseAnswer<List<Long>> addAll(List<Movie> movies)
    {
        List<Long> res = new ArrayList<Long>();
        for (Movie m : movies)
        {
            DataBaseAnswer<Long> answer = dataBase.add(m);
            if (answer.code != 0) {
                return new DataBaseAnswer<List<Long>>(answer.code, res);
            }
            res.add(Long.valueOf(answer.object));
        }
        return new DataBaseAnswer<List<Long>>(0, res);
    }

    /**
     * replace object with another object (doesn't change id)
     * @param id id of existing object
     * @param movie another object
     * @return message code
     */
    public DataBaseAnswer<Void> replace(long id, Movie movie)
    {
        return dataBase.replace(id, movie);
    }

    /**
     * remove object with id
     * @param id
     * @return message code
     */
    public DataBaseAnswer<Void> remove(long id)
    {
        return dataBase.remove(id);
    }

    /** 
     * remove all objects from data base
     */
    public DataBaseAnswer<Void> clear()
    {
        return dataBase.clear();
    }

    /**
     * Add object if it's higher than max element in data base
     * @param movie
     * @return result
     */
    public DataBaseAnswer<Boolean> addIfMax(Movie movie)
    {
        DataBaseAnswer<Movie> maxElementAnswer = dataBase.getMaxElement();
        if (maxElementAnswer.code != 0) {
            return new DataBaseAnswer<Boolean>(maxElementAnswer.code, false);
        }
        if ((maxElementAnswer.object == null) || (movie.compareTo(maxElementAnswer.object) > 0))
        {
            DataBaseAnswer<Long> answer = dataBase.add(movie);
            return new DataBaseAnswer<Boolean>(answer.code, answer.code == 0);
        }
        return new DataBaseAnswer<Boolean>(0, false);
    }

    /**
     * Remove all objects lower than this object
     * @param movie
     */
    public DataBaseAnswer<Void> removeLower(Movie movie)
    {
        DataBaseAnswer<List<Movie>> elementsAnswer = getAll();
        if (elementsAnswer.code != 0) {
            return new DataBaseAnswer<Void>(elementsAnswer.code, null);
        }
        for (Movie el : elementsAnswer.object)
        {
            if (el.compareTo(movie) < 0)
            {
                DataBaseAnswer<Void> answer = dataBase.remove(el.getId());
                if (answer.code != 0) {
                    //return answer;
                }
            }
        }
        return new DataBaseAnswer<Void>(0, null);
    }

    /**
     * Remove all movies with this operator
     * @param operator
     */
    public DataBaseAnswer<Void> removeAllByOperator(Person operator)
    {
        DataBaseAnswer<List<Movie>> elementsAnswer = dataBase.searchByOperator(operator);
        if (elementsAnswer.code != 0) {
            return new DataBaseAnswer<Void>(elementsAnswer.code, null);
        }
        for (Movie el : elementsAnswer.object)
        {
            DataBaseAnswer<Void> answer = dataBase.remove(el.getId());
            if (answer.code != 0) {
                //return answer;
            }
        }
        return new DataBaseAnswer<Void>(0, null);
    }

    /**
     * Calculate summ of oscars for data base elements
     * @return summ
     */
    public DataBaseAnswer<Long> getSumOfOscarsCount()
    {
        DataBaseAnswer<List<Movie>> elementsAnswer = getAll();
        if (elementsAnswer.code != 0) {
            return new DataBaseAnswer<Long>(elementsAnswer.code, null);
        }
        long res = 0;
        for (Movie el : elementsAnswer.object)
        {
            res += el.getOscarsCount();
        }
        return new DataBaseAnswer<Long>(0, res);
    }

    /**
     * Count of movies with the same oscars count
     * @return hash map where keys are oscars count and values are movies count
     */
    public DataBaseAnswer<HashMap<Long, Long>> getGroupCountingByOscarsCount()
    {
        DataBaseAnswer<List<Movie>> elementsAnswer = getAll();
        if (elementsAnswer.code != 0) {
            return new DataBaseAnswer<HashMap<Long, Long>>(elementsAnswer.code, null);
        }
        HashMap<Long, Long> groups = new HashMap<Long, Long>();
        for (Movie el : elementsAnswer.object)
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
        return new DataBaseAnswer<HashMap<Long, Long>>(0, groups);
    }

    public DataBaseAnswer<Void> load()
    {
        return dataBase.load();
    } 

    public DataBaseAnswer<Void> save()
    {
        return dataBase.save();
    }

}
