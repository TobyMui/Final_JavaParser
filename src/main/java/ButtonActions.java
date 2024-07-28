import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/***
 * @author Bryan Gomez
 *
 */

public class ButtonActions extends Component implements ActionListener {
    private DirectoryManager directoryManager = DirectoryManager.getInstance();

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals("Choose a Java Directory...")) {
            JFileChooser directoryChooser = new JFileChooser();
            directoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            directoryChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Java Files", "java"));
            directoryChooser.setDialogTitle("Choose a Java File");
            int result = directoryChooser.showOpenDialog(this);
            try {
                if (result == JFileChooser.APPROVE_OPTION) {
                    directoryManager.setDirectoryPath(directoryChooser.getSelectedFile().getAbsolutePath());
                }
            } catch (Exception ex) {
                System.out.println(ex);
            }
        }else{
            directoryManager.setChartDisplayMode(e.getActionCommand());
        }

    }
}
