
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


/**
 * A simple demo showing a dataset created using the {@link XYSeriesCollection} class.
 *
 */
public class ChartWindowPanel extends JPanel {

    private static final long serialVersionUID = 5555080117985336199L;

    private JPanel jPanel;

    /**
     * A demonstration application showing an XY series containing a null value.
     *
     * @param title  the frame title.
     */
    public ChartWindowPanel(int numberofFilesDownloaded) {
        initialize(numberofFilesDownloaded);
    }

    public void initialize(int numberofFilesDownloaded) {
        setLayout(new CardLayout());
        setSize(new Dimension(530/2, 326/2));
        //this.setSize(screenSize.width, screenSize.height);
        add(getJPanel(numberofFilesDownloaded), getJPanel(numberofFilesDownloaded).getName());
        repaint();

    }

    /**
     * This method initializes jPanel
     *
     * @return javax.swing.JPanel
     */
    private JPanel getJPanel(int numberofFilesDownloaded) {
        if (jPanel == null) {
            jPanel = new JPanel();
            GridLayout gridLayout = new GridLayout();
            gridLayout.setColumns(2);
            jPanel.setLayout(gridLayout);
            jPanel.add(getchartPanel(numberofFilesDownloaded));
            //jPanel.add(getchartPanel1());
            jPanel.setName("jPanel");
        }
        return jPanel;
    }

    private ChartPanel getchartPanel(int xUnit) {
        final XYSeries series = new XYSeries("Crops Grown Award Function");
        double x,y;
        for(x= 0;x<=xUnit;x++){
            y =	fileAwardFunction((int)x);
            series.add(x,y);
            repaint();
        }
        final XYSeriesCollection data = new XYSeriesCollection(series);
        final JFreeChart chart = ChartFactory.createXYLineChart(
                "Irrigation Infrastructure",
                "Crops Grown",
                "Tokens Earned",
                data,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
                );
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setLayout(new CardLayout());
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        return chartPanel;
    }

    /**
     * This method initializes jPanel
     *
     * @return javax.swing.JPanel
     */

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
    private int fileAwardFunction(final int infrastructureEfficiency) {
    	if (infrastructureEfficiency <= 45) {
    		return 0;
    	}
    	else if (infrastructureEfficiency <= 51) {
    		return 5;
    	}
    	else if (infrastructureEfficiency <= 55) {
    		return 10;
    	}
    	else if (infrastructureEfficiency <= 58) {
    		return 15;
    	}
    	else if (infrastructureEfficiency <= 61) {
    		return 20;
    	}
    	else if (infrastructureEfficiency <= 65) {
    		return 25;
    	}
    	else if (infrastructureEfficiency <= 70) {
    		return 30;
    	}
    	else if (infrastructureEfficiency <= 80) {
    		return 35;
    	}
    	else if (infrastructureEfficiency <= 100) {
    		return 40;
    	}
        return 40;
//        double B = 0;
//        double alpha,b,a,num,deno;
//        /**
//         * This would be taken by a proper configuration file
//         */
//        //alpha = getCurrentRoundConfiguration().getAlpha();
//        //a = getCurrentRoundConfiguration().getA();
//        //b = getCurrentRoundConfiguration().getB();
//
//        alpha = 20;
//        a = 5;
//        b = 6;
//
//        num = alpha * Math.pow((double)totalTokens,b);
//        deno = Math.pow(a,b)+Math.pow(totalTokens,b);
//
//        //num = alpha;
//        //deno = 1+java.lang.Math.exp(-b*(totalTokens - a));
//        B = num/deno;
//        //Bt = totalTokens;
//        return Math.round(B);
    }
}
