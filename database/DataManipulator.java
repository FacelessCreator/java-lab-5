package database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import movie.Movie;
import movie.Person;

public class DataManipulator {
    
    private DataBase dataBase;

    public DataManipulator(DataBase dataBase)
    {
        this.dataBase = dataBase;
    }

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

    public Movie get(long id)
    {
        return dataBase.get(id);
    }

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

    public long add(Movie movie)
    {
        return dataBase.add(movie);
    }

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

    public boolean replace(long id, Movie movie)
    {
        return dataBase.replace(id, movie);
    }

    public boolean remove(long id)
    {
        return dataBase.remove(id);
    }

    public void clear()
    {
        dataBase.clear();
    }

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

    public void removeAllByOperator(Person operator)
    {
        List<Movie> elements = dataBase.searchByOperator(operator);
        for (Movie el : elements)
        {
            dataBase.remove(el.getId());
        }
    }

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
