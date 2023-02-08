import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UdpClient {
	private static final Logger LOGGER = LogManager.getLogger(UdpClient.class);
	public static void main(String[] args) {
		if (args.length < 2) {
			LOGGER.error("Usage: java UdpClientSock <Host Name> <Port Number>");
			System.exit(1);
		}

		String hostName = args[0];

		int port = Integer.parseInt(args[1]);
		
		try {
			InetAddress host = InetAddress.getByName(hostName);
			
			Put(host,port);
			Get(host,port);
			Delete(host,port);

		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

	}

	private static void Put(InetAddress hostAddress, int port) {
		String putReqData = SocketReader.getInstance().getProperty("UDP_PUT_REQUEST_DATA");
		LOGGER.debug("put data in client: " + putReqData);
		DatagramSocket client = null;
		try {
			String[] items = putReqData.split("\\s*\\|\\s*");
			for (String tokens : items) {
				client = new DatagramSocket();
				LOGGER.debug("Message String items: " + tokens);
				String clientMsg = "PUT " + tokens;
				DatagramPacket clientMsgPacket = new DatagramPacket(clientMsg.getBytes(),clientMsg.length(),hostAddress,port);
				client.send(clientMsgPacket);
				AckFromServer(client);
				client.close();
			}

		} catch (IOException e) {
			LOGGER.error("An exception has occurred: " + e);
		} finally {
			assert client != null;
			client.close();
		}
	}
	
	private static void Get(InetAddress hostAddress, int port) {
		String getReqData = SocketReader.getInstance().getProperty("UDP_GET_REQUEST_DATA");
		LOGGER.debug("get data in client: " + getReqData);
		DatagramSocket client = null;
		try {
			String[] items = getReqData.split("\\s*,\\s*");
			for (String tokens : items) {
				client = new DatagramSocket();
				LOGGER.debug("Message String items: " + tokens);
				String clientMsg = "GET " + tokens;
				DatagramPacket clientMsgPacket = new DatagramPacket(clientMsg.getBytes(),clientMsg.length(),hostAddress,port);
				client.send(clientMsgPacket);
				AckFromServer(client);
				client.close();
			}

		} catch (IOException e) {
			LOGGER.error("An exception has occurred: " + e);
		} finally {
			assert client != null;
			client.close();
		}
	}
	
	private static void Delete(InetAddress hostAddress, int port) {
		String delReqData = SocketReader.getInstance().getProperty("UDP_DEL_REQUEST_DATA");
		LOGGER.debug("get delete data in client: " + delReqData);
		DatagramSocket client = null;
		try {
			String[] items = delReqData.split("\\s*,\\s*");
			for (String tokens : items) {
				client = new DatagramSocket();
				LOGGER.debug("Message String items: " + tokens);
				String clientMsg = "DEL " + tokens;
				DatagramPacket clientMsgPacket = new DatagramPacket(clientMsg.getBytes(),clientMsg.length(),hostAddress,port);
				client.send(clientMsgPacket);
				AckFromServer(client);
				client.close();
			}

		} catch (IOException e) {
			LOGGER.error("An exception has occurred: " + e);
		} finally {
			assert client != null;
			client.close();
		}
	}

	private static void AckFromServer(DatagramSocket client) {
		try {
			client.setSoTimeout(
					Integer.parseInt(SocketReader.getInstance().getProperty("CLIENT_SOCKET_TIMEOUT")));
			byte[] ackMsgBuffer = new byte[500];
			DatagramPacket returnMsgPacket = new DatagramPacket(ackMsgBuffer, ackMsgBuffer.length);
			client.receive(returnMsgPacket);
			LOGGER.debug("Acknowledgement message: " + new String(returnMsgPacket.getData()));
		} catch (SocketTimeoutException e) {
			LOGGER.error("Server is not responding. Timeout error has occurred.");
		} catch (IOException e) {
			LOGGER.error("An exception has occurred: " + e);
		} catch (Exception ex) {
			LOGGER.debug("Exception: " + ex);
		}
	}
}