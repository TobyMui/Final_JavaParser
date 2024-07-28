import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/***
 * @author Toby
 * Singleton Implementation
 */

public class DirectoryManager {
    private static DirectoryManager instance;
    PropertyChangeSupport pcs;
    private String chartDisplayMode; //manages which
    private String directoryPath;
    private ArrayList<String> javaFileList; //Updated when Directory is set
    private ArrayList<FileParser> parsedFileList;

    private DirectoryManager(){
        pcs = new PropertyChangeSupport(this);
        javaFileList = new ArrayList<>();
        parsedFileList = new ArrayList<>();
    }


    public static DirectoryManager getInstance() {
        if (instance == null) {
            instance = new DirectoryManager();
        }
        return instance;
    }

    public void setChartDisplayMode(String chartDisplayMode){
        this.chartDisplayMode = chartDisplayMode;
        pcs.firePropertyChange("chartDisplayMode", null, chartDisplayMode);
    }

    public String getChartDisplayMode(){
        if(chartDisplayMode == null){
            return "Lines";
        }else{
            return chartDisplayMode;
        }
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
        //Update javaFileList
        javaFileList.clear();
        javaFileList.addAll(javaFiles);

        //Update parsed files list
        parsedFileList.clear();
        FileParser currentFile;
        for(String file: this.javaFileList){
            currentFile = new FileParser(file);
            parsedFileList.add(currentFile);
        }
        pcs.firePropertyChange(null, null, null);
    }

    public ArrayList<String> getJavaFilesList() {
        return javaFileList;
    }

    public ArrayList<FileParser> getParsedFileList() {
        return parsedFileList;
    }
}
