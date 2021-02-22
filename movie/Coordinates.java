package movie;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "coordinates")
@XmlRootElement
public class Coordinates {
    
    private int x;
    private long y;

    public Coordinates(int x, long y)
    {
        this.x = x;
        this.y = y;
    }

    public Coordinates() {

    }

    public int getX()
    {
        return x;
    }

    public void setX(int x)
    {
        this.x = x;
    }

    public long getY()
    {
        return y;
    }

    public void setY(long y)
    {
        this.y = y;
    }

    @Override
    public String toString()
    {
        return "["+Integer.toString(x)+","+Long.toString(y)+"]";
    }

    public Coordinates clone()
    {
        Coordinates res = new Coordinates(this.x, this.y);
        return res;
    }
}
