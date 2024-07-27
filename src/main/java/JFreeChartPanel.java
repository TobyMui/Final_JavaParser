import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class JFreeChartPanel extends JPanel implements PropertyChangeListener {
    private final JFreeChart chart;
    private static DefaultPieDataset dataset;

    public JFreeChartPanel(){
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
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        updateDataset();
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
