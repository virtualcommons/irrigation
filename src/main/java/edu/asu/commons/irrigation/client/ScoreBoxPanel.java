package edu.asu.commons.irrigation.client;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JPanel;

import edu.asu.commons.irrigation.server.ClientData;

/**
 * $Id$
 * 
 *
 * @author <a href='mailto:Allen.Lee@asu.edu'>Allen Lee</a>
 * @version $Rev$
 */
public class ScoreBoxPanel extends JPanel {

	private static final long serialVersionUID = 4724679308719540371L;
	//creating a Linked Hash Map for the activity Panel Screens
	//Map<Integer, ActivitySummaryPanel>activitySummaryPanelMap = new LinkedHashMap<Integer, ActivitySummaryPanel>();
	Map<Integer, SummaryPanel>activitySummaryPanelMap = new LinkedHashMap<Integer, SummaryPanel>();

	IrrigationClient client;
	
	int NUMBER_PARAMETERS = 3;
	
	public ScoreBoxPanel(IrrigationClient client){
		super();
		this.client = client;
		initialize();
	}

	private void initialize() {
		// TODO Auto-generated method stub
		GridLayout gridLayout = new GridLayout();
		gridLayout.setColumns(2);
		gridLayout.setRows(2);
		this.setSize(new Dimension(530,326));
		this.setLayout(gridLayout);

		//add the new activity panel that gives information per Parameter
		for(int i=0;i<NUMBER_PARAMETERS;i++){
			//get the activity panels per parameter
			SummaryPanel activitySummaryPanelNew = new SummaryPanel(i,client);
			activitySummaryPanelMap.put(new Integer(i), activitySummaryPanelNew);
			this.add(activitySummaryPanelNew);
		}
	}

	public void update(ClientData clientData){
		//update specific panel if activity Summary panel is being used, else update all the panels.
		//activitySummaryPanelMap.get(new Integer(clientData.getPriority())).update(clientData);
		for(int i=0;i<NUMBER_PARAMETERS;i++){
			activitySummaryPanelMap.get(new Integer(i)).update(clientData);
		}
	}
}
