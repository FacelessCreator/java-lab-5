package etc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Serializing {
    public static byte[] serializeObject(Object obj) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(obj);
            out.flush();
            byte[] res = bos.toByteArray();
            bos.close();
            return res;
        } catch (IOException e) {
            System.err.println("This is impossible!");
            return null;
        }
    }

    public static Object deserializeObject(byte[] bytes) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bis);
            Object res = ois.readObject();
            return res;
        } catch (ClassNotFoundException | IOException e) {
            return null;
        }
    }
}
