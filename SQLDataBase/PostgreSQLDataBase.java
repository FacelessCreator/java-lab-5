package SQLDataBase;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import database.AuthorizedDataBase;
import database.DataBase;
import database.DataBaseAnswer;
import movie.Color;
import movie.Coordinates;
import movie.Country;
import movie.Movie;
import movie.MovieGenre;
import movie.MpaaRating;
import movie.Person;

public class PostgreSQLDataBase implements AuthorizedDataBase {
    
    private String dataBaseURL;
    private String dataBaseUserName; // user name for SQL DB
    private String dataBasePassword;

    private String invokingUserName = AuthorizedDataBase.DEFAULT_USERNAME; // name user that invokes methods

    private Connection dataBaseConnection;
    private Statement dataBaseStatement;

    private static final int TIMEOUT = 5; // timeout seconds
    private static final String MOVIES_TABLE_NAME = "s312783_movies";
    private static final String ID_SEQUENCE_NAME = "s312783_sequence";

    private Movie movieFromResultSet(ResultSet resultSet) {
        try {
            // 1 id | 2 name | 3 creationtime | 4 oscarscount | 5 length | 6 genre | 7 _mpaarating | 8 coordinates_x | 9 coordinates_y | 10 person_name | 11 person_passportid | 12 person_eyecolor | 13 person_nationality | 14 creatorName
        String opEyeColorString = resultSet.getString(12);
        String movieGenreString = resultSet.getString(6);
        String mpaaRatingString = resultSet.getString(7);
        Coordinates coords = new Coordinates(
            resultSet.getInt(8),
            resultSet.getLong(9)
            );
        Person op = new Person(
            resultSet.getString(10), 
            resultSet.getString(11), 
            opEyeColorString == null ? null : Color.valueOf(opEyeColorString),
            Country.valueOf(resultSet.getString(13))
            );
        Movie movie = new Movie(
            resultSet.getString(2),
            coords,
            resultSet.getLong(4), 
            resultSet.getLong(5), 
            movieGenreString == null ? null : MovieGenre.valueOf(movieGenreString), 
            mpaaRatingString == null ? null : MpaaRating.valueOf(mpaaRatingString), 
            op
            );
        movie.setId(resultSet.getInt(1));
        movie.setCreatorName(resultSet.getString(14));
        movie.setCreationTime(resultSet.getTimestamp(3).toLocalDateTime());
        return movie;
        } catch (SQLException e) {
            return null;
        }
        
    }

