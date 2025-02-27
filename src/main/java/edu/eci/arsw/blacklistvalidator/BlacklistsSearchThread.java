package edu.eci.arsw.blacklistvalidator;

import java.util.List;

import edu.eci.arsw.spamkeywordsdatasource.HostBlacklistsDataSourceFacade;

public class BlacklistsSearchThread extends Thread {
    private HostBlacklistsDataSourceFacade skds;
    private String ipaddress;
    private int startIndex, endIndex;
    private List<Integer> blackListOcurrences;
    private SharedCounter sharedCounter;

    public BlacklistsSearchThread (String ipaddress, HostBlacklistsDataSourceFacade skds, int startIndex, int endIndex, List<Integer> blackListOcurrences, SharedCounter sharedCounter){
        this.ipaddress = ipaddress;
        this.skds = skds;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.blackListOcurrences = blackListOcurrences;
        this.sharedCounter = sharedCounter;
    }
    
    @Override
    public void run(){
        for (int i = startIndex; i < endIndex && SharedCounter.get() < HostBlackListsValidator.BLACK_LIST_ALARM_COUNT; i++){
            if(skds.isInBlackListServer(i, ipaddress)) {
                synchronized (blackListOcurrences){
                    blackListOcurrences.add(i);
                }
                sharedCounter.increment();
            }

        }
    }
}
