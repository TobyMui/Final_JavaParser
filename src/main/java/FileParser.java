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
import java.util.Optional;

public class FileParser {
    public String filepath;
    public InputStream in;
    public JavaParser parser;
    public Optional<CompilationUnit> result;
    CompilationUnit compilationUnit;

    public FileParser(String filePath) throws FileNotFoundException {
        filepath = filePath;
        in = new FileInputStream(filePath);
        parser = new JavaParser();
        result = parser.parse(in).getResult();
        compilationUnit = result.get();
    }

    public int CalculateLOC(String code){
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
