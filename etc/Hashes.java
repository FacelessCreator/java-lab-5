package etc;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public abstract class Hashes {
    public static final String getSHA384(String string) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-384");
            byte[] digest = messageDigest.digest(string.getBytes());
            BigInteger no = new BigInteger(1, digest);
            return no.toString(16);
        } catch (NoSuchAlgorithmException e) {
            // this is impossible
            return "THIS IS IMPOSSIBLE";
        }
    }
}
