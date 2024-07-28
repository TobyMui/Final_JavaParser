import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class JavaParserPanel extends JPanel implements PropertyChangeListener {

    private JTextArea textArea;
    private DirectoryManager directoryManager;

    public JavaParserPanel() {
        JButton buttonLocal = new JButton("Choose a Java Directory...");
        textArea = new JTextArea();
        textArea.setBackground(new Color(200,200,200));
        JScrollPane scrollPane = new JScrollPane(textArea);
        //layout
        setLayout(new BorderLayout());
        add(buttonLocal, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        //behavior
        buttonLocal.addActionListener(new ButtonActions());
        directoryManager = DirectoryManager.getInstance();
        directoryManager.addPropertyChangeListener(this);
    }


    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        textArea.setText("");
        for(FileParser file: directoryManager.getParsedFileList()) {
            textArea.append("Name" +file.getName() + "\n");
            textArea.append("Lines: " + file.getLines() + "\n");
            textArea.append("LOC: " + file.getLOC() + "\n");
            textArea.append("ELOC: " + file.getELOC() + "\n");
            textArea.append("ILOC: " + file.getILOC() + "\n");
            textArea.append("Cyclomatic Complexity:" + file.getCyclomaticComplexity() + "\n");
            textArea.append("--------------------------------"+ "\n");
        }
    }
}
