import java.io.Serializable;

@SuppressWarnings("serial")
public class Message implements Serializable{
}
@SuppressWarnings("serial")
// Request Message to request critical session
class RequestMsg extends Message implements Serializable{
	String msg;
	int srcNodeID;
	int dstNodeID;	
	int timestamp;

	public RequestMsg(Node node, int dstNodeID){
		synchronized(node){
			this.msg = "Request";
			this.srcNodeID = node.nodeID;
			this.dstNodeID = dstNodeID;
			this.timestamp = node.timestamp;
		}
	}
}
@SuppressWarnings("serial")
// Reply Message
class ReplyMsg extends Message implements Serializable{
	String msg;
	int srcNodeID;
	int dstNodeID;
	int timestamp;

	public ReplyMsg(Node node, int dstNodeID){
		synchronized(node){
			this.msg = "Reply";
			this.srcNodeID = node.nodeID;
			this.dstNodeID = dstNodeID;
			this.timestamp = node.timestamp;
		}
	}
}
@SuppressWarnings("serial")
// Release Message
class ReleaseMsg extends Message implements Serializable{
	String msg;
	int  srcNodeID;
	int dstNodeID;
	int timestamp;

	public ReleaseMsg(Node node, int dstNodeID){
		synchronized(node){
			this.msg = "Release";
			this.srcNodeID = node.nodeID;
			this.dstNodeID = dstNodeID;
			this.timestamp = node.timestamp;
		}
	}
}
