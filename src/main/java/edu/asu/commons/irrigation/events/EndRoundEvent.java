package edu.asu.commons.irrigation.events;

import edu.asu.commons.event.AbstractEvent;
import edu.asu.commons.event.ExperimentUpdateEvent;
import edu.asu.commons.irrigation.model.GroupDataModel;
import edu.asu.commons.net.Identifier;

/**
 * Conveys the GroupDataModel and a lastRound boolean flag to the relevant clients.
 */
public class EndRoundEvent extends AbstractEvent implements ExperimentUpdateEvent {

    private static final long serialVersionUID = 6165820512151646176L;

    private final boolean lastRound;

    private final GroupDataModel groupDataModel;

    public EndRoundEvent(Identifier id, GroupDataModel groupDataModel, boolean lastRound) {
        super(id);
        this.lastRound = lastRound;
        this.groupDataModel = groupDataModel;
    }

    public GroupDataModel getGroupDataModel() {
        return groupDataModel;
    }

    public boolean isLastRound() {
        return lastRound;
    }

}
