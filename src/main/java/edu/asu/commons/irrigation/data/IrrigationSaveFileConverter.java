package edu.asu.commons.irrigation.data;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import edu.asu.commons.event.ChatRequest;
import edu.asu.commons.event.PersistableEvent;
import edu.asu.commons.experiment.Persister;
import edu.asu.commons.experiment.SaveFileProcessor;
import edu.asu.commons.experiment.SavedRoundData;
import edu.asu.commons.irrigation.conf.RoundConfiguration;
import edu.asu.commons.irrigation.server.ClientData;
import edu.asu.commons.irrigation.server.GroupDataModel;
import edu.asu.commons.irrigation.server.ServerDataModel;
import edu.asu.commons.net.Identifier;

/**
 * $Id$
 * 
 * 
 * @author <a href='mailto:Allen.Lee@asu.edu'>Allen Lee</a>
 * @version $Rev$
 */
public class IrrigationSaveFileConverter {
    
    public static boolean convert(String saveDataDirectory) {
        File allSaveFilesDirectory = new File(saveDataDirectory);
        if (allSaveFilesDirectory.exists() && allSaveFilesDirectory.isDirectory()) {
            List<SaveFileProcessor> processors = new ArrayList<SaveFileProcessor>();
            processors.addAll(Arrays.asList(
                    new ClientTokensProcessor(),
                    new AllDataProcessor(),
                    new SummaryProcessor()
            ));
            Persister.processSaveFiles(allSaveFilesDirectory, processors);
            return true;
        }
        return false;
    }
    
    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Usage: java " + IrrigationSaveFileConverter.class + " <save-data-directory>");
            System.exit(0);
        }
        if (convert(args[0])) {
            System.err.println("Successfully converted files in " + args[0]);
        }
        else {
            System.err.println(args[0] + " doesn't appear to be a valid save file directory.");
        }
    }
    
    private static class AllDataProcessor extends SaveFileProcessor.Base {

        @Override
        public void process(SavedRoundData savedRoundData, PrintWriter writer) {
            writer.println("Time, Group #, Identifier, Event");
            ServerDataModel serverDataModel = (ServerDataModel) savedRoundData.getDataModel();
            ArrayList<GroupDataModel> groups = new ArrayList<GroupDataModel>(serverDataModel.getAllGroupDataModels());
            for (PersistableEvent persistableEvent: savedRoundData.getActions()) {
                long elapsedTime = savedRoundData.getElapsedTimeInSeconds(persistableEvent);
                Identifier id = persistableEvent.getId();
                GroupDataModel group = serverDataModel.getGroupDataModel(id);
                String groupNumber = (group == null) ? "System Event" : String.valueOf(groups.indexOf(group));
                writer.println(String.format("%d, %s, %s, %s", elapsedTime, groupNumber, id, persistableEvent));
            }
        }

        @Override
        public String getOutputFileExtension() {
            return "-all-data.txt";
        }
        
    }
    
    private static class SummaryProcessor extends SaveFileProcessor.Base {

        @Override
        public void process(SavedRoundData savedRoundData, PrintWriter writer) {
            ServerDataModel serverDataModel = (ServerDataModel) savedRoundData.getDataModel();
            RoundConfiguration roundConfiguration = (RoundConfiguration) savedRoundData.getRoundParameters();
            ArrayList<GroupDataModel> groups = new ArrayList<GroupDataModel>(serverDataModel.getAllGroupDataModels());
            writer.println("Group, Total group tokens earned, Water availability, Water supply, Tokens invested, Infrastructure before investment, Total infrastructure efficiency, % infrastructure decline");
            for (GroupDataModel group: groups) {
                int totalGroupTokensEarned = 0;
                int totalGroupTokenInvestment = 0;
                for (ClientData data : group.getClientDataMap().values()) {
                    totalGroupTokensEarned += data.getAllTokensEarnedThisRound();
                    totalGroupTokenInvestment += data.getInvestedTokens();
                }
                writer.println(String.format("%s, %d, %d, %d, %d, %d, %d, %d", 
                        groups.indexOf(group), 
                        totalGroupTokensEarned, 
                        group.getIrrigationCapacity(), 
                        roundConfiguration.getWaterSupplyCapacity(),
                        totalGroupTokenInvestment,
                        group.getInfrastructureEfficiencyBeforeInvestment(),
                        group.getInfrastructureEfficiency(),
                        roundConfiguration.getInfrastructureDegradationFactor()
                        ));
            }
            Map<GroupDataModel, SortedSet<ChatRequest>> chatRequestMap = new HashMap<GroupDataModel, SortedSet<ChatRequest>>();
            SortedSet<ChatRequest> allChatRequests = savedRoundData.getChatRequests();
            if (! allChatRequests.isEmpty()) {
                ChatRequest first = allChatRequests.first();
                for (ChatRequest request: savedRoundData.getChatRequests()) {
                    GroupDataModel group = serverDataModel.getGroupDataModel(request.getSource());
                    if (chatRequestMap.containsKey(group)) {
                        chatRequestMap.get(group).add(request);
                    }
                    else {
                        TreeSet<ChatRequest> chatRequests = new TreeSet<ChatRequest>();
                        chatRequests.add(request);
                        chatRequestMap.put(group, chatRequests);
                    }
                }
                // FIXME: hack to deal with rolling chat logs
                // set last creation time to the last chat request that occurred so in the next chat round
                // we'll know if we repeated a chat request.
                //                ChatRequest last = allChatRequests.last();
                //                lastCreationTime = last.getCreationTime();
                //                System.err.println("last creation time: " + lastCreationTime + " - " + last);
                for (GroupDataModel group: groups) {
                    SortedSet<ChatRequest> chatRequests = chatRequestMap.get(group);
                    if (chatRequests != null) {
                        writer.println("Group #" + groups.indexOf(group) + " chats");
                        for (ChatRequest request: chatRequests) {
                            writer.println(
                                    String.format("%s: %s (%s s)", 
                                            request.getSource(), 
                                            request.toString(), 
                                            TimeUnit.SECONDS.convert(request.getCreationTime() - first.getCreationTime(), TimeUnit.NANOSECONDS)));
                        }
                    }
                }
            }
        }

        @Override
        public String getOutputFileExtension() {
            return "-summary.txt";
        }
        
    }
    
    private static class ClientTokensProcessor extends SaveFileProcessor.Base {

        public void process(SavedRoundData savedRoundData, PrintWriter writer) {
            ServerDataModel serverDataModel = (ServerDataModel) savedRoundData.getDataModel();
            ArrayList<GroupDataModel> groups = new ArrayList<GroupDataModel>(serverDataModel.getAllGroupDataModels());
            writer.println("Group #, Identifier, Position, Tokens Earned From Water Collected, Uninvested Tokens");
            for (GroupDataModel group: groups) {
                for (ClientData data : group.getClientDataMap().values()) {
                    writer.println(String.format("%s, %s, %s, %s, %s", 
                            groups.indexOf(group), data.getId(), data.getPriorityString(), data.getTokensEarnedFromWaterCollected(), data.getUninvestedTokens()));
                }
            }

            
        }

        @Override
        public String getOutputFileExtension() {
            return "-tokens.txt";
        }
        
    }

}
