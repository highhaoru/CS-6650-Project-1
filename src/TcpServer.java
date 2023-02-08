import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TcpServer {

	public static Logger LOGGER = LogManager.getLogger(TcpServer.class);
public static void main(String[] args) {
		System.out.println(
				"Test prop file: " + SocketReader.getInstance().getProperty("TCP_PUT_REQUEST_DATA"));
		LOGGER.debug("debugging using log4j");

		if (args.length < 1) {
			LOGGER.error("Using: java TcpServerSock <Port Number>");
			System.exit(1);
		}

		int port = Integer.parseInt(args[0]);
		Map<String, String> msgStoreMap = new HashMap<String, String>();

		try {
			ServerSocket server = new ServerSocket(port);
			LOGGER.debug("port number: " + port);
			while (true) {
				LOGGER.debug("waiting  ...");
				Socket client = server.accept();
				LOGGER.debug("waiting 2 ...");
				DataInputStream input = new DataInputStream(client.getInputStream());
				String clientMsg = input.readUTF();
				if (!clientMsg.equals("")) {
					String requestType = clientMsg.substring(0, clientMsg.indexOf(" "));
					String msgContent = clientMsg.substring(clientMsg.indexOf(" "));
					LOGGER.debug("requestType: " + requestType + " msgContent" + msgContent);
					if (requestType.equalsIgnoreCase("PUT")) {
						Put(client, msgContent, msgStoreMap);
					}else if (requestType.equalsIgnoreCase("GET")) {
						Get(client, msgContent, msgStoreMap);
					}else if (requestType.equalsIgnoreCase("DEL")) {
						Delete(client, msgContent, msgStoreMap);
					}else{
						LOGGER.error("Unknown request type: "+requestType+ " is received.");
					}
				}
				LOGGER.debug("current Map size is: " + msgStoreMap.size());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static void Put(Socket client, String msgContent, Map<String, String> messageStoreMap) {
		LOGGER.debug("PUT request received from " + client.getInetAddress() + " at Port " + client.getPort());
		if (!Objects.equals(msgContent, "")) {

			String key = msgContent.substring(0, msgContent.indexOf(","));
			String message = msgContent.substring(msgContent.indexOf(","));
			if (!key.equals("")) {
				LOGGER.debug("The request is to store a message with key: " + key);
				messageStoreMap.put(key, message);
				Ack(client, "PUT", key, "");

			} else {
				LOGGER.error("Received a wrong request of length: " + msgContent.length() + " from: "
						+ client.getInetAddress() + " at Port: " + client.getPort());
			}

		} else {
			LOGGER.debug("The searched message content is not present.");
		}
		try {
			client.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static void Get(Socket client, String msgContent, Map<String, String> messageStoreMap) {
		LOGGER.debug("GET request received from " + client.getInetAddress() + " at Port " + client.getPort());
		if (!Objects.equals(msgContent, "")) {
			if (!Objects.equals(msgContent, "")) {
				LOGGER.debug(" Requesting to get a message with key: " + msgContent);
				if (messageStoreMap.containsKey(msgContent)) {
					String retrievedMsg = messageStoreMap.get(msgContent);
					Ack(client, "GET", msgContent, retrievedMsg);
				} else {
					LOGGER.error("There exist no key-value pair for key: " + msgContent);
				}

			} else {
				LOGGER.error("Received a wrong request of length: " + msgContent.length() + " from: "
						+ client.getInetAddress() + " at Port: " + client.getPort());
			}

		} else {
			LOGGER.debug("The searched message content is not present.");
		}
		try {
			client.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	private static void Delete(Socket client, String msgContent, Map<String, String> messageStoreMap) {
		LOGGER.debug(" DELETE request received from " + client.getInetAddress() + " at Port " + client.getPort());
		if (!Objects.equals(msgContent, "")) {
			if (!Objects.equals(msgContent, "")) {
				LOGGER.debug(" Requesting to delete a message with key: " + msgContent);
				if (messageStoreMap.containsKey(msgContent)) {
					messageStoreMap.remove(msgContent);
					Ack(client, "DELETE", msgContent, "");
				} else {
					LOGGER.error("There exists no key-value pair for key: " + msgContent);
				}

			} else {
				LOGGER.error("Received a wrong request of length: " + msgContent.length() + " from: "
						+ client.getInetAddress() + " at Port: " + client.getPort());
			}

		} else {
			LOGGER.debug("The searched message content is not present.");
		}
		try {
			client.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static void Ack(Socket client, String requestType, String key, String returnMsg) {
		LOGGER.debug("Sending acknowledgement to client...");
		try {
			DataOutputStream outStream = new DataOutputStream(client.getOutputStream());
			if (!Objects.equals(returnMsg, "") && requestType.equalsIgnoreCase("GET")) {
				outStream.writeUTF("Retrieved message with key: " + key + " is: " + returnMsg);
			} else {
				outStream.writeUTF(requestType + " with key: " + key + " SUCCESS");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
