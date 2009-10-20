package edu.asu.commons.irrigation.client;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;

import edu.asu.commons.irrigation.server.ClientData;
import edu.asu.commons.irrigation.server.GroupDataModel;
import edu.asu.commons.net.Identifier;

/**
 * Presents the group contributions as a pie chart.
 */
public class PieChart extends JPanel {

	private static final long serialVersionUID = -5382293105043214105L;

	private ChartPanel chartPanel;
	
    public void setClientData(final ClientData clientData) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                final PieDataset dataset = createDataset(clientData);
                final JFreeChart chart = createChart(dataset);
                remove(chartPanel);
                chartPanel = new ChartPanel(chart);
                chartPanel.setPreferredSize(new Dimension(500, 270));
                add(chartPanel);
                setBounds(new Rectangle(0,0,500,270));
            }
        });
    }

    /**
     * Creates a pie dataset out of the client 
     * @return a sample dataset.
     */
    private PieDataset createDataset(ClientData clientData) {
        final DefaultPieDataset defaultPieDataset = new DefaultPieDataset();
        GroupDataModel groupDataModel = clientData.getGroupDataModel();
        Map<Identifier,ClientData>clientDataMap = groupDataModel.getClientDataMap();
        for (ClientData currentClientData : clientDataMap.values()) {
            StringBuilder labelBuilder = new StringBuilder();
            if (currentClientData.getId().equals(clientData.getId())) {
                labelBuilder.append("You");
            }
            else {
                labelBuilder.append(currentClientData.getPriorityAsString());
            }
            labelBuilder.append(" invested ").append(currentClientData.getInvestedTokens()).append(" token(s)");
            defaultPieDataset.setValue(labelBuilder.toString(), currentClientData.getInvestedTokens());
        }
        return defaultPieDataset;        
    }
    
    // ****************************************************************************
    // * JFREECHART DEVELOPER GUIDE                                               *
    // * The JFreeChart Developer Guide, written by David Gilbert, is available   *
    // * to purchase from Object Refinery Limited:                                *
    // *                                                                          *
    // * http://www.object-refinery.com/jfreechart/guide.html                     *
    // *                                                                          *
    // * Sales are used to provide funding for the JFreeChart project - please    * 
    // * support us so that we can continue developing free software.             *
    // ****************************************************************************
    
    /**
     * Creates a chart.
     * 
     * @param dataset  the dataset.
     * 
     * @return a chart.
     */
    private JFreeChart createChart(final PieDataset dataset) {
        
        final JFreeChart chart = ChartFactory.createPieChart(
            "Tokens Contributed",  // chart title
            dataset,             // data
            false,               // include legend
            true,
            false
        );

        final PiePlot plot = (PiePlot) chart.getPlot();
        //plot.setLabelGenerator(new CustomLabelGenerator());
        plot.setLabelFont(new Font("SansSerif", Font.PLAIN, 12));
        plot.setNoDataMessage("No data available");
        plot.setCircular(false);
        plot.setLabelGap(0.02);
        return chart;
        
    }
    
}
