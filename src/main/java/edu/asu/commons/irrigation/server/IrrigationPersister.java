package edu.asu.commons.irrigation.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TreeSet;

import edu.asu.commons.event.EventChannel;
import edu.asu.commons.event.EventTypeProcessor;
import edu.asu.commons.event.PersistableEvent;
import edu.asu.commons.irrigation.conf.RoundConfiguration;
import edu.asu.commons.irrigation.conf.ServerConfiguration;

public class IrrigationPersister {
    
    private final static String CONFIGURATION_SAVE_FILENAME = "server-configuration.save";
    private final static String FAIL_SAFE_SAVE_DIRECTORY = "/opt/failsafe/irrigation-data";
    private RoundConfiguration roundConfiguration;
    private String experimentSaveDirectory;
    
    private final TreeSet<PersistableEvent> actions;
    private String saveDirectory;

    public IrrigationPersister(EventChannel channel, ServerConfiguration configuration) {
        this.actions = new TreeSet<PersistableEvent>();
        this.saveDirectory = configuration.getPersistenceDirectory();
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy_HH.mm.ss");
        this.experimentSaveDirectory = sdf.format(new Date());
        saveConfiguration(configuration);
        channel.add(this, new EventTypeProcessor<PersistableEvent>(PersistableEvent.class) {
            public void handle(PersistableEvent event) {
                synchronized (actions) {
                    event.timestamp();
                    actions.add(event);
                }
            }
        });
    }

    public void clear() {
        synchronized (actions) {
            actions.clear();
        }
    }
    
    public void initialize(RoundConfiguration configuration) {
        clear();
        this.roundConfiguration = configuration;
    }
    
    public void persist(ServerDataModel serverDataModel) {
        try {
            saveRound(serverDataModel, saveDirectory);
        }
        catch (FileNotFoundException recoverable) {
            try {
                saveRound(serverDataModel, FAIL_SAFE_SAVE_DIRECTORY);
            }
            catch (IOException unrecoverable) {
                // now really give up
                unrecoverable.printStackTrace();
                throw new RuntimeException(
                        "Couldn't save to the failsafe directory", unrecoverable);
            }
            
        }
        catch (IOException unrecoverable) {
            unrecoverable.printStackTrace();
            throw new RuntimeException("Couldn't save this round");
        }
        
    }
    
    private void saveRound(ServerDataModel serverDataModel, String persistenceDirectory) throws IOException {
        String saveDestination = getSavePath(persistenceDirectory);
        ObjectOutputStream oos = 
            new ObjectOutputStream(new FileOutputStream(getRoundFilePath(saveDestination, roundConfiguration.getRoundNumber())));
        try {
            oos.writeObject(roundConfiguration);
            oos.writeObject(serverDataModel);
            synchronized (actions) {
                oos.writeObject(actions);
            }
        }
        finally {
            oos.close();
        }
    }
    
//    private void restoreRound() throws IOException, ClassNotFoundException {
//        ObjectInputStream stream = new ObjectInputStream(new FileInputStream("round-0.save"));
//        RoundConfiguration configuration = (RoundConfiguration) stream.readObject();
//        ServerDataModel dataModel = (ServerDataModel) stream.readObject();
//        TreeSet<PersistableEvent> actions = (TreeSet<PersistableEvent>) stream.readObject();
//        for (PersistableEvent event: actions) {
//            System.err.println("persistable event: " + event);
//        }
//        for (GroupDataModel group: dataModel.getAllGroupDataModels()) {
//            group.setServerDataModel(dataModel);
//        }
//    }
//    
    
    
    public static String getRoundFilePath(String directory, int roundNumber) {
        return String.format("%s%sround-%d.save", directory, File.separator, roundNumber);
    }
    
    private void saveConfiguration(ServerConfiguration serverConfiguration) {
        try {
            String savePath = getSavePath(saveDirectory);
            createDirectoryIfNeeded(savePath);
            String configurationSavePath = savePath + File.separator + CONFIGURATION_SAVE_FILENAME;
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(configurationSavePath));
            oos.writeObject(serverConfiguration);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void createDirectoryIfNeeded(String directoryName) {
        File file = new File(directoryName);
        // try to create the directory if it doesn't already exist.
        if (!file.isDirectory()) {
            if (!file.mkdir()) {
                file.mkdirs();
            }
        }
    }
    
    private String getSavePath(String persistenceDirectory) {
        return persistenceDirectory + File.separator + experimentSaveDirectory;
    }

}
