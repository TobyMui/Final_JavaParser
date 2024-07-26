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
import java.util.List;
import java.util.Optional;


public class JavaParserPanel extends JPanel implements ActionListener {

    private JTextArea textArea;

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
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        textArea.setText("");
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Java Files", "java"));
        fileChooser.setDialogTitle("Choose a Java File");
        int result = fileChooser.showOpenDialog(this);
        try {
            if (result == JFileChooser.APPROVE_OPTION) {
                parseJavaFile(fileChooser.getSelectedFile().getAbsolutePath());
            }
        } catch (Exception ex) {
            textArea.setText("Error: " + ex.getMessage());
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
