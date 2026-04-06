package com.example.examsystemproject;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class NetworkClient {

    private final String host;
    private final int port;

    public NetworkClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public Response send(Request request) throws Exception {
        try (
                Socket socket = new Socket(host, port);
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream())
        ) {
            out.writeObject(request);
            out.flush();
            return (Response) in.readObject();
        }
    }
}

