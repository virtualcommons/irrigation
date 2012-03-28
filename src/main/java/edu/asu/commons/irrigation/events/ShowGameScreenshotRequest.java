package edu.asu.commons.irrigation.events;

import edu.asu.commons.event.AbstractEvent;
import edu.asu.commons.event.ShowRequest;
import edu.asu.commons.net.Identifier;

public class ShowGameScreenshotRequest extends AbstractEvent implements ShowRequest<ShowGameScreenshotRequest> {

    private static final long serialVersionUID = -344715300306788401L;

    public ShowGameScreenshotRequest(Identifier id) {
        super(id);
    }

    @Override
    public ShowGameScreenshotRequest clone(Identifier id) {
        return new ShowGameScreenshotRequest(id);
    }

}
