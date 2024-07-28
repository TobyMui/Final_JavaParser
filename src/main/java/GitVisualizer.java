import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.revwalk.RevCommit;
import com.mxgraph.layout.mxOrganicLayout;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/***
 * @author Bryan Gomez
 * Git Visualizer Panel
 */

public class GitVisualizer extends JPanel implements ActionListener {
    private final CustomGraph graph;
    private final Object parent;
    private final JButton buttonURL;
    private final JPanel graphPanel;
    private final Map<String, mxCell> commitCells;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public GitVisualizer() {
        // Initialize components
        buttonURL = new JButton("Enter Git Repository URL...");
        graph = new CustomGraph();
        parent = graph.getDefaultParent();
        commitCells = new HashMap<>();
        graphPanel = new JPanel(new BorderLayout());

        // Set up the layout
        setLayout(new BorderLayout());
        add(buttonURL, BorderLayout.NORTH);
        add(graphPanel, BorderLayout.CENTER);

        // Add action listener to the button
        buttonURL.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == buttonURL) {
            // Prompt user for Git repository URL
            String repoUrl = JOptionPane.showInputDialog(this, "Enter Git Repository URL:", "Git Repository URL", JOptionPane.QUESTION_MESSAGE);

            // Check if the user provided a URL
            if (repoUrl == null || repoUrl.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "No URL provided.");
                return;
            }

            // Define the local directory to clone the repository
            String localPath = System.getProperty("java.io.tmpdir") + "/cloned-repo";  // Use temp directory for simplicity
            File localDir = new File(localPath);

            // Delete the directory if it already exists
            if (localDir.exists()) {
                try {
                    deleteDirectory(localDir);
                } catch (IOException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Failed to delete existing directory: " + ex.getMessage());
                    return;
                }
            }

            try {
                // Clone the Git repository
                Git git = Git.cloneRepository()
                        .setURI(repoUrl)
                        .setDirectory(localDir)
                        .call();

                // Create a JGraphX graph
                graph.getModel().beginUpdate();
                try {
                    // Iterate through the commits
                    Iterable<RevCommit> commits = git.log().call();
                    for (RevCommit commit : commits) {
                        String commitId = commit.getName();
                        String commitMessage = commit.getShortMessage();
                        String author = commit.getAuthorIdent().getName();
                        String commitDate = DATE_FORMAT.format(commit.getAuthorIdent().getWhen());

                        String commitInfo = String.format("<html>Author: %s<br/>Date: %s<br/>Message: %s</html>",
                                author, commitDate, commitMessage);

                        if (!commitCells.containsKey(commitId)) {
                            mxCell commitCell = (mxCell) graph.insertVertex(parent, null, commitInfo, 100, 100, 200, 80);
                            commitCells.put(commitId, commitCell);
                        }

                        // Add edges to represent parent-child relationships
                        for (RevCommit parentCommit : commit.getParents()) {
                            String parentId = parentCommit.getName();
                            String parentCommitMessage = parentCommit.getShortMessage();
                            String parentAuthor = parentCommit.getAuthorIdent().getName();
                            String parentCommitDate = DATE_FORMAT.format(parentCommit.getAuthorIdent().getWhen());

                            String parentCommitInfo = String.format("<html>Author: %s<br/>Date: %s<br/>Message: %s</html>",
                                    parentAuthor, parentCommitDate, parentCommitMessage);

                            if (!commitCells.containsKey(parentId)) {
                                mxCell parentCell = (mxCell) graph.insertVertex(parent, null, parentCommitInfo, 100, 100, 200, 80);
                                commitCells.put(parentId, parentCell);
                            }
                            graph.insertEdge(parent, null, "", commitCells.get(commitId), commitCells.get(parentId));
                        }
                    }
                } finally {
                    graph.getModel().endUpdate();
                }

                // Display the graph
                displayGraph();

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to clone repository: " + ex.getMessage());
            }
        }
    }

    private void displayGraph() {
        // Remove existing graph component if any
        graphPanel.removeAll();

        // Create a new graph component
        mxGraphComponent graphComponent = new mxGraphComponent(graph);
        graphPanel.add(graphComponent, BorderLayout.CENTER);

        // Use an organic layout to arrange the graph
        mxIGraphLayout layout = new mxOrganicLayout(graph);
        layout.execute(graph.getDefaultParent());

        // Refresh the panel to show the new graph
        graphPanel.revalidate();
        graphPanel.repaint();
    }

    // Helper method to delete a directory
    private void deleteDirectory(File directory) throws IOException {
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    deleteDirectory(file);
                }
            }
        }
        Files.delete(directory.toPath());
    }

    private static class CustomGraph extends mxGraph {
        @Override
        public boolean isCellConnectable(Object cell) {
            return false; // Disable cell connections
        }
    }
}