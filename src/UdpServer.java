import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UdpServer {

	private static final Logger LOGGER = LogManager.getLogger(UdpServer.class);

	public static void main(String[] args) {

		if (args.length < 1) {
			LOGGER.error("Usage: java UpdServer <Port Number>");
			System.exit(1);
		}

		int portNumber = Integer.parseInt(args[0]);
		Map<String, String> messageStoreMap = new HashMap<String, String>();

		DatagramSocket socketByte = null;
		try {
			socketByte = new DatagramSocket(portNumber);

			byte[] buffer = new byte[500];

			while (true) {
				DatagramPacket dataPacket = new DatagramPacket(buffer, buffer.length);
				socketByte.receive(dataPacket);
				System.out.println("Message from client: " + new String(dataPacket.getData()));
				String clientMessage = new String(dataPacket.getData());
				if (!clientMessage.equals("")) {
					String requestType = clientMessage.substring(0, clientMessage.indexOf(" "));
					LOGGER.debug("requestType: " + requestType);
					if (requestType.equalsIgnoreCase("PUT")) {
						Put(socketByte, dataPacket, messageStoreMap);
					} else if (requestType.equalsIgnoreCase("GET")) {
						Get(socketByte, dataPacket, messageStoreMap);
					} else if (requestType.equalsIgnoreCase("DEL")) {
						Delete(socketByte, dataPacket, messageStoreMap);
					} else {
						LOGGER.error("Unknown request type: " + requestType + " is received.");
					}
				}
				LOGGER.debug("Current Map size is: " + messageStoreMap.size());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static void Put(DatagramSocket socket, DatagramPacket clientPacket,
							Map<String, String> messageStoreMap) {
		LOGGER.debug("Received a PUT request from " + clientPacket.getAddress() + " at Port " + clientPacket.getPort());
		String msgData = new String(clientPacket.getData());
		if (!msgData.equals("")) {
			String keyValueData = msgData.substring(msgData.indexOf(" "));
			String key = keyValueData.substring(0, keyValueData.indexOf(","));
			String message = keyValueData.substring(keyValueData.indexOf(",") + 1);
			if (!key.equals("")) {
				LOGGER.debug("The request is to store a message with key: " + key + " and Message" + message);
				messageStoreMap.put(key.trim(), message);
				Ack(socket, clientPacket, "PUT", key, "");

			} else {
				String failureMsg = "Received a malformed request of length: " + clientPacket.getLength() + " from: "
						+ clientPacket.getAddress() + " at Port: " + clientPacket.getPort();
				LOGGER.error(failureMsg);
				sendFailureAck(socket, clientPacket, failureMsg);
			}

		} else {
			String failMsg = "Fail:The message content is not present.";
			LOGGER.error(failMsg);
			sendFailureAck(socket, clientPacket, failMsg);
		}

	}

	private static void Get(DatagramSocket socket, DatagramPacket clientPacket,
							Map<String, String> messageStoreMap) {
		LOGGER.debug("Received a GET request from " + clientPacket.getAddress() + " at Port " + clientPacket.getPort());
		String messageData = new String(clientPacket.getData());
		if (!messageData.equals("")) {
			String keyValueData = messageData.substring(messageData.indexOf(" "));
			String key = keyValueData.substring(0, keyValueData.indexOf(","));
			if (!key.equals("")) {
				LOGGER.debug("The request is to get a message with key: " + key);
				if (messageStoreMap.containsKey(key.trim())) {
					String retrievedMsg = messageStoreMap.get(key.trim());
					Ack(socket, clientPacket, "GET", key, retrievedMsg);
				} else {
					String failMsg = "Fail:There is no key-value pair for key: " + key;
					LOGGER.error(failMsg);
					sendFailureAck(socket, clientPacket, failMsg);
				}

			} else {
				String failMsg = "Fail:Received a malformed request of length: " + clientPacket.getLength() + " from: "
						+ clientPacket.getAddress() + " at Port: " + clientPacket.getPort();
				LOGGER.error(failMsg);
				sendFailureAck(socket, clientPacket, failMsg);
			}

		} else {
			String failMsg = "Fail:The message content is not present.";
			LOGGER.error(failMsg);
			sendFailureAck(socket, clientPacket, failMsg);
		}

	}

	private static void Delete(DatagramSocket socket, DatagramPacket clientPacket,
							   Map<String, String> messageStoreMap) {
		LOGGER.debug(
				"Received a DELETE request from " + clientPacket.getAddress() + " at Port " + clientPacket.getPort());
		String messageData = new String(clientPacket.getData());
		if (!messageData.equals("")) {
			String keyValueData = messageData.substring(messageData.indexOf(" "));
			String key = keyValueData.substring(0, keyValueData.indexOf(","));
			if (!key.equals("")) {
				LOGGER.debug("The request is to get a message with key: " + key);
				if (messageStoreMap.containsKey(key.trim())) {
					messageStoreMap.remove(key.trim());
					Ack(socket, clientPacket, "DEL", key, "");
				} else {
					String failureMsg = "There exist no such key-value pair for key: " + key;
					LOGGER.error(failureMsg);
					sendFailureAck(socket, clientPacket, failureMsg);
				}

			} else {
				String failureMsg = "Received a malformed request of length: " + clientPacket.getLength() + " from: "
						+ clientPacket.getAddress() + " at Port: " + clientPacket.getPort();
				LOGGER.error(failureMsg);
				sendFailureAck(socket, clientPacket, failureMsg);
			}

		} else {
			String failureMsg = "The message content is not present.";
			LOGGER.error(failureMsg);
			sendFailureAck(socket, clientPacket, failureMsg);
		}

	}

	private static void Ack(DatagramSocket socket, DatagramPacket request, String requestType, String key,
							String returnMsg) {
		LOGGER.debug("Sending acknowledgement to client...");
		try {
			byte[] ackMessage = new byte[500];
			if (!Objects.equals(returnMsg, "") && requestType.equalsIgnoreCase("GET")) {
				ackMessage = ("Retrieved message with key: " + key + " is: " + returnMsg).getBytes();
			} else {
				ackMessage = (requestType + " with key: " + key + " SUCCESS").getBytes();
			}
			DatagramPacket ackMsgPacket = new DatagramPacket(ackMessage, ackMessage.length, request.getAddress(),
					request.getPort());
			socket.send(ackMsgPacket);

		} catch (IOException e) {
			LOGGER.error("An exception has occurred: " + e);
		}

	}

	private static void sendFailureAck(DatagramSocket socket, DatagramPacket request, String returnMsg) {
		LOGGER.debug("Sending acknowledgement to client for failure...");
		try {
			byte[] ackMessage = new byte[500];
			ackMessage = ("Request FAILED due to: " + returnMsg).getBytes();
			DatagramPacket ackMsgPacket = new DatagramPacket(ackMessage, ackMessage.length, request.getAddress(),
					request.getPort());
			socket.send(ackMsgPacket);

		} catch (IOException e) {
			LOGGER.error("An exception has occurred: " + e);
		}

	}

}