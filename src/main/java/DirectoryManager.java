import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;

public class DirectoryManager {
    private static DirectoryManager instance;
    PropertyChangeSupport pcs;

    private String directoryPath;
    private ArrayList<String> javaFiles;

    private DirectoryManager(){
        pcs = new PropertyChangeSupport(this);
    }


    public static DirectoryManager getInstance() {
        if (instance == null) {
            instance = new DirectoryManager();
        }
        return instance;
    }

    //PropertyChange Methods
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    public String getDirectoryPath() {
        return directoryPath;
    }
    public void setDirectoryPath(String directoryPath) {
        this.directoryPath = directoryPath;
    }

    public ArrayList<String> getJavaFiles() {
        return javaFiles;
    }




}
