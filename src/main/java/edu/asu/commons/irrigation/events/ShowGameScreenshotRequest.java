package edu.asu.commons.irrigation.events;

import edu.asu.commons.event.AbstractEvent;
import edu.asu.commons.event.FacilitatorRequest;
import edu.asu.commons.net.Identifier;

public class ShowGameScreenshotRequest extends AbstractEvent implements FacilitatorRequest {

    private static final long serialVersionUID = -344715300306788401L;

    public ShowGameScreenshotRequest(Identifier id) {
        super(id);
    }
    
}
