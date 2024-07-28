import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DirectoryManager {
    private static DirectoryManager instance;
    PropertyChangeSupport pcs;

    private String directoryPath;
    private ArrayList<String> javaFileList; //Updated when Directory is set

    private DirectoryManager(){
        pcs = new PropertyChangeSupport(this);
        javaFileList = new ArrayList<>();
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
    public void setDirectoryPath(String directoryPath) throws IOException {
        this.directoryPath = directoryPath;
        ArrayList<String> javaFiles;
        try (Stream<Path> paths = Files.walk(Paths.get(this.directoryPath))) {
            javaFiles = paths.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".java"))
                    .map(Path::toString)
                    .collect(Collectors.toCollection(ArrayList::new));
        }
        //Update the ArrayList
        javaFileList.clear();
        javaFileList.addAll(javaFiles);
    }

    public ArrayList<String> getJavaFilesList() {
        return javaFileList;
    }
}
