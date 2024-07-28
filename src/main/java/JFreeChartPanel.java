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


/***
 * @author Toby Mui
 *
 */

public class JFreeChartPanel extends JPanel implements  PropertyChangeListener {
    private DirectoryManager directoryManager;

    private final JFreeChart chart;
    private static DefaultPieDataset dataset;

    public JFreeChartPanel(){
        directoryManager = DirectoryManager.getInstance();
        //File Button
        JButton buttonLocal = new JButton("Choose a Java Directory...");
        //Analytics Button
        JButton buttonLines = new JButton("Lines");
        JButton buttonLOC = new JButton("LOC");
        JButton buttonELOC = new JButton("ELOC");
        JButton buttonILOC = new JButton("ILOC");
        JButton buttonCC = new JButton("CC");
        JPanel metricPanel = new JPanel(new GridLayout(1,5));
        metricPanel.add(buttonILOC);
        metricPanel.add(buttonLines);
        metricPanel.add(buttonLOC);
        metricPanel.add(buttonELOC);
        metricPanel.add(buttonCC);

        dataset = createDataset();
        chart = ChartFactory.createPieChart("Please Choose a Directory", dataset, false, true, false);
        chart.setBackgroundPaint(new Color(172, 248, 199));
        chart.getPlot().setBackgroundPaint(new Color(172, 248, 199));
        chart.getPlot().setOutlineVisible(false);
        chart.setAntiAlias(true);
        ChartPanel chartPanel = new ChartPanel(chart);

        setLayout(new BorderLayout());
        add(buttonLocal, BorderLayout.NORTH);
        add(metricPanel, BorderLayout.SOUTH);
        add(chartPanel,BorderLayout.CENTER);
        buttonLocal.addActionListener(new ButtonActions());
        buttonLines.addActionListener(new ButtonActions());
        buttonLOC.addActionListener(new ButtonActions());
        buttonELOC.addActionListener(new ButtonActions());
        buttonILOC.addActionListener(new ButtonActions());
        buttonCC.addActionListener(new ButtonActions());

        directoryManager.addPropertyChangeListener(this);
    }

    private static DefaultPieDataset createDataset(){
        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("Lines", 10);
        dataset.setValue("LOC", 10);
        dataset.setValue("eLOC",10);
        dataset.setValue("iLOC",10);
        return dataset;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        dataset.clear();
        String currentMode = directoryManager.getChartDisplayMode();
        switch(currentMode){
            case "Lines":
                chart.setTitle("Lines Pie Chart");
                for(FileParser file: directoryManager.getParsedFileList()) {
                    dataset.setValue(file.getName(), file.getLines());
                }
                break;
            case "LOC":
                chart.setTitle("LOC Pie Chart");
                for(FileParser file: directoryManager.getParsedFileList()) {
                    dataset.setValue(file.getName(), file.getLOC());
                }
                break;
            case "ELOC":
                chart.setTitle("ELOC Pie Chart");
                for(FileParser file: directoryManager.getParsedFileList()) {
                    dataset.setValue(file.getName(), file.getELOC());
                }
                break;
            case "ILOC":
                chart.setTitle("ILOC Pie Chart");
                for(FileParser file: directoryManager.getParsedFileList()) {
                    dataset.setValue(file.getName(), file.getILOC());
                }
                break;
            case "CC":
                chart.setTitle("CC Pie Chart");
                for(FileParser file: directoryManager.getParsedFileList()) {
                    dataset.setValue(file.getName(),file.getCyclomaticComplexity());
                }
                break;
        }
        this.repaint();
    }
}
