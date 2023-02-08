Executive Summary

Assignment Overview:

In this project, I successfully developed a server program that functions as a key-value store, allowing a single client to communicate with the server and perform three basic operations: PUT (key, value), GET (key), and DELETE(key). 

The server uses a Hash Map and is single-threaded, only responding to a single request at a time. The client and server communicate using two distinct L4 communication protocols: UDP and TCP, with the client being robust to server failure and malformed datagram packets. 

The client and server logs are time-stamped with the current system time, and the server runs forever until forcibly killed. The key-value store was pre-populated with data and a set of keys using the client, with five PUTs, five GETs, and five DELETEs performed.

Technical Impression:

The assignment requires me to develop a key-value store server program that can be communicated with by a single client. The server program should support three basic operations: PUT (key, value), GET (key), and DELETE (key). The client and server communication should be configurable to use either UDP or TCP protocols. The client must take the hostname or IP address of the server and its port number as command line arguments and must be robust to server failure by using a timeout mechanism. The server should run indefinitely and display requests received and its responses in a human-readable fashion.

The assignment provides a good opportunity to learn about socket programming and network protocols. The requirement to use both UDP and TCP protocols in the communication between the client and server can help you to understand the differences between these protocols and their use cases. Additionally, implementing a timeout mechanism for the client and error handling for malformed packets can improve your understanding of robust software design. Overall, the assignment requires a good understanding of the Java programming language, socket programming, and network protocols to successfully complete.



