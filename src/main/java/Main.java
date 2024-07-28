import javax.swing.*;

public class Main extends JFrame {
    public static void main(String[] args) {
        Main main = new Main();
        main.setSize(800,800);
        main.setTitle("Java Analyzer");
        main.setVisible(true);
        main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        main.setResizable(false);
    }


    public Main(){
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Code Analytics", new JavaParserPanel());

        JFreeChartPanel chartPanel = new JFreeChartPanel();
        tabbedPane.addTab("Code Analytics Pie Chart" , chartPanel);
        add(tabbedPane);
    }


}
