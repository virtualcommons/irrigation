package edu.asu.commons.irrigation.client;

import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.asu.commons.irrigation.model.ClientData;
import edu.asu.commons.irrigation.model.GroupDataModel;

/**
 * $Id$
 *  
 * Panel displaying data for all participants.
 *
 * @author <a href='mailto:Allen.Lee@asu.edu'>Allen Lee</a>
 * @version $Rev$
 */
public class ScoreBoxPanel extends JPanel {

	private static final long serialVersionUID = 4724679308719540371L;
	
	private List<JLabel> availableWaterLabels = new ArrayList<JLabel>();
	
	private List<JLabel> waterUsedLabels = new ArrayList<JLabel>();
	
	private void clear() {
		availableWaterLabels.clear();
		waterUsedLabels.clear();
		removeAll();
	}
	
	public void initialize(ClientDataModel clientDataModel) {
		clear();
		// number of clients + 1 for the labels
		GroupDataModel groupDataModel = clientDataModel.getGroupDataModel();
		int columns = groupDataModel.size() + 1;
		GridLayout gridLayout = new GridLayout(3, columns);
		setLayout(gridLayout);
		
		add(new JLabel("Position:"));
		List<ClientData> clientDataList = clientDataModel.getClientDataSortedByPriority();
		for (ClientData clientData : clientDataList) {
			add(new JLabel(clientData.getPriorityString()));
		}
		// available water per second
		add(new JLabel("Available water per second:"));
		for (ClientData clientData: clientDataList) {
			JLabel availableWaterLabel = new JLabel("" + clientData.getAvailableFlowCapacity());
			availableWaterLabels.add(availableWaterLabel);
			add(availableWaterLabel);			
		}
		// water collected
		add(new JLabel("Water collected:"));
		for (ClientData clientData : clientDataList) {
			JLabel waterUsedLabel = new JLabel("" + clientData.getWaterCollected());
			waterUsedLabels.add(waterUsedLabel);
			add(waterUsedLabel);
		}
	}

	public void update(ClientDataModel clientDataModel) {
		List<ClientData> clientDataList = clientDataModel.getClientDataSortedByPriority();
		for (int index = 0; index < clientDataList.size(); index++) {
			ClientData clientData = clientDataList.get(index);
			JLabel availableWaterLabel = availableWaterLabels.get(index);
			availableWaterLabel.setText("" + clientData.getAvailableFlowCapacity());
			JLabel waterUsedLabel = waterUsedLabels.get(index);
			waterUsedLabel.setText("" + clientData.getWaterCollected());
		}

	}
}
