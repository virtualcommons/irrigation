package edu.asu.commons.irrigation.client;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.GridLayout;

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
 *
 * @author Sanket Joshi
 * @version $Rev$
 */
public class ChartWindowPanelTokenBandwidth extends JPanel {

    private static final long serialVersionUID = 5555080117985336199L;

    private int numberofFilesDownloaded = 10;

    private Dimension screenSize;

    private JPanel jPanel;

    private IrrigationClient client;



    /**
     * A demonstration application showing an XY series containing a null value.
     *
     * @param title  the frame title.
     */
    public ChartWindowPanelTokenBandwidth(IrrigationClient client) {
        this.client = client;
        initialize();
    }



    public void initialize() {
        this.setLayout(new CardLayout());
        this.setSize(new Dimension(530/2, 326/2));
        //this.setSize(screenSize.width, screenSize.height);
        this.add(getJPanel(), getJPanel().getName());
        repaint();

    }

    /**
     * This method initializes jPanel
     *
     * @return javax.swing.JPanel
     */
    private JPanel getJPanel() {
        if (jPanel == null) {
            jPanel = new JPanel();
            GridLayout gridLayout = new GridLayout();
            gridLayout.setColumns(2);
            jPanel.setLayout(gridLayout);
            jPanel.add(getChartPanel());
            //jPanel.add(getchartPanel1());
            jPanel.setName("jPanel");
        }
        return jPanel;
    }

    private ChartPanel getChartPanel() {
        // TODO Auto-generated method stub
        final XYSeries actualFlowCapacitySeries = new XYSeries("Actual");
        final XYSeries potentialFlowCapacitySeries = new XYSeries("Potential");
        //				final XYSeries actualFlowCapacitySeriesY = new XYSeries("Actual");
        final XYSeries initialInfrastructureEfficiencySeries = new XYSeries("Initial");
        int x,y;
        GroupDataModel group = client.getClientDataModel().getGroupDataModel();
        final int infrastructureEfficiency = group.getInfrastructureEfficiency();
        final int actualFlowCapacity = group.getFlowCapacity();
        for(y = 0; y <= actualFlowCapacity; y++) {
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

        //	            for(y = 0; y <= actualFlowCapacity; y++) {
        //	            	actualFlowCapacitySeriesY.add(infrastructureEfficiency,y);
        //	            }

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
        //chartPanel.chartProgress(arg0)
        chartPanel.setLayout(new CardLayout());
        chartPanel.setPreferredSize(new Dimension(500, 270));
        repaint();
        //setContentPane(chartPanel);
        return chartPanel;
        //y=4*x;
    }
}