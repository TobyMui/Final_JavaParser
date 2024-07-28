import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

import java.io.*;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

public class FileParser {
    String name;
    private int lines,eloc,loc,ilines,cyclomaticComplexity;

    public FileParser(String filePath) throws FileNotFoundException {
        InputStream in = new FileInputStream(filePath);
        JavaParser parser = new JavaParser();

        Optional<CompilationUnit> result = parser.parse(in).getResult();
        if (result.isPresent()) {
            CompilationUnit compilationUnit = result.get();
            List<ClassOrInterfaceDeclaration> classDeclarations = compilationUnit.findAll(ClassOrInterfaceDeclaration.class);
            for (ClassOrInterfaceDeclaration classDeclaration : classDeclarations) {
                name = String.valueOf(classDeclaration.getName());
                lines = classDeclaration.getEnd().get().line;
                loc = CalculateLOC(classDeclaration.toString());
                eloc = CalculateELOC(classDeclaration.toString());
                ilines = CalculateILOC(classDeclaration.toString());
                cyclomaticComplexity = CalculateCC(classDeclaration.toString());
            }
        }
    }

    public String getName(){
        return name;
    }


    public int getCyclomaticComplexity(){
        return cyclomaticComplexity;
    }

    public int getLines(){
        return lines;
    }

    public int getLOC(){
        return loc;
    }

    public int getELOC(){
        return  eloc;
    }

    public int getILOC(){
        return ilines;
    }

    public int CalculateLOC(String code){
        int counter = 0;
        String[] lines = code.split("\n");
        for (String line : lines) {
            String trimmedLine = line.trim();
            if (!trimmedLine.startsWith("//")) {
                counter++;
            }
        }
        return counter;
    }

    public int CalculateELOC(String code){
        int loc = 0;
        String[] lines = code.split("\n");
        for (String line : lines) {
            String trimmedLine = line.trim();
            if (!trimmedLine.isEmpty() && !trimmedLine.startsWith("//") && !trimmedLine.startsWith("/*") && !trimmedLine.startsWith("*")){
                loc++;
            }
        }
        return loc;
    }

    public int CalculateILOC(String code){
        int counter = 0;
        String[] lines = code.split("\n");
        for (String line : lines) {
            String trimmedLine = line.trim();
            if (trimmedLine.contains(";")){
                counter++;
            }
        }
        return counter;
    }

    public int CalculateCC(String code){
        int counter = 0;
        String[] lines = code.split("\n");
        for (String line : lines) {
            String trimmedLine = line.trim();
            if (trimmedLine.contains("if") || trimmedLine.contains("case") || trimmedLine.contains("for") || trimmedLine.contains("while")){
                counter++;
            }
        }
        counter++;
        return counter;
    }
}
