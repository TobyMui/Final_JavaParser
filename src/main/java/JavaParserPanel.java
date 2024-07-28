import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class JavaParserPanel extends JPanel implements ActionListener {

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
        buttonLocal.addActionListener(this);
        directoryManager = DirectoryManager.getInstance();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        textArea.setText("");
        JFileChooser directoryChooser = new JFileChooser();
        directoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        directoryChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Java Files", "java"));
        directoryChooser.setDialogTitle("Choose a Directory");
        FileParser currentFile;
        int result = directoryChooser.showOpenDialog(this);
        try {
            if (result == JFileChooser.APPROVE_OPTION) {
                directoryManager.setDirectoryPath(directoryChooser.getSelectedFile().getAbsolutePath());
                for(String file:directoryManager.getJavaFilesList()){
                    currentFile = new FileParser(file);
                    textArea.append("Name" + currentFile.getName() + "\n");
                    textArea.append("Lines: " + currentFile.getLines() + "\n");
                    textArea.append("LOC: " + currentFile.getLOC() + "\n");
                    textArea.append("ELOC: " + currentFile.getELOC() + "\n");
                    textArea.append("ILOC: " + currentFile.getILOC() + "\n");
                    textArea.append("Cyclomatic Complexity:" + currentFile.getCyclomaticComplexity() + "\n");
                    textArea.append("--------------------------------"+ "\n");
                }
            }
        } catch (Exception ex) {
            textArea.setText("Error: " + ex.getMessage());
        }
    }

}
