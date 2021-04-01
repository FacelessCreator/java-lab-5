import movie.*;
import etc.Serializing;

public class Temp {
    public static void main(String[] args) {
        Movie obj = new Movie("Arch", new Coordinates(1, 2), 4, 500, MovieGenre.COMEDY, MpaaRating.PG_13, new Person("Gregor", "1234", Color.BROWN, Country.ITALY));
        byte[] arr = Serializing.serializeObject(obj);
        Object obj2 = Serializing.deserializeObject(arr);
        System.out.println(obj2);
    }
}
