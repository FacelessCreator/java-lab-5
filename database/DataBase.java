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

    /** message code - object already exists */
    int MESSAGE_OBJECT_ALREADY_EXISTS = -1;
    /** message code - object is not found */
    int MESSAGE_OBJECT_NOT_FOUND = -2;

    /**
     * describe data base
     * @return description
     */
    String about();

    /**
     * get status of data base
     * @return status
     */
    String getStatus();

    /**
     * get count of elements inside
     * @return length
     */
    int length();

    /**
     * add movie to data base
     * @param movie
     * @return object identifier or message code
     */
    long add(Movie movie);

    /**
     * remove object with id
     * @param id
     * @return message code
     */
    int remove(long id);

    /**
     * get object with id
     * @param id
     * @return movie or null
     */
    Movie get(long id);

    /**
     * replace object with another object (doesn't change id)
     * @param id id of existing object
     * @param movie another object
     * @return message code
     */
    int replace(long id, Movie movie);

    /** 
     * remove all objects from data base
     */
    void clear();
    
    /**
     * get list of identifiers
     * @return id list
     */
    List<Long> getIdList();

    /**
     * get max element of data base
     * @return movie or null
     */
    Movie getMaxElement();

    /**
     * get min element of data base
     * @return movie or null
     */
    Movie getMinElement();

    /**
     * get movies with this operator
     * @param operator
     * @return movies list
     */
    List<Movie> searchByOperator(Person operator);
}
