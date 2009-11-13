package edu.asu.commons.irrigation.events;

import edu.asu.commons.event.AbstractEvent;
import edu.asu.commons.event.FacilitatorRequest;
import edu.asu.commons.net.Identifier;

public class ShowTokenInvestmentScreenRequest extends AbstractEvent implements FacilitatorRequest {
    
    private static final long serialVersionUID = -4133284727930840712L;

    public ShowTokenInvestmentScreenRequest(Identifier id) {
        super(id);
    }
}
