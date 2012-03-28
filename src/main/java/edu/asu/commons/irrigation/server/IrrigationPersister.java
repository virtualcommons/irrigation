package edu.asu.commons.irrigation.server;

import edu.asu.commons.event.EventChannel;
import edu.asu.commons.experiment.Persister;
import edu.asu.commons.irrigation.conf.RoundConfiguration;
import edu.asu.commons.irrigation.conf.ServerConfiguration;

/**
 * $Id$
 * 
 * Provides persistence of irrigation experiment actions.
 * 
 * @author <a href='mailto:Allen.Lee@asu.edu'>Allen Lee</a>
 * @version $Rev$
 */
public class IrrigationPersister extends Persister<ServerConfiguration, RoundConfiguration> {

    private final static String FAIL_SAFE_SAVE_DIRECTORY = "/tmp/failsafe/irrigation-data";

    public IrrigationPersister(EventChannel channel, ServerConfiguration configuration) {
        super(channel, configuration);
    }

    @Override
    protected String getFailSafeSaveDirectory() {
        return FAIL_SAFE_SAVE_DIRECTORY;
    }

}
