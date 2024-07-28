import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.layout.mxOrganicLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxGraph;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/***
 * @author Bryan Gomez
 * Class Diagram Panel
 */

public class ClassDiagram extends JPanel implements ActionListener {
    private final CustomGraph graph;
    private final Object parent;
    private final Map<String, mxCell> classCells; // Map to keep track of class cells

    public ClassDiagram() {
        JButton buttonLocal = new JButton("Choose a Java Source Directory...");
        graph = new CustomGraph();
        parent = graph.getDefaultParent();
        classCells = new HashMap<>();
        mxGraphComponent graphComponent = new mxGraphComponent(graph);

        // Layout
        setLayout(new BorderLayout());
        add(buttonLocal, BorderLayout.NORTH);
        add(graphComponent, BorderLayout.CENTER);

        // Behavior
        buttonLocal.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JFileChooser directoryChooser = new JFileChooser();
        directoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        directoryChooser.setDialogTitle("Choose a Directory");

        int result = directoryChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            String directoryPath = directoryChooser.getSelectedFile().getAbsolutePath();
            try {
                DirectoryManager.getInstance().setDirectoryPath(directoryPath);
                buildClassDiagram();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void buildClassDiagram() throws Exception {
        graph.getModel().beginUpdate();
        try {
            for (String filePath : DirectoryManager.getInstance().getJavaFilesList()) {
                parseAndAddToGraph(filePath);
            }
            createEdges(); // Create edges after all classes are added
        } finally {
            graph.getModel().endUpdate();
        }

        // Use mxOrganicLayout to arrange the graph
        mxIGraphLayout layout = new mxOrganicLayout(graph);
        layout.execute(parent);
    }

    private void parseAndAddToGraph(String filePath) throws Exception {
        InputStream in = new FileInputStream(filePath);
        JavaParser parser = new JavaParser();
        Optional<CompilationUnit> result = parser.parse(in).getResult();

        if (result.isPresent()) {
            CompilationUnit compilationUnit = result.get();
            List<ClassOrInterfaceDeclaration> classDeclarations = compilationUnit.findAll(ClassOrInterfaceDeclaration.class);

            for (ClassOrInterfaceDeclaration classDeclaration : classDeclarations) {
                String className = classDeclaration.getNameAsString();
                if (!classCells.containsKey(className)) {
                    // Create a composite cell for the class
                    mxCell classCell = (mxCell) graph.insertVertex(parent, null, "", 100, 100, 150, 30 + 20 * classDeclaration.getFields().size(), "shape=rectangle;whiteSpace=wrap;fillColor=white;strokeColor=black");

                    // Add the class name as a label inside the composite cell
                    mxCell titleCell = new mxCell(className, new mxGeometry(0, 0, 150, 30), "shape=rectangle;fillColor=lightgray;strokeColor=black;align=center;verticalAlign=middle");
                    titleCell.setVertex(true);
                    graph.addCell(titleCell, classCell);

                    // Add attributes inside the composite cell
                    int attributeHeight = 30;
                    for (var field : classDeclaration.getFields()) {
                        String fieldName = field.getVariables().get(0).getNameAsString();
                        mxCell attributeCell = new mxCell(fieldName, new mxGeometry(0, attributeHeight, 150, 20), "shape=rectangle;fillColor=white;strokeColor=black;align=left;verticalAlign=middle");
                        attributeCell.setVertex(true);
                        graph.addCell(attributeCell, classCell);
                        attributeHeight += 20;
                    }

                    classCells.put(className, classCell); // Store class cell
                }

                // Handle inheritance
                for (ClassOrInterfaceType extendedType : classDeclaration.getExtendedTypes()) {
                    String parentClassName = extendedType.getNameAsString();
                    mxCell parentClassCell = classCells.computeIfAbsent(parentClassName, key -> createEmptyClassCell(key));
                    graph.insertEdge(parent, null, "extends", classCells.get(className), parentClassCell);
                }

                // Handle interface implementation
                for (ClassOrInterfaceType implementedType : classDeclaration.getImplementedTypes()) {
                    String interfaceName = implementedType.getNameAsString();
                    mxCell interfaceCell = classCells.computeIfAbsent(interfaceName, key -> createEmptyClassCell(key));
                    graph.insertEdge(parent, null, "implements", classCells.get(className), interfaceCell);
                }
            }
        }
    }

    private mxCell createEmptyClassCell(String name) {
        return (mxCell) graph.insertVertex(parent, null, name, 100, 100, 150, 30, "shape=rectangle;whiteSpace=wrap;fillColor=white;strokeColor=black");
    }

    private void createEdges() {
        for (String className : classCells.keySet()) {
            mxCell classCell = classCells.get(className);
            ClassOrInterfaceDeclaration classDeclaration = findClassDeclaration(className);
            if (classDeclaration != null) {
                for (ClassOrInterfaceType extendedType : classDeclaration.getExtendedTypes()) {
                    String parentClassName = extendedType.getNameAsString();
                    mxCell parentClassCell = classCells.get(parentClassName);
                    if (parentClassCell != null) {
                        graph.insertEdge(parent, null, "extends", classCell, parentClassCell);
                    }
                }
                for (ClassOrInterfaceType implementedType : classDeclaration.getImplementedTypes()) {
                    String interfaceName = implementedType.getNameAsString();
                    mxCell interfaceCell = classCells.get(interfaceName);
                    if (interfaceCell != null) {
                        graph.insertEdge(parent, null, "implements", classCell, interfaceCell);
                    }
                }
            }
        }
    }

    private ClassOrInterfaceDeclaration findClassDeclaration(String className) {
        for (String filePath : DirectoryManager.getInstance().getJavaFilesList()) {
            try (InputStream in = new FileInputStream(filePath)) {
                JavaParser parser = new JavaParser();
                Optional<CompilationUnit> result = parser.parse(in).getResult();
                if (result.isPresent()) {
                    CompilationUnit compilationUnit = result.get();
                    List<ClassOrInterfaceDeclaration> classDeclarations = compilationUnit.findAll(ClassOrInterfaceDeclaration.class);
                    for (ClassOrInterfaceDeclaration classDeclaration : classDeclarations) {
                        if (classDeclaration.getNameAsString().equals(className)) {
                            return classDeclaration;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private static class CustomGraph extends mxGraph {
        @Override
        public boolean isCellConnectable(Object cell) {
            return false; // Disable cell connections
        }
    }
}