import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class NetworkTablesClient {

    public static void main(String[] args) throws Exception {
	new NetworkTablesClient().run();
    }

    public void run() throws Exception {
	Socket localSocket;
	PrintWriter socketOut;
	BufferedReader socketIn;

	// Establish connection to targeting server
	localSocket = new Socket("localhost", 3341);
	socketOut = new PrintWriter(localSocket.getOutputStream(), true);
	socketIn = new BufferedReader(new InputStreamReader(localSocket.getInputStream()));

	// Establish connection to cRIO server
	NetworkTable.setClientMode();
	NetworkTable.setIPAddress("10.33.41.2");
	NetworkTable table = NetworkTable.getTable("targeting");

	// Set data defaults
	table.putBoolean("connection", true);
	table.putBoolean("found", true);
	table.putBoolean("target", false);
	table.putNumber("distance", 0.0);
	table.putNumber("horizontalDistance", 0.0);
	table.putNumber("azimuth", 0.0);

	// Get data from targeting server and send to cRIO server
	while (true) {
	    socketOut.println("");
	    Thread.sleep(100); // Update values every 100 ms
	    String targetDataRaw = null;
	    targetDataRaw = socketIn.readLine();

	    if (targetDataRaw == null)
		break;
	    if (targetDataRaw.isEmpty())
		continue;

	    // Parse targeting data
	    if (targetDataRaw.equals("No rectangle")) {
		table.putBoolean("found", false);
		System.out.println("No rectangle found");
	    } else {
		String[] targetDataParsed = targetDataRaw.split(";");
		table.putBoolean("found", true);
		table.putBoolean("target", targetDataParsed[0].equals("true"));
		table.putNumber("distance", Double.parseDouble(targetDataParsed[1]));
		table.putNumber("horizontalDistance", Double.parseDouble(targetDataParsed[2]));
		table.putNumber("azimuth", Double.parseDouble(targetDataParsed[3]));
	    }
	}

	// Clean up
	socketOut.close();
	socketIn.close();
	localSocket.close();
    }
}
