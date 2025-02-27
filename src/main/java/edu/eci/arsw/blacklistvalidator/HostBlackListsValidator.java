/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.blacklistvalidator;

import edu.eci.arsw.spamkeywordsdatasource.HostBlacklistsDataSourceFacade;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author hcadavid
 */
public class HostBlackListsValidator {

    static final int BLACK_LIST_ALARM_COUNT=5;
    
    /**
     * Check the given host's IP address in all the available black lists,
     * and report it as NOT Trustworthy when such IP was reported in at least
     * BLACK_LIST_ALARM_COUNT lists, or as Trustworthy in any other case.
     * The search is not exhaustive: When the number of occurrences is equal to
     * BLACK_LIST_ALARM_COUNT, the search is finished, the host reported as
     * NOT Trustworthy, and the list of the five blacklists returned.
     * @param ipaddress suspicious host's IP address.
     * @return  Blacklists numbers where the given host's IP address was found.
     */
    public List<Integer> checkHost(String ipaddress, int numThreads){
        
        LinkedList<Integer> blackListOcurrences=new LinkedList<>();
        HostBlacklistsDataSourceFacade skds=HostBlacklistsDataSourceFacade.getInstance();
        int totalServers = skds.getRegisteredServersCount();
        int range = totalServers / numThreads;
        SharedCounter sharedCounter = new SharedCounter();
        List<BlacklistsSearchThread> threads = new LinkedList<>();
        
        for (int i = 0; i < numThreads; i++){
            int startIndex = i * range;
            int endIndex = (i == numThreads - 1) ? totalServers : startIndex * range;
            BlacklistsSearchThread thread = new BlacklistsSearchThread(ipaddress, skds, startIndex, endIndex, blackListOcurrences, sharedCounter);
            threads.add(thread);
            thread.start();
        }

        for (BlacklistsSearchThread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e){
                Thread.currentThread().interrupt();
            }
        }

        if (blackListOcurrences.size() >= BLACK_LIST_ALARM_COUNT) {
            System.out.println("Info: host " + ipaddress + "Reported as NOT trustworthy"); 
        } else {
            System.out.println("Info: host " + ipaddress + "Reported as trustworthy");
        }

        return blackListOcurrences;
    }
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(HostBlackListsValidator.class.getName());
    
}
