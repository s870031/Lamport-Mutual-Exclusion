import java.io.*;
import java.nio.charset.Charset;
public class ConfigReader
{
	public String ConfigFile; // Config file path

	// Construct	
	public ConfigReader(String ConfigFile){
		this.ConfigFile = ConfigFile;
	}
	// Get node host name
	public String getNodeHostName(int nodeID){
		String line;
		String[] token;
		int lineNum = 0;     // File line number counter
		int NumOfNode = 0;    // Number of Node
		String hostName = null;

		// Read Config.txt 
		try(
				InputStream       fis = new FileInputStream(ConfigFile);
				InputStreamReader isr = new InputStreamReader(fis);
				BufferedReader    br = new BufferedReader(isr);
		   )
		{
			while ((line = br.readLine()) != null){
				line = line.replaceAll("#(.*)","");    // Delete comments
				if(line.matches("^\\s*$")) continue;   // Delete empty lines
				line = line.replaceAll("^\\s+","");    // Delete space in the begining

				token = line.split("\\s+");            // Save each entity in token array
				lineNum++;
				if (lineNum == 1){
					NumOfNode = Integer.parseInt(token[0]);   // Get Number of node
					continue;
				}
				else if((lineNum-1) <= NumOfNode && nodeID == Integer.parseInt(token[0])){
					// Get host name of node
					hostName = token[1];  
				}				
			}	
		}
		// Exception
		catch (FileNotFoundException ex) {System.out.println(ex.getMessage()); }
		catch (IOException ioe){System.out.println(ioe.getMessage());}
		return hostName;
	}
	//	
	// Get  Node Socket Port:
	//   input node ID, return node socket
	//   server listen port
	//
	public int getNodeListenPort(int nodeID){
		String line;
		String[] token;
		int lineNum = 0;     // File line number counter
		int NumOfNode = 0;    // Number of Node
		int listenPort = 0;

		// Read Config.txt 
		try(
				InputStream       fis = new FileInputStream(ConfigFile);
				InputStreamReader isr = new InputStreamReader(fis);
				BufferedReader    br = new BufferedReader(isr);
		   )
		{
			while ((line = br.readLine()) != null) {
				line = line.replaceAll("#(.*)","");    // Delete comments
				if(line.matches("^\\s*$")) continue;   // Delete empty lines
				line = line.replaceAll("^\\s+","");    // Delete space in the begining

				token = line.split("\\s+");            // Save each entity in token array
				lineNum++;
				if (lineNum == 1){
					NumOfNode = Integer.parseInt(token[0]);   // Get Number of node
					continue;
				}
				else if((lineNum-1) <= NumOfNode && nodeID == Integer.parseInt(token[0])){
					// Get listern port of node
					listenPort = Integer.parseInt(token[2]);  
				}				
			}	
		}
		// Exception
		catch (FileNotFoundException ex) {System.out.println(ex.getMessage()); }
		catch (IOException ioe){System.out.println(ioe.getMessage());}

		return listenPort;
	}
	
	//
	// Get general setting:
	//   return first line of config.txt in an
	//   array
	//
	public int [] getGeneralInfo(){
		String line;
		String token[];
		int generalSetting[] = null;    // gerneral setting array
		int lineNum = 0;
		// Read Config.txt 
		try(
				InputStream       fis = new FileInputStream(ConfigFile);
				InputStreamReader isr = new InputStreamReader(fis);
				BufferedReader    br = new BufferedReader(isr);
		   )
		{
			while ((line = br.readLine()) != null){
				line = line.replaceAll("#(.*)","");    // Delete comments
				if(line.matches("^\\s*$")) continue;   // Delete empty lines
				line = line.replaceAll("^\\s+","");    // Delete space in the begining
				token = line.split("\\s+");            // Save each entity in token array
				lineNum++;
				
				// Get general setting from config.txt 1st line
				if (lineNum == 1){
					generalSetting = new int[token.length];
					for (int i=0; i<token.length; i++)
						generalSetting[i] = Integer.parseInt(token[i]);
				}
			}	
		}
		// Exception
		catch (FileNotFoundException ex){System.out.println(ex.getMessage());}
		catch (IOException ioe){System.out.println(ioe.getMessage());}

		return	generalSetting;
	}
}
