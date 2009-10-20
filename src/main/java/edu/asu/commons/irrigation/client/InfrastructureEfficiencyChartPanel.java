package edu.asu.commons.irrigation.client;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import edu.asu.commons.irrigation.server.GroupDataModel;

/**
 * $Id$
 * 
 * Presents a chart view of the infrastructure efficiency function. 
 * 
 * @author Allen Lee, Sanket Joshi
 * @version $Rev$
 */
public class InfrastructureEfficiencyChartPanel extends JPanel {

    private static final long serialVersionUID = 5555080117985336199L;

    private IrrigationClient client;
    
    private ChartPanel chartPanel;
    
    public InfrastructureEfficiencyChartPanel(IrrigationClient client) {
        this.client = client;
    	setPreferredSize(new Dimension(530/2, 326/2));
        //this.setSize(screenSize.width, screenSize.height);
        setName("infrastructure efficiency chart panel");
        setLayout(new BorderLayout());
    }

    public void initialize() {
    	if (chartPanel != null) {
    		remove(chartPanel);
    	}
    	chartPanel = createChartPanel();
    	add(chartPanel, BorderLayout.CENTER);
    }

    private ChartPanel createChartPanel() {
        final XYSeries actualFlowCapacitySeries = new XYSeries("Actual");
        final XYSeries potentialFlowCapacitySeries = new XYSeries("Potential");
        //				final XYSeries actualFlowCapacitySeriesY = new XYSeries("Actual");
        final XYSeries initialInfrastructureEfficiencySeries = new XYSeries("Initial");
        int x,y;
        GroupDataModel group = client.getClientDataModel().getGroupDataModel();
        final int infrastructureEfficiency = group.getInfrastructureEfficiency();
        final int actualFlowCapacity = group.getFlowCapacity();
        for (y = 0; y <= actualFlowCapacity; y++) {
            actualFlowCapacitySeries.add(infrastructureEfficiency, y);
        }
        for(x =0; x<=client.getRoundConfiguration().getMaximumInfrastructureEfficiency();x++){
            y =	group.calculateFlowCapacity(x);
            potentialFlowCapacitySeries.add(x,y);
        }
        final int initialInfrastructureEfficiency = group.getInitialInfrastructureEfficiency();
        final int initialFlowCapacity = group.calculateFlowCapacity(initialInfrastructureEfficiency);
        for (y = 0; y <= initialFlowCapacity; y++) {
            initialInfrastructureEfficiencySeries.add(initialInfrastructureEfficiency, y);
        }

        final XYSeriesCollection data = new XYSeriesCollection();
        data.addSeries(initialInfrastructureEfficiencySeries);
        data.addSeries(actualFlowCapacitySeries);
        data.addSeries(potentialFlowCapacitySeries);

        //	            data.addSeries(actualFlowCapacitySeriesY);

        final JFreeChart chart = ChartFactory.createXYLineChart(
                "Total Flow Capacity",
                "Infrastructure Efficiency",
                "Actual Flow Capacity",
                data,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(500, 270));
        return chartPanel;
    }
}