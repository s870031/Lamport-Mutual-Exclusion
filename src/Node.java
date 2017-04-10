import java.io.*;
import java.net.*;
import java.util.*;
public class Node
{
	public int nodeID;             // node ID
	public String hostName;        // node host name
	public int listenPort;         // node socket listen port
	public int timestamp;          // implement lamport's timestamp
	public boolean CSRequest;      // check if the node has request critical session
	ConfigReader R = new ConfigReader(Project2.ConfigFile);
	HashMap<Integer,Socket> channels = new HashMap<Integer,Socket>();
	HashMap<Integer,ObjectOutputStream> oStream = new HashMap<Integer,ObjectOutputStream>();

	// Constructor
	public Node(int nodeID){
		this.nodeID = nodeID;
		this.hostName = R.getNodeHostName(nodeID);
		this.listenPort = R.getNodeListenPort(nodeID);
		this.timestamp = 0;
		this.CSRequest = false;
	}

	// Socket Server listen
	public void socketListen(){				
		new ServerThread(this).start();	
	}

	// Build Channel with every node except for itself
	public void buildChannel() throws IOException, UnknownHostException{
		for(int i=0;i<Project2.NumOfNode;i++){
			// If the node if not it self, build channel 
			if(i != Project2.nodeID){
				String hostName = R.getNodeHostName(i);
				int port = R.getNodeListenPort(i);
				InetAddress address = InetAddress.getByName(hostName);
				// Create socket and save in hashmap
				Socket client = new Socket(address,port);
				channels.put(i, client);
				// Create output stream and save in hashmap
				ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
				oStream.put(i, oos);
			}	
		}
	}
	
	/*	
		public static void main (String args[])
		{
		Node n = new Node(1);
		System.out.println("NodeID: " + n.nodeID );
		System.out.println("status: " + n.status );
		System.out.println("port: " + n.listenPort);
		System.out.print("neighbor #: " + n.neighbor.length + ", ");
		for(int i=0; i<n.neighbor.length; i++)
		System.out.print(n.neighbor[i] + " ");
		System.out.print("\n");

		for(int i=0; i<n.vector.length; i++)
		System.out.print(n.vector[i] + " ");
		}
		*/ 
}

// Server listen thread
class ServerThread extends Thread{
	Node node;
	int port; 
	String hostName;  
	int nodeID; 

	public ServerThread(Node node){
		this.node = node;
		this.port = node.listenPort;
		this.hostName = node.hostName;
		this.nodeID  = node.nodeID;
	}	

	public void run(){
		try{
			ServerSocket serverSock = new ServerSocket(port);  // Create a server socket service at port
			System.out.println( hostName +"(" + nodeID + ")" + " server socket listening...");

			while(true){                                       //  Server starts infinite loop waiting for accept client
				Socket sock = serverSock.accept();             //    Wait for client connection
				new ClientThread(sock,node).start();           //    Start new thead to handle client connection
			}
		}catch (IOException ex) {ex.printStackTrace();}
	}
}

// Socket accept connection
//    create new thread for each connection
class ClientThread extends Thread{	
	Socket cSocket; // Client Socket
	Node node;

	public ClientThread(Socket cSocket, Node node){
		this.cSocket = cSocket;
		this.node = node;
	}

	public void run() {
		ObjectInputStream ois = null;
		try{
			ois = new ObjectInputStream(cSocket.getInputStream());
		}catch (IOException e){e.printStackTrace();}

		while(true){
		try{				
			Message message;
			message = (Message) ois.readObject();	
			synchronized(node){
				// Get request message
				if(message instanceof RequestMsg){
					LamportMutEx.getRequestMsg(node, (RequestMsg)message);
				}
				// Get reply message
				if(message instanceof ReplyMsg){
					LamportMutEx.getReplyMsg(node, (ReplyMsg)message);
				}
				// Get release message
				if(message instanceof ReleaseMsg){
					LamportMutEx.getReleaseMsg(node, (ReleaseMsg)message);
				}
			}
		}catch (IOException e) {e.printStackTrace();}
		 catch (ClassNotFoundException e){e.printStackTrace();}
		}
	}
}
