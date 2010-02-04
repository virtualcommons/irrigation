package edu.asu.commons.irrigation.events;

import edu.asu.commons.event.AbstractPersistableEvent;
import edu.asu.commons.net.Identifier;

/**
 * @author Sanket
 * This event transfers the tokens from client to server, and transfers the Bandwidth calculated to thte client side
 *
 */
public class InvestedTokensEvent extends AbstractPersistableEvent {

    private static final long serialVersionUID = -9128732807359173475L;
    
    private final int investedTokens;
    
	public InvestedTokensEvent(Identifier id, int submittedTokens) {
		super(id);
        this.investedTokens = submittedTokens;
	}

	public int getInvestedTokens(){
		return investedTokens;
	}
	
	public String toString() {
	    return String.format("%s invested %d tokens", id, investedTokens);
	}
	
}
