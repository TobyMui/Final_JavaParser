import java.beans.PropertyChangeSupport;

public class Singleton extends PropertyChangeSupport {
    private static Singleton instance;
    private static int lines;
    private static int loc;


    public Singleton() {
        super(new Object());
    }

    public static Singleton getInstance() {
        if (instance == null) {
            instance = new Singleton();
        }
        return instance;
    }
    public int getLines() {
        return lines;
    }

    public void setLines(int lines) {
        this.lines = lines;
    }

    public int getLoc() {
        return loc;
    }

    public void setLoc(int loc) {
        this.loc = loc;
    }
}
