/**
 * 
 */
package edu.asu.commons.irrigation.client;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JPanel;

import edu.asu.commons.irrigation.server.ClientData;

/**
 * @author Sanket
 *
 */
public class ScoreBoxPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4724679308719540371L;
	//creating a Linked Hash Map for the activity Panel Screens
	//Map<Integer, ActivitySummaryPanel>activitySummaryPanelMap = new LinkedHashMap<Integer, ActivitySummaryPanel>();
	Map<Integer, ActivitySummaryPanelNew>activitySummaryPanelMap = new LinkedHashMap<Integer, ActivitySummaryPanelNew>();

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
		
		//for number of clients in the group filling in the activvity Summary Panel
		//FIXME : Add activity Summary Panel if Panels per group are needed in the scoreboard section
		//else add ActivityPanelNew which determines activity panel per parameter
		/*for(int i=0;i<client.getRoundConfiguration().getClientsPerGroup();i++){
			//dont get the activity summary panels for self client ids.
			if(client.getClientGameState().getPriority() != i){
				ActivitySummaryPanel activitySummaryPanel = new ActivitySummaryPanel(i);
				activitySummaryPanelMap.put(new Integer(i), activitySummaryPanel);
				this.add(activitySummaryPanel);
			}
		}*/
		
		//add the new activity panel that gives information per Parameter
		for(int i=0;i<NUMBER_PARAMETERS;i++){
			//get the activity panels per parameter
			ActivitySummaryPanelNew activitySummaryPanelNew = new ActivitySummaryPanelNew(i,client);
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

	public void endRound() {
		// TODO Auto-generated method stub
		/*for(int i=0;i<client.getRoundConfiguration().getClientsPerGroup();i++){
			if(activitySummaryPanelMap.get(new Integer(i))!= null)
			activitySummaryPanelMap.get(new Integer(i)).endRound();
		}*/
		
		for(int i=0;i<NUMBER_PARAMETERS;i++){
			activitySummaryPanelMap.get(new Integer(i)).endRound();
		}
	}
}
