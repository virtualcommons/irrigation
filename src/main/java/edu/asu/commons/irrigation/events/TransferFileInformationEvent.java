/**
 * 
 */
package edu.asu.commons.irrigation.events;


import edu.asu.commons.event.AbstractPersistableEvent;
import edu.asu.commons.net.Identifier;

/**
* @author Sanket
*
*/
public abstract class TransferFileInformationEvent extends AbstractPersistableEvent {

    private static final long serialVersionUID = -5241129364032790383L;
    
    private String fileNumber;
	
	public TransferFileInformationEvent(Identifier id) {
		super(id);
	}
	
	public void setFileNumber(String fileNumber) {
		this.fileNumber = fileNumber;
	}
	
	public String getFileNumber(){
		return fileNumber;
	}
	
}
