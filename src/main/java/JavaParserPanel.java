import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.type.Type;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class JavaParserPanel extends JPanel implements ActionListener {

    private JTextArea textArea;
    private DirectoryManager directoryManager;

    public JavaParserPanel() {
        JButton buttonLocal = new JButton("Choose a Java File...");
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
        directoryChooser.setDialogTitle("Choose a Java File");
        int result = directoryChooser.showOpenDialog(this);
        try {
            if (result == JFileChooser.APPROVE_OPTION) {
                directoryManager.setDirectoryPath(directoryChooser.getSelectedFile().getAbsolutePath());
                getJavaFiles(directoryManager.getDirectoryPath());
                for(String file:directoryManager.getJavaFiles()){
                    parseDirectory(file);
                }
//                parseJavaFile(fileChooser.getSelectedFile().getAbsolutePath());
            }
        } catch (Exception ex) {
            textArea.setText("Error: " + ex.getMessage());
        }
    }

    //Takes the DirectoryPatch returns an ArrayList of java file paths
    public void getJavaFiles(String directoryPath) throws IOException {
        ArrayList<String> javaFiles = new ArrayList<>();
        try (Stream<Path> paths = Files.walk(Paths.get(directoryPath))) {
            javaFiles = paths.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".java"))
                    .map(Path::toString)
                    .collect(Collectors.toCollection(ArrayList::new));
        }
        //Update the ArrayList
        directoryManager.getJavaFiles().clear();
        directoryManager.getJavaFiles().addAll(javaFiles);
    }


    public void parseDirectory(String filePath) throws IOException {
        InputStream in = new FileInputStream(filePath);
        JavaParser parser = new JavaParser();
        Optional<CompilationUnit> result = parser.parse(in).getResult();
        if (result.isPresent()) {
            CompilationUnit compilationUnit = result.get();
            List<ClassOrInterfaceDeclaration> classDeclarations = compilationUnit.findAll(ClassOrInterfaceDeclaration.class);
            for (ClassOrInterfaceDeclaration classDeclaration : classDeclarations) {
                textArea.append("Class: " + classDeclaration.getName() + "\n");
                textArea.append("Lines: " + classDeclaration.getEnd().get().line + "\n");
//                System.out.println(classDeclaration.toString());
                int loc = CalculateLOC(classDeclaration.toString());
                textArea.append("Lines of Code: " + loc + "\n");
                // Extract field declarations within the class
//                textArea.append("\nFields:\n");
//                for (FieldDeclaration field : classDeclaration.getFields()) {
//                    for (VariableDeclarator variable : field.getVariables()) {
//                        Type fieldType = variable.getType();
//                        String fieldName = variable.getNameAsString();
//                        textArea.append("Field: " + fieldName + " Type: " + fieldType + "\n");
//                    }
//                }
            }
        }
    }


    public void parseJavaFile(String filePath) throws IOException {
        InputStream in = new FileInputStream(filePath);
        JavaParser parser = new JavaParser();
        Optional<CompilationUnit> result = parser.parse(in).getResult();
        if (result.isPresent()) {
            CompilationUnit compilationUnit = result.get();
            List<ClassOrInterfaceDeclaration> classDeclarations = compilationUnit.findAll(ClassOrInterfaceDeclaration.class);
            for (ClassOrInterfaceDeclaration classDeclaration : classDeclarations) {
                textArea.append("Class: " + classDeclaration.getName() + "\n");
                textArea.append("Lines: " + classDeclaration.getEnd().get().line + "\n");
//                System.out.println(classDeclaration.toString());
                int loc = CalculateLOC(classDeclaration.toString());
                textArea.append("Lines of Code: " + loc + "\n");
                // Extract field declarations within the class
                textArea.append("\nFields:\n");
                for (FieldDeclaration field : classDeclaration.getFields()) {
                    for (VariableDeclarator variable : field.getVariables()) {
                        Type fieldType = variable.getType();
                        String fieldName = variable.getNameAsString();
                        textArea.append("Field: " + fieldName + " Type: " + fieldType + "\n");
                    }
                }
            }
        }
    }

    private int CalculateLOC(String code) {
        int loc = 0;
        String[] lines = code.split("\n");
        for (String line : lines) {
            String trimmedLine = line.trim();
            if (!trimmedLine.startsWith("//")) {
                loc++;
            }
        }
        return loc;
    }


}
