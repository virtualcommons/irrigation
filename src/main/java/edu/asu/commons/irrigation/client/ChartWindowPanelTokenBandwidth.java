
/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2004, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc.
 * in the United States and other countries.]
 *
 * -----------------
 * XYSeriesDemo.java
 * -----------------
 * (C) Copyright 2002-2004, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id$
 *
 * Changes
 * -------
 * 08-Apr-2002 : Version 1 (DG);
 * 11-Jun-2002 : Inserted value out of order to see that it works (DG);
 * 11-Oct-2002 : Fixed issues reported by Checkstyle (DG);
 *
 */

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
 * A simple demo showing a dataset created using the {@link XYSeriesCollection} class.
 *
 */
//public class XYSeriesDemo extends ApplicationFrame {

	public class ChartWindowPanelTokenBandwidth extends JPanel {

	/**
	 *
	 */
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

    	//super(title);
    	this.client = client;
    	initialize();
    }



		public void initialize() {
		// TODO Auto-generated method stub
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
	            repaint();

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
	            chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
	            //setContentPane(chartPanel);
	            return chartPanel;
	            	//y=4*x;
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
     * Starting point for the demonstration application.
     *
     * @param args  ignored.
     */

		}