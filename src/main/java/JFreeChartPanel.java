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

public class JFreeChartPanel extends JPanel implements  PropertyChangeListener {
    private DirectoryManager directoryManager;
    private final JFreeChart chart;
    private static DefaultPieDataset dataset;

    public JFreeChartPanel(){

        directoryManager = DirectoryManager.getInstance();
        JButton buttonLocal = new JButton("Choose a Java Directory...");

        dataset = createDataset();
        chart = ChartFactory.createPieChart("Lines of Code", dataset, false, true, false);
        chart.setBackgroundPaint(new Color(172, 248, 199));
        chart.getPlot().setBackgroundPaint(new Color(172, 248, 199));
        chart.getPlot().setOutlineVisible(false);
        chart.setAntiAlias(true);
        ChartPanel chartPanel = new ChartPanel(chart);
        setLayout(new BorderLayout());
        add(buttonLocal, BorderLayout.NORTH);
        add(chartPanel,BorderLayout.CENTER);
        buttonLocal.addActionListener(new ButtonActions());
        directoryManager.addPropertyChangeListener(this);

    }

    private void updateDataset() {
        dataset.setValue("Lines", Singleton.getInstance().getLines());
        dataset.setValue("LOC", Singleton.getInstance().getLoc());
    }

    private static DefaultPieDataset createDataset(){
        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("Lines", 10);
        dataset.setValue("LOC", 11);
        return dataset;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        dataset.clear();
        for(FileParser file: directoryManager.getParsedFileList()) {
            dataset.setValue(file.getName(), file.getLines());
        }
        this.repaint();
    }
}
