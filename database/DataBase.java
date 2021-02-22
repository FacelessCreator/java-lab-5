package database;

import movie.Movie;
import movie.Person;
import java.util.List;

public interface DataBase {
    String about();
    String getStatus();
    int length();
    long add(Movie movie);
    boolean remove(long id);
    Movie get(long id);
    boolean replace(long id, Movie movie);
    void clear();
    List<Long> getIdList();
    Movie getMaxElement();
    Movie getMinElement();
    List<Movie> searchByOperator(Person operator);
}
