package movie;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Coordinates do describe objects
 * @author Alexandr Shchukin
 * @version 1.0
 */
@XmlType(name = "coordinates")
@XmlRootElement
public class Coordinates implements Serializable {
    
    /** x coordinate */
    private int x;
    /** y coordinate */
    private long y;

    /**
     * Constructor
     * @param x
     * @param y
     */
    public Coordinates(int x, long y)
    {
        this.x = x;
        this.y = y;
    }

    /**
     * Constructor
     */
    public Coordinates() {

    }

    /**
     * get x coordinate
     * @return x
     */
    public int getX()
    {
        return x;
    }

    /**
     * set x coordinate
     * @param x
     */
    public void setX(int x)
    {
        this.x = x;
    }

    /**
     * get y coordinate
     * @return y
     */
    public long getY()
    {
        return y;
    }

    /**
     * set y coordinate
     * @param y
     */
    public void setY(long y)
    {
        this.y = y;
    }

    /**
     * Convertation to string
     * @return readable form [x,y]
     */
    @Override
    public String toString()
    {
        return "["+Integer.toString(x)+","+Long.toString(y)+"]";
    }

    /**
     * get a copy of this object
     * @return copy
     */
    public Coordinates clone()
    {
        Coordinates res = new Coordinates(this.x, this.y);
        return res;
    }
}
