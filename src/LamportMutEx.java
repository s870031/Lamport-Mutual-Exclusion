import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.concurrent.atomic.AtomicInteger; 
public class LamportMutEx{
	static Comparator<RequestMsg> comparator = new RequestComparator();
	static PriorityQueue<RequestMsg> requestMsgQueue = new PriorityQueue<RequestMsg>(10, comparator); // Priority Queue storing request message
	static AtomicInteger replyMsgGet = new AtomicInteger(0); // Count the number of reply message get
	static AtomicInteger releaseMsgGet = new AtomicInteger(0);                            // Count the number of release message get
	static AtomicInteger messageSent = new AtomicInteger(0); // Count the message complexity

	// Request permission for critical section
	public static void csEnter(Node node){
		synchronized(node){
		replyMsgGet.set(0);
		broadcastRequestMsg(node);      // broadcast request message
		RequestMsg message = new RequestMsg(node, 0);
		requestMsgQueue.add(message);   // insert into priority queue
		node.CSRequest = true;
		}
	}
	// Inform the service that finish critical section
	public static void csLeave(Node node){
		synchronized(node){
		requestMsgQueue.remove();      // Remove request message from queue
		broadcastReleaseMsg(node);     // Broadcast release message
		replyMsgGet.set(0);
		node.CSRequest = false;
		}
	}
	// Get request message
	public static void getRequestMsg(Node node, RequestMsg message){
		synchronized(node){
		node.timestamp = Math.max(node.timestamp, message.timestamp)+1; // update timestamp
		//System.out.println(node.nodeID + "(" + node.timestamp + ")" + " Get request message from " + message.srcNodeID);
		requestMsgQueue.add(message);           // insert into priority queue
		sendReplyMsg(node, message.srcNodeID);  // send Reply
		}
	}
	// Get reply message
	public static void getReplyMsg(Node node, ReplyMsg message){
		synchronized(node){
		node.timestamp = Math.max(node.timestamp, message.timestamp)+1; // update timestamp
		//System.out.println(node.nodeID + "(" + node.timestamp + ")" + " Get reply message from " + message.srcNodeID);
		replyMsgGet.incrementAndGet();
		checkCSEntry(node);             // check if enter critical session
		}
	}
	// Get release message
	public static void getReleaseMsg(Node node, ReleaseMsg message){
		synchronized(node){
		node.timestamp = Math.max(node.timestamp, message.timestamp)+1; // update timestamp
		//System.out.println(node.nodeID + "(" + node.timestamp + ")" + " Get release message from " + message.srcNodeID);
		requestMsgQueue.remove();       // Remove request message from queue
		checkCSEntry(node);             // check if enter critical session
		releaseMsgGet.incrementAndGet();
		}
	}
	// Check critical session entry
	// if it's ok to critical session, start critical session
	public static void checkCSEntry(Node node){
		synchronized(node){
		if(requestMsgQueue.size() != 0){
			//System.out.println("   replyMsg: " + replyMsgGet.get());
			RequestMsg topMessage = requestMsgQueue.peek();
			//System.out.println("   QueueTop: " + topMessage.srcNodeID);  // TODO DELETE
			// If it's ok to enter critical session, start critical session	
			if(replyMsgGet.get() == (Project2.NumOfNode-1) &&
					topMessage.srcNodeID == node.nodeID){
				System.out.println( "==== "+node.nodeID + " Enter critical session ====");
				// Enter critical session
				new OutputWriter(node,'E').writeToFile();
				try{Thread.sleep((int)RV.exp_rv((double)1/Project2.meanOfc));} catch(InterruptedException e){};  
				// Leave critical session
				Project2.responseTime = System.currentTimeMillis() - Project2.responseTime; // calculate response time
				Project2.responseAvg += Project2.responseTime;
				new OutputWriter(node,'L').writeToFile();
				csLeave(node); 
			}
		}
		}
	}
	// Broadcast Request message
	public static void broadcastRequestMsg(Node node){
		RequestMsg message;
		synchronized(node){
			node.timestamp++; // update timestamp	
			for(int id=0; id<Project2.NumOfNode; id++){
				try{
					// If the node id is not it self, broadcast
					if(id != node.nodeID){
						//System.out.println(node.nodeID + "(" + node.timestamp + ")" + " send request message to " + id);
						message = new RequestMsg(node,id);             // create Request message	
						ObjectOutputStream oos = node.oStream.get(id); // get outputstream
						oos.writeObject(message);                      // send message
						oos.flush();
						messageSent.incrementAndGet();                 // count message complexity
					}
				}catch(IOException ex) {ex.printStackTrace();}
			}
		}
	}
	// Send reply message
	public static void sendReplyMsg(Node node, int dstID){
		ReplyMsg message;
		synchronized(node){
		try{
			node.timestamp++; //update timestamp
			//System.out.println(node.nodeID + "(" + node.timestamp + ")" + " send reply message to " + dstID);
			message = new ReplyMsg(node, dstID);
			ObjectOutputStream oos = node.oStream.get(dstID);
			oos.writeObject(message);
			oos.flush();
			messageSent.incrementAndGet();              // count message complexity
		}catch(IOException ex) {ex.printStackTrace();}
		}
	}
	// Broadcast Release message
	public static void broadcastReleaseMsg(Node node){
		ReleaseMsg message;
		synchronized(node){
			node.timestamp++; // update timestamp	
			for(int id=0; id<Project2.NumOfNode; id++){
				try{
					// If the node id is not it self, broadcast
					if(id != node.nodeID){
						//System.out.println(node.nodeID + "(" + node.timestamp + ")" + " send release message to " + id);
						message = new ReleaseMsg(node,id);             // create Request message	
						ObjectOutputStream oos = node.oStream.get(id); // get outputstream
						oos.writeObject(message);                      // send message
						oos.flush();
						messageSent.incrementAndGet();                 // count message complexity
					}
				}catch(IOException ex) {ex.printStackTrace();}
			}
		}
	}
}
// Class for writing file
class OutputWriter {
	Node node;
	char type; // type of event. "E": enter CS, "L" leave CS
			   // "S" statistic log
	// Construct
	public OutputWriter(Node node, char type) {
		this.node = node;
		this.type = type;
	}
	// Edit file
	public void writeToFile() {
		String fileName = Project2.OutputFile + "-" + node.nodeID + ".out";
			try {
				File file = new File(fileName);
				FileWriter fileWriter;
				// if file exist open the original file
				if(file.exists()){
					fileWriter = new FileWriter(file,true);
				}
				// else file not exist create a file
				else{
					fileWriter = new FileWriter(file);
				}
				BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
				// Edit the file
				if(type == 'E')
					bufferedWriter.write(node.nodeID + " " + node.timestamp + " Enter critical session\n");
				else if(type == 'L')
					bufferedWriter.write(node.nodeID + " " + node.timestamp + " Leave critical session\n");
				else if(type == 'S'){
					Project2.responseAvg = Project2.responseAvg / Project2.NumOfRequest;
					bufferedWriter.write(LamportMutEx.messageSent.get() + " " + Project2.responseAvg + " " + Project2.timeExpense);
				}
				// Close files.
				bufferedWriter.close();
			}
			catch(IOException ex) {
				System.out.println("Error writing to file '" + fileName + "'");
			}
	}
}
