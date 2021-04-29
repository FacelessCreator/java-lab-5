package database;

import java.util.List;
import java.util.TreeSet;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.NoSuchElementException;

import movie.Movie;
import movie.Person;

public class CachedDataBase implements AuthorizedDataBase {

    private AuthorizedDataBase linkedDataBase;
    
    private String invokingUserName = AuthorizedDataBase.DEFAULT_USERNAME;
    
    private TreeSet<Movie> movies = new TreeSet<Movie>();

    private void syncMovies() {
        DataBaseAnswer<List<Long>> linkedAnswer = linkedDataBase.getIdList();
        if (linkedAnswer.code == DataBaseAnswer.CODE_SUCCESS) {
            movies.clear();
            for (Long id : linkedAnswer.object) {
                DataBaseAnswer<Movie> linkedAnswer2 = linkedDataBase.get(id);
                if (linkedAnswer2.code == DataBaseAnswer.CODE_SUCCESS) {
                    movies.add(linkedAnswer2.object);
                }
            }
        }
    }

    public CachedDataBase(AuthorizedDataBase linkedDataBase) {
        this.linkedDataBase = linkedDataBase;
        syncMovies();
    }

    @Override
    public DataBaseAnswer<Void> changeUser(String userName) {
        invokingUserName = userName;
        return linkedDataBase.changeUser(userName);
    }

    @Override
    public DataBaseAnswer<String> about() {
        return linkedDataBase.about();
    }

    @Override
    public DataBaseAnswer<String> getStatus() {
        return linkedDataBase.getStatus();
    }

    @Override
    public DataBaseAnswer<Integer> length() {
        return linkedDataBase.length();
    }

    @Override
    public DataBaseAnswer<Long> add(Movie movie) {
        DataBaseAnswer<Long> linkedAnswer = linkedDataBase.add(movie);
        if (linkedAnswer.code == DataBaseAnswer.CODE_SUCCESS) {
            DataBaseAnswer<Movie> clonedMovieAnswer = linkedDataBase.get(linkedAnswer.object);
            movies.add(clonedMovieAnswer.object);
        }
        return linkedAnswer;
    }

    @Override
    public DataBaseAnswer<Void> remove(long id) {
        DataBaseAnswer<Void> linkedAnswer = linkedDataBase.remove(id);
        if (linkedAnswer.code == DataBaseAnswer.CODE_SUCCESS) {
            Optional<Movie> movieOptional = movies.stream().filter(m -> m.getId() == id).findFirst();
            movies.remove(movieOptional.get());
        }
        return linkedAnswer;
    }

    @Override
    public DataBaseAnswer<Movie> get(long id) {
        Optional<Movie> movieOptional = movies.stream().filter(m -> m.getId() == id).findFirst();
        if (movieOptional.isPresent()) {
            return new DataBaseAnswer<Movie>(DataBaseAnswer.CODE_SUCCESS, movieOptional.get());
        } else {
            return new DataBaseAnswer<Movie>(DataBaseAnswer.CODE_OBJECT_NOT_FOUND, null);
        }
    }

    @Override
    public DataBaseAnswer<Void> replace(long id, Movie movie) {
        DataBaseAnswer<Void> linkedAnswer = linkedDataBase.replace(id, movie);
        if (linkedAnswer.code == DataBaseAnswer.CODE_SUCCESS) {
            movies.remove(movie);
            DataBaseAnswer<Movie> clonedMovieAnswer = linkedDataBase.get(id);
            movies.add(clonedMovieAnswer.object);
        }
        return linkedAnswer;
    }

    @Override
    public DataBaseAnswer<Void> clear() {
        DataBaseAnswer<Void> linkedAnswer = linkedDataBase.clear();
        if (linkedAnswer.code == DataBaseAnswer.CODE_SUCCESS) {
            movies.clear();
        }
        return linkedAnswer;
    }

    @Override
    public DataBaseAnswer<List<Long>> getIdList() {
        List<Long> res = movies.stream().map(m -> m.getId()).collect(Collectors.toList());
        return new DataBaseAnswer<List<Long>>(DataBaseAnswer.CODE_SUCCESS, res);
    }

    @Override
    public DataBaseAnswer<Movie> getMaxElement() {
        Movie res;
        try {
            res = movies.last().clone();
        } catch (NoSuchElementException e) {
            res = null;
        }
        return new DataBaseAnswer<Movie>(DataBaseAnswer.CODE_SUCCESS, res);
    }

    @Override
    public DataBaseAnswer<Movie> getMinElement() {
        Movie res;
        try {
            res = movies.first().clone();
        } catch (NoSuchElementException e) {
            res = null;
        }
        return new DataBaseAnswer<Movie>(DataBaseAnswer.CODE_SUCCESS, res);
    }

    @Override
    public DataBaseAnswer<List<Movie>> searchByOperator(Person operator) {
        List<Movie> res = movies.stream().filter(m -> m.getOperator().equals(operator)).map(m -> m.clone()).collect(Collectors.toList());
        return new DataBaseAnswer<List<Movie>>(DataBaseAnswer.CODE_SUCCESS, res);
    }

    @Override
    public DataBaseAnswer<Void> load() {
        DataBaseAnswer<Void> linkedAnswer = linkedDataBase.load();
        if (linkedAnswer.code == DataBaseAnswer.CODE_SUCCESS) {
            syncMovies();
        }
        return linkedAnswer;
    }

    @Override
    public DataBaseAnswer<Void> save() {
        return linkedDataBase.save();
    }
}