    public PostgreSQLDataBase(String dataBaseURL, String userName, String password) {
        this.dataBaseURL = dataBaseURL;
        this.dataBaseUserName = userName;
        this.dataBasePassword = password;

        try {
            dataBaseConnection = DriverManager.getConnection(dataBaseURL, userName, password);
            dataBaseStatement = dataBaseConnection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public DataBaseAnswer<Void> changeUser(String userName) {
        invokingUserName = userName;
        return new DataBaseAnswer<Void>(DataBaseAnswer.CODE_SUCCESS, null);
    }

    @Override
    public DataBaseAnswer<String> about() {
        return new DataBaseAnswer<String>(DataBaseAnswer.CODE_SUCCESS, "SQL data base");
    }

    @Override
    public DataBaseAnswer<String> getStatus() {
        try {
            if (dataBaseConnection.isValid(TIMEOUT)) {
                return new DataBaseAnswer<String>(DataBaseAnswer.CODE_SUCCESS, "online");
            } else {
                return new DataBaseAnswer<String>(DataBaseAnswer.CODE_SUCCESS, "offline");
            }
        } catch (SQLException e) {
            return new DataBaseAnswer<String>(DataBaseAnswer.CODE_SUCCESS, "fail");
        }
    }

    @Override
    public DataBaseAnswer<Integer> length() {
        String query = String.format("select count(id) from %s", MOVIES_TABLE_NAME);
        int result = -1;
        try {
            ResultSet resultSet = dataBaseStatement.executeQuery(query);
            resultSet.next();
            result = resultSet.getInt(1);
            resultSet.close();
        } catch (SQLException e) {
            return new DataBaseAnswer<Integer>(DataBaseAnswer.CODE_SQL_ERROR, result);
        }
        return new DataBaseAnswer<Integer>(DataBaseAnswer.CODE_SUCCESS, result);
    }

    @Override
    public DataBaseAnswer<Long> add(Movie movie) {
        long id;
        try {
            ResultSet resultSet;
            String gettingIdQuery = String.format("select nextval('%s')", ID_SEQUENCE_NAME);
            String addingQuery = "insert into %s values (%d, '%s', current_timestamp, %d, %d, %s, %s, %d, %d, '%s', %s, %s, '%s', '%s')";
            
            resultSet = dataBaseStatement.executeQuery(gettingIdQuery);
            resultSet.next();
            id = resultSet.getInt(1);
            resultSet.close();

            // id | name |        creationtime        | oscarscount | length | genre  | _mpaarating | coordinates_x | coordinates_y | person_name | person_passportid | person_eyecolor | person_nationality | creatorName
            Coordinates coords = movie.getCoordinates();
            Person op = movie.getOperator();
            MovieGenre genre = movie.getGenre();
            MpaaRating mpaaRating = movie.getMpaaRating();
            String opPassportId = op.getPassportId();
            Color opEyeColor = op.getEyeColor();
            addingQuery = String.format(addingQuery, 
                MOVIES_TABLE_NAME,
                id,
                movie.getName(),
                movie.getOscarsCount(), 
                movie.getLength(),
                genre == null ? "null" : String.format("'%s'", genre.name()), 
                mpaaRating == null ? "null" : String.format("'%s'", mpaaRating.name()),
                coords.getX(), 
                coords.getY(), 
                op.getName(), 
                opPassportId == null ? "null" : String.format("'%s'", opPassportId),
                opEyeColor == null ? "null" : String.format("'%s'", opEyeColor.name()),
                op.getNationality().name(),
                invokingUserName
            );
            int result = dataBaseStatement.executeUpdate(addingQuery);
            if (result != 1) {
                return new DataBaseAnswer<Long>(DataBaseAnswer.CODE_OBJECT_ALREADY_EXISTS, null);
            }
        } catch (SQLException e) {
            if (e.getMessage().indexOf("duplicate") != -1) {
                return new DataBaseAnswer<Long>(DataBaseAnswer.CODE_OBJECT_ALREADY_EXISTS, null);
            }
            return new DataBaseAnswer<Long>(DataBaseAnswer.CODE_SQL_ERROR, null);
        }
        return new DataBaseAnswer<Long>(DataBaseAnswer.CODE_SUCCESS, id);
    }

    @Override
    public DataBaseAnswer<Void> remove(long id) {
        try {
            String query = String.format("delete from %s where id=%d and creatorName='%s'", MOVIES_TABLE_NAME, id, invokingUserName);
            int result = dataBaseStatement.executeUpdate(query);
            if (result != 1) {
                return new DataBaseAnswer<Void>(DataBaseAnswer.CODE_OBJECT_NOT_FOUND, null);
            }
        } catch (SQLException e) {
            return new DataBaseAnswer<Void>(DataBaseAnswer.CODE_SQL_ERROR, null);
        }
        return new DataBaseAnswer<Void>(DataBaseAnswer.CODE_SUCCESS, null);
    }

    @Override
    public DataBaseAnswer<Movie> get(long id) {
        Movie movie;
        try {
            String query = String.format("select * from %s where id=%d", MOVIES_TABLE_NAME, id);
            ResultSet resultSet = dataBaseStatement.executeQuery(query);
            if (resultSet.next()) {
                movie = movieFromResultSet(resultSet);
            } else {
                return new DataBaseAnswer<Movie>(DataBaseAnswer.CODE_OBJECT_NOT_FOUND, null);
            }
        } catch (SQLException e) {
            return new DataBaseAnswer<Movie>(DataBaseAnswer.CODE_SQL_ERROR, null);
        }
        return new DataBaseAnswer<Movie>(DataBaseAnswer.CODE_SUCCESS, movie);
    }

    @Override
    public DataBaseAnswer<Void> replace(long id, Movie movie) {
        try {
            String query = "update %s set name='%s', oscarscount=%d, length=%d, genre=%s, _mpaarating=%s, coordinates_x=%d, coordinates_y=%d, person_name='%s', person_passportid=%s, person_eyecolor=%s, person_nationality='%s' where id=%d and creatorName='%s'";
            Coordinates coords = movie.getCoordinates();
            Person op = movie.getOperator();
            MovieGenre genre = movie.getGenre();
            MpaaRating mpaaRating = movie.getMpaaRating();
            String opPassportId = op.getPassportId();
            Color opEyeColor = op.getEyeColor();
            query = String.format(query, 
                MOVIES_TABLE_NAME,
                movie.getName(), 
                movie.getOscarsCount(), 
                movie.getLength(), 
                genre == null ? "null" : String.format("'%s'", genre.name()), 
                mpaaRating == null ? "null" : String.format("'%s'", mpaaRating.name()),
                coords.getX(), 
                coords.getY(), 
                op.getName(), 
                opPassportId == null ? "null" : String.format("'%s'", opPassportId),
                opEyeColor == null ? "null" : String.format("'%s'", opEyeColor.name()),
                op.getNationality().name(),
                id,
                invokingUserName
                );
            int result = dataBaseStatement.executeUpdate(query);
            if (result != 1) {
                return new DataBaseAnswer<Void>(DataBaseAnswer.CODE_OBJECT_NOT_FOUND, null);
            }
        } catch (Exception e) {
            if (e.getMessage().indexOf("duplicate") != -1) {
                return new DataBaseAnswer<Void>(DataBaseAnswer.CODE_OBJECT_ALREADY_EXISTS, null);
            }
            return new DataBaseAnswer<Void>(DataBaseAnswer.CODE_SQL_ERROR, null);
        }
        return new DataBaseAnswer<Void>(DataBaseAnswer.CODE_SUCCESS, null); 
    }

    @Override
    public DataBaseAnswer<Void> clear() {
        try {
            String query = "delete from %s where creatorName='%s'";
            query = String.format(query, MOVIES_TABLE_NAME, invokingUserName);
            dataBaseStatement.executeUpdate(query);
        } catch (Exception e) {
            return new DataBaseAnswer<Void>(DataBaseAnswer.CODE_SQL_ERROR, null);
        }
        return new DataBaseAnswer<Void>(DataBaseAnswer.CODE_SUCCESS, null); 
    }

    @Override
    public DataBaseAnswer<List<Long>> getIdList() {
        List<Long> result = new ArrayList<Long>();
        try {
            String query = "select id from %s";
            query = String.format(query, MOVIES_TABLE_NAME);
            ResultSet resultSet = dataBaseStatement.executeQuery(query);
            while (resultSet.next()) {
                result.add(Long.valueOf(resultSet.getInt(1)));
            }
        } catch (Exception e) {
            return new DataBaseAnswer<List<Long>>(DataBaseAnswer.CODE_SQL_ERROR, null);
        }
        return new DataBaseAnswer<List<Long>>(DataBaseAnswer.CODE_SUCCESS, result); 
    }

    @Override
    public DataBaseAnswer<Movie> getMaxElement() {
        Movie movie;
        try {
            String query = "select * from %s where name = (select max(name) from %s)";
            query = String.format(query, MOVIES_TABLE_NAME, MOVIES_TABLE_NAME);
            ResultSet resultSet = dataBaseStatement.executeQuery(query);
            if (resultSet.next()) {
                movie = movieFromResultSet(resultSet);
            } else {
                return new DataBaseAnswer<Movie>(DataBaseAnswer.CODE_OBJECT_NOT_FOUND, null);
            }
        } catch (SQLException e) {
            return new DataBaseAnswer<Movie>(DataBaseAnswer.CODE_SQL_ERROR, null);
        }
        return new DataBaseAnswer<Movie>(DataBaseAnswer.CODE_SUCCESS, movie);
    }

    @Override
    public DataBaseAnswer<Movie> getMinElement() {
        Movie movie;
        try {
            String query = "select * from %s where name = (select min(name) from %s)";
            query = String.format(query, MOVIES_TABLE_NAME, MOVIES_TABLE_NAME);
            ResultSet resultSet = dataBaseStatement.executeQuery(query);
            if (resultSet.next()) {
                movie = movieFromResultSet(resultSet);
            } else {
                return new DataBaseAnswer<Movie>(DataBaseAnswer.CODE_OBJECT_NOT_FOUND, null);
            }
        } catch (SQLException e) {
            return new DataBaseAnswer<Movie>(DataBaseAnswer.CODE_SQL_ERROR, null);
        }
        return new DataBaseAnswer<Movie>(DataBaseAnswer.CODE_SUCCESS, movie);
    }

    @Override
    public DataBaseAnswer<List<Movie>> searchByOperator(Person operator) {
        List<Movie> result = new ArrayList<Movie>();
        try {
            String query = "select * from %s where person_passportid = '%s'";
            query = String.format(query, MOVIES_TABLE_NAME, operator.getPassportId());
            ResultSet resultSet = dataBaseStatement.executeQuery(query);
            while (resultSet.next()) {
                Movie movie = movieFromResultSet(resultSet);
                result.add(movie);
            }
        } catch (Exception e) {
            return new DataBaseAnswer<List<Movie>>(DataBaseAnswer.CODE_SQL_ERROR, null);
        }
        return new DataBaseAnswer<List<Movie>>(DataBaseAnswer.CODE_SUCCESS, result); 
    }

    @Override
    public DataBaseAnswer<Void> load() {
        return new DataBaseAnswer<Void>(DataBaseAnswer.CODE_OPERATION_UNSUPPORTED, null);
    }

    @Override
    public DataBaseAnswer<Void> save() {
        return new DataBaseAnswer<Void>(DataBaseAnswer.CODE_OPERATION_UNSUPPORTED, null);
    }
}
