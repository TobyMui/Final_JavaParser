import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class JFreeChartPanel extends JPanel implements ActionListener {
    private DirectoryManager directoryManager;
    private final JFreeChart chart;
    private static DefaultPieDataset dataset;

    public JFreeChartPanel(){
        directoryManager = DirectoryManager.getInstance();
        JButton buttonLocal = new JButton("Choose a Java File...");
        dataset = createDataset();
        chart = ChartFactory.createPieChart("Code Analytics", dataset, false, true, false);
        chart.setBackgroundPaint(new Color(172, 248, 199));
        chart.getPlot().setBackgroundPaint(new Color(172, 248, 199));
        chart.getPlot().setOutlineVisible(false);
        ChartPanel chartPanel = new ChartPanel(chart);
        setLayout(new BorderLayout());
        add(buttonLocal, BorderLayout.NORTH);
        add(chartPanel,BorderLayout.SOUTH);
        buttonLocal.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JFileChooser directoryChooser = new JFileChooser();
        directoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        directoryChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Java Files", "java"));
        directoryChooser.setDialogTitle("Choose a Java File");
        int result = directoryChooser.showOpenDialog(this);
        try {
            if (result == JFileChooser.APPROVE_OPTION) {
                directoryManager.setDirectoryPath(directoryChooser.getSelectedFile().getAbsolutePath());
                for(String file:directoryManager.getJavaFilesList()){
                }
//                parseJavaFile(fileChooser.getSelectedFile().getAbsolutePath());
            }
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }


    private void updateDataset() {
        dataset.setValue("Lines", Singleton.getInstance().getLines());
        dataset.setValue("LOC", Singleton.getInstance().getLoc());
    }

    private static DefaultPieDataset createDataset(){
        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("Lines", Singleton.getInstance().getLines());
        dataset.setValue("LOC", Singleton.getInstance().getLoc());
        return dataset;
    }
}
