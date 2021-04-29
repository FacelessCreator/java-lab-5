package database;

import movie.Movie;
import movie.Person;
import java.util.List;

/**
 * Interface of all data bases
 * @author Alexandr Shchukin
 * @version 1.0
 */
public interface DataBase {

    /**
     * describe data base
     * @return description
     */
    DataBaseAnswer<String> about();

    /**
     * get status of data base
     * @return status
     */
    DataBaseAnswer<String> getStatus();

    /**
     * get count of elements inside
     * @return length
     */
    DataBaseAnswer<Integer> length();

    /**
     * add movie to data base
     * @param movie
     * @return object identifier or message code
     */
    DataBaseAnswer<Long> add(Movie movie);

    /**
     * remove object with id
     * @param id
     * @return message code
     */
    DataBaseAnswer<Void> remove(long id);

    /**
     * get object with id
     * @param id
     * @return movie or null
     */
    DataBaseAnswer<Movie> get(long id);

    /**
     * replace object with another object (doesn't change id)
     * @param id id of existing object
     * @param movie another object
     * @return message code
     */
    DataBaseAnswer<Void> replace(long id, Movie movie);

    /** 
     * remove all objects from data base
     */
    DataBaseAnswer<Void> clear();
    
    /**
     * get list of identifiers
     * @return id list
     */
    DataBaseAnswer<List<Long>> getIdList();

    /**
     * get max element of data base
     * @return movie or null
     */
    DataBaseAnswer<Movie> getMaxElement();

    /**
     * get min element of data base
     * @return movie or null
     */
    DataBaseAnswer<Movie> getMinElement();

    /**
     * get movies with this operator
     * @param operator
     * @return movies list
     */
    DataBaseAnswer<List<Movie>> searchByOperator(Person operator);

    DataBaseAnswer<Void> load();

    DataBaseAnswer<Void> save();
}
