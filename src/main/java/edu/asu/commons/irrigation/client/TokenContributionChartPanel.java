package edu.asu.commons.irrigation.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
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
public class TokenContributionChartPanel extends JPanel {

	private static final long serialVersionUID = -5382293105043214105L;

	private ChartPanel chartPanel;
	
	public TokenContributionChartPanel() {
		setLayout(new BorderLayout());
	}
	
    public void initialize(final ClientDataModel clientDataModel) {
    	JFreeChart chart = createChart(clientDataModel);
    	if (chartPanel != null) {
    		remove(chartPanel);
    	}
    	chartPanel = new ChartPanel(chart);
    	add(chartPanel, BorderLayout.CENTER);
    	revalidate();
    }
    
    private JFreeChart createChart(ClientDataModel clientDataModel) {
    	boolean restrictedVisibility = clientDataModel.getRoundConfiguration().isRestrictedVisibility();
    	if (restrictedVisibility) {
    		return createBarChart(clientDataModel);
    	}
    	else {
    		return createPieChart(clientDataModel);
    	}
    }

    private CategoryDataset createCategoryDataset(ClientDataModel clientDataModel) {
    	DefaultCategoryDataset dataset = new DefaultCategoryDataset();
    	List<ClientData> neighbors = clientDataModel.getOrderedVisibleClients();
    	for (ClientData neighbor: neighbors) {
    		dataset.addValue(neighbor.getInvestedTokens(), "Tokens Invested", neighbor.getPriorityString());
    	}
    	return dataset;
	}

	/**
     * Creates and returns a pie dataset from the token contributions for the entire group. 
     * @return a pie dataset
     */
    private PieDataset createPieDataset(ClientDataModel clientDataModel) {
        final DefaultPieDataset defaultPieDataset = new DefaultPieDataset();
        GroupDataModel groupDataModel = clientDataModel.getGroupDataModel();
        Map<Identifier,ClientData> clientDataMap = groupDataModel.getClientDataMap();
        for (ClientData clientData : clientDataMap.values()) {
            StringBuilder labelBuilder = new StringBuilder();
            labelBuilder.append(clientData.getPriorityString());
            if (clientData.getId().equals(clientDataModel.getId())) {
                labelBuilder.append(" (You)");
            }
            labelBuilder.append(" invested ").append(clientData.getInvestedTokens()).append(" token(s)");
            defaultPieDataset.setValue(labelBuilder.toString(), clientData.getInvestedTokens());
        }
        return defaultPieDataset;        
    }
    
    private JFreeChart createBarChart(ClientDataModel clientDataModel) {
        final CategoryDataset dataset = createCategoryDataset(clientDataModel);
    	JFreeChart chart = ChartFactory.createBarChart("Tokens Contributed by your Neighbors", "Participant", "Tokens Invested", dataset, 
    			PlotOrientation.VERTICAL, false, false, false);
    	CategoryPlot plot = chart.getCategoryPlot();
    	ValueAxis rangeAxis = plot.getRangeAxis();
    	rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
    	rangeAxis.setUpperBound(clientDataModel.getRoundConfiguration().getTokenEndowment());
    	plot.getRenderer().setSeriesPaint(0, Color.BLUE);
    	return chart;
    }
    
    private JFreeChart createPieChart(ClientDataModel clientDataModel) {
        final PieDataset dataset = createPieDataset(clientDataModel);
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
