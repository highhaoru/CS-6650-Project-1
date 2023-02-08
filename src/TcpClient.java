import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TcpClient {

	private static final Logger LOGGER = LogManager.getLogger(TcpClient.class);

	public static void main(String[] args) {
		LOGGER.debug("Client main is called");
		if (args.length < 2) {
			LOGGER.error("Using: java TcpClientSock <Host Name> <Port Number>");
			System.exit(1);
		}

		String hostName = args[0];
		int portNumber = Integer.parseInt(args[1]);
		LOGGER.debug("in try of client");
		Put(hostName, portNumber);
		
		Get(hostName, portNumber);
		Delete(hostName, portNumber);
	}

	private static void Put(String host, int port) {
		String putReqData = SocketReader.getInstance().getProperty("TCP_PUT_REQUEST_DATA");
		LOGGER.debug("putting data in client hash map: " + putReqData);
		try {
			List<String> items = Arrays.asList(putReqData.split("\\s*\\|\\s*"));
			LOGGER.debug("items stored in as arrays: " + items);
			DataOutputStream outputStream = null;
			Socket client = null;
			for (String tokens : items) {
				client = new Socket(host, port);
				outputStream = new DataOutputStream(client.getOutputStream());
				LOGGER.debug("String items: " + tokens);
				outputStream.writeUTF("PUT " + tokens);
				AckFromServer(client);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static void Get(String host, int port) {
		String getReqData = SocketReader.getInstance().getProperty("TCP_GET_REQUEST_DATA");
		LOGGER.debug("get data or retrieving data in client: " + getReqData);
		Socket client = null;
		try {
			List<String> items = Arrays.asList(getReqData.split("\\s*,\\s*"));
			LOGGER.debug("items retrieve in as arrays: " + items);
			DataOutputStream outputStream = null;

			for (String tokens : items) {
				client = new Socket(host, port);
				outputStream = new DataOutputStream(client.getOutputStream());
				LOGGER.debug("String items: " + tokens);
				outputStream.writeUTF("GET " + tokens);
				AckFromServer(client);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				assert client != null;
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
	
	private static void Delete(String host, int port) {
		String delReqData = SocketReader.getInstance().getProperty("TCP_DEL_REQUEST_DATA");
		LOGGER.debug("deleting data in clients array: " + delReqData);
		Socket client = null;
		try {
			List<String> items = Arrays.asList(delReqData.split("\\s*,\\s*"));
			LOGGER.debug("delete items as arrays: " + items);
			DataOutputStream outputStream = null;

			for (String tokens : items) {
				client = new Socket(host, port);
				outputStream = new DataOutputStream(client.getOutputStream());
				LOGGER.debug("Delete String items: " + tokens);
				outputStream.writeUTF("DEL " + tokens);
				AckFromServer(client);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				assert client != null;
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	private static void AckFromServer(Socket client) {
		try {
			DataInputStream inputStream = new DataInputStream(client.getInputStream());
			client.setSoTimeout(
					Integer.parseInt(SocketReader.getInstance().getProperty("CLIENT SOCKET TIMEOUT")));
			String ackMessage = inputStream.readUTF();
			LOGGER.debug("Acknowledgement message2: " + ackMessage);
		} catch (SocketTimeoutException e) {
			LOGGER.error("Error: Server is not responding. Timeout error has occurred.");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception ex) {
			LOGGER.debug("Exception2: " + ex);
		}
	}

}

