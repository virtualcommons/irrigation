package edu.asu.commons.irrigation.events;

import edu.asu.commons.event.AbstractPersistableEvent;
import edu.asu.commons.net.Identifier;



public class FileDownloadedEvent extends AbstractPersistableEvent {

    private static final long serialVersionUID = 1061602349717830635L;
    
    private final String fileNumber;
    
    public FileDownloadedEvent(Identifier id, String fileNumber) {
        super(id);
        this.fileNumber = fileNumber;
    }

    public String getFileNumber() {
        return fileNumber;
    }

}
