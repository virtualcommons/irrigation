/**
 * 
 */
package edu.asu.commons.irrigation.events;

import edu.asu.commons.event.AbstractEvent;
import edu.asu.commons.net.Identifier;

/**
 * @author Sanket
 *
 */
public class QuizCompletedEvent extends AbstractEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7081410122722056083L;
	
	private int instructionNumber = 0;
	
	public QuizCompletedEvent(Identifier id,int instructionNumber){
		super(id);
		this.instructionNumber = instructionNumber;
	}
	
	public int getInstructionNumber(){
		return instructionNumber;
	}

}
