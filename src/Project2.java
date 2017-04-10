import java.io.*;
import java.net.*;
import java.util.*;

public class Project2 {
	// Project General Settings
	public static int nodeID, NumOfNode, meanOfd, meanOfc, NumOfRequest;	
	public static String ConfigFile, OutputFile;
	public static int [][] connectTable;
	public static boolean runMAP = true;
	public static ArrayList<int[]> output = new ArrayList<int[]>(); 
	public static long responseTime;
	public static long responseAvg=0;
	public static long timeExpense = System.currentTimeMillis();// calculate time complexity

	public static void main(String args[]) throws IOException{
		Project2.ConfigFile = args[1];                          // Get config file path from args[1]
		ConfigReader R = new ConfigReader(Project2.ConfigFile); // Create Reader Object
		Project2.OutputFile = Project2.ConfigFile.substring(0, Project2.ConfigFile.lastIndexOf('.'));
		nodeID = Integer.parseInt(args[0]);                     // args[0] is nodeID
		Node node = new Node (nodeID);                          // Create a Node	
		Project2.NumOfNode = R.getGeneralInfo()[0];				// Get Number of node
		Project2.meanOfd = R.getGeneralInfo()[1];				// Get mean of inter-request delay
		Project2.meanOfc = R.getGeneralInfo()[2];               // Get mean of cs-execution time
		Project2.NumOfRequest = R.getGeneralInfo()[3];          // Get number of request
		int request = Project2.NumOfRequest;                    // count down the number of request
		

		node.socketListen();                                      // Turn on Server Socket
		// TODO find a better way to ensure all nodes has 
		// constructed socket, but set sleep time brutally
		try{Thread.sleep(5000);} catch(InterruptedException e){}; // Sleep 5 sec wait all socket turn on
		node.buildChannel();                                      // Build channel with every other node
		try{Thread.sleep(5000);} catch(InterruptedException e){}; // Sleep 5 sec Wait all node build channel

		// CS Request and program end detection
		while(true){
			synchronized(node){
				// CS Request
				if(request > 0 && node.CSRequest == false){
					responseTime = System.currentTimeMillis();    // calculate response time
					LamportMutEx.csEnter(node);                   // CS request
					try{Thread.sleep((int)RV.exp_rv((double)1/meanOfd));} catch(InterruptedException e){}; 
					request--;
				}
				// Detect end of program (check other nodes and itself finish CS)
				else if(LamportMutEx.releaseMsgGet.get() == ((NumOfNode-1) * NumOfRequest) && 
						node.CSRequest == false && !(request > 0)){
					timeExpense = System.currentTimeMillis() - timeExpense;  // calculate time complexity
					new OutputWriter(node,'S').writeToFile();                // write statistic (msg, time) log to file
					System.out.println(node.nodeID+" exit program");
					System.exit(0);
				}
			}
		}
	}
}
