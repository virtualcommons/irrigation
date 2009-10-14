package edu.asu.commons.irrigation.client;

import javax.swing.JPanel;

import edu.asu.commons.irrigation.server.ClientData;


/**
 * $Id$
 * 
 * 
 * @author <a href='mailto:Allen.Lee@asu.edu'>Allen Lee</a>
 * @version $Rev$
 */
public class SummaryPanel extends JPanel {

	private static final long serialVersionUID = 2393688647176774993L;

	private IrrigationClient client;
	
	
	public SummaryPanel(int parameterIndex, IrrigationClient client) {
		super();
		this.client = client;
	}

	

	public void update(ClientData clientData) {

	}

}
