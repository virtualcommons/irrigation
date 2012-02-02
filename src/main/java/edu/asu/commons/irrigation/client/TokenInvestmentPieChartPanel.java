package edu.asu.commons.irrigation.client;

import java.awt.BorderLayout;
import java.awt.Font;
import java.util.Map;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;

import edu.asu.commons.irrigation.model.ClientData;
import edu.asu.commons.irrigation.model.GroupDataModel;
import edu.asu.commons.net.Identifier;

/**
 * $Id$
 * 
 * Presents the group contributions as a pie chart.
 *
 * @author <a href='mailto:Allen.Lee@asu.edu'>Allen Lee</a>
 * @version $Rev$
 */
public class TokenInvestmentPieChartPanel extends JPanel {

	private static final long serialVersionUID = -5382293105043214105L;

	private ChartPanel chartPanel;
	
	public TokenInvestmentPieChartPanel() {
		setLayout(new BorderLayout());
	}
	
    public void initialize(final ClientData clientData) {
    	final PieDataset dataset = createPieDataset(clientData);
    	final JFreeChart chart = createChart(dataset);
    	if (chartPanel != null) {
    		remove(chartPanel);
    	}
    	chartPanel = new ChartPanel(chart);
    	add(chartPanel, BorderLayout.CENTER);
    	revalidate();
    }

    /**
     * Creates a pie dataset out of the client 
     * @return a sample dataset.
     */
    private PieDataset createPieDataset(ClientData thisClientData) {
        final DefaultPieDataset defaultPieDataset = new DefaultPieDataset();
        GroupDataModel groupDataModel = thisClientData.getGroupDataModel();
        Map<Identifier,ClientData>clientDataMap = groupDataModel.getClientDataMap();
        for (ClientData clientData : clientDataMap.values()) {
            StringBuilder labelBuilder = new StringBuilder();
            labelBuilder.append(clientData.getPriorityString());
            if (clientData.getId().equals(thisClientData.getId())) {
                labelBuilder.append(" (You)");
            }
            labelBuilder.append(" invested ").append(clientData.getInvestedTokens()).append(" token(s)");
            defaultPieDataset.setValue(labelBuilder.toString(), clientData.getInvestedTokens());
        }
        return defaultPieDataset;        
    }
    
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
        plot.setNoDataMessage("No contributions were made.");
        plot.setCircular(false);
        plot.setLabelGap(0.02);
        return chart;
    }
    
}
