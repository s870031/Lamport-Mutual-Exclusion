// StringLengthComparator.java
//  compare timestamp(or id) to assign priority queue 
import java.util.Comparator;

public class RequestComparator implements Comparator<RequestMsg>{
	@Override
		public int compare(RequestMsg m1, RequestMsg m2){
			// if timestamp is smaller, prioritize 
			if (m1.timestamp < m2.timestamp){
				return -1;
			}
			if (m1.timestamp > m2.timestamp){
				return 1;
			}
			// if timestamp are the same, lower node id first
			if (m1.timestamp == m2.timestamp){
				if(m1.srcNodeID < m2.srcNodeID){
					return -1; 
				}
				else{
					return 1;
				}
			}
			return 0;
		}
}
