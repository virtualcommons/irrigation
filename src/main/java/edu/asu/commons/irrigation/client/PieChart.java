/**
 * 
 */
package edu.asu.commons.irrigation.client;

import java.awt.Font;
import java.awt.Rectangle;
import java.text.AttributedString;
import java.util.Map;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;

import edu.asu.commons.irrigation.server.ClientData;
import edu.asu.commons.irrigation.server.GroupDataModel;
import edu.asu.commons.net.Identifier;

/**
 * A pie chart with a custom label generator.
 */
public class PieChart extends JPanel {

    /**
	 * 
	 */
	private static final long serialVersionUID = -5382293105043214105L;

	/**
     * Creates a new demo instance.
     *
     * @param title  the frame title.
     */
	
	GroupDataModel groupDataModel;

	private IrrigationClient client;
	
    public PieChart(GroupDataModel groupDataModel,IrrigationClient client) {
        super();
        this.groupDataModel = groupDataModel;
        this.client = client;
        final PieDataset dataset = createDataset();
        final JFreeChart chart = createChart(dataset);
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        this.add(chartPanel);
        this.setBounds(new Rectangle(0,0,500,270));
        
    }

    /**
     * Creates a sample dataset.
     * 
     * @return a sample dataset.
     */
    private PieDataset createDataset() {
        final DefaultPieDataset dataset = new DefaultPieDataset();
        Map<Identifier,ClientData>clientDataMap = groupDataModel.getClientDataMap();
        for(ClientData clientData : clientDataMap.values()){
        	String clientPieLabel =null;
        	switch(clientData.getPriority()){
        	case 0 : clientPieLabel = "A = ";
        			 break;
        			 
        	case 1 : clientPieLabel = "B = ";
        			 break;
        			 
        	case 2 : clientPieLabel = "C = ";
        			 break;
        			 
        	case 3 : clientPieLabel = "D =  ";	
        			 break;
        			 
        	case 4 : clientPieLabel = "E = ";
        			 break;
        	}
        	if(client.getClientGameState().getPriority() == clientData.getPriority()){
        		clientPieLabel = "YOU = ";
        	}
        	clientPieLabel = clientPieLabel+new Integer(clientData.getContributedTokens()).toString();
        	dataset.setValue(clientPieLabel,new Double(clientData.getContributedTokens()));
        }
        /*dataset.setValue("One", new Double(43.2));
        dataset.setValue("Two", new Double(10.0));
        dataset.setValue("Three", new Double(27.5));
        dataset.setValue("Four", new Double(17.5));
        dataset.setValue("Five", new Double(11.0));
        dataset.setValue("Six", new Double(19.4));
        */
        return dataset;        
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
    
    /**
     * Starting point for the demonstration application.
     *
     * @param args  ignored.
     */
   
    
    /**
     * A custom label generator (returns null for one item as a test).
     */
    static class CustomLabelGenerator implements PieSectionLabelGenerator {
        
        /**
         * Generates a label for a pie section.
         * 
         * @param dataset  the dataset (<code>null</code> not permitted).
         * @param key  the section key (<code>null</code> not permitted).
         * 
         * @return the label (possibly <code>null</code>).
         */
        public String generateSectionLabel(final PieDataset dataset, final Comparable key) {
            String result = null;    
            if (dataset != null) {
                if (!key.equals("Two")) {
                    result = key.toString();   
                }
            }
            return result;
        }

		public AttributedString generateAttributedSectionLabel(PieDataset arg0, Comparable arg1) {
			// TODO Auto-generated method stub
			return null;
		}
   
    }

}
