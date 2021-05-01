package objectServer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.ServerSocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.function.UnaryOperator;

import etc.Serializing;

/**
 * ObjectSharingServer
 */
public class ObjectSharingServer {

    private UnaryOperator<Object> objectAnswerer;
    private int port;
    private ServerSocket serverSocket;

    private ExecutorService readingExecutor = Executors.newCachedThreadPool();
    private ForkJoinPool processingExecutor = new ForkJoinPool();

    private class ClientProcessingRunnable implements Runnable {
    
        private Socket clientSocket;
        private byte[] message;

        public ClientProcessingRunnable(Socket clientSocket, byte[] message) {
            this.clientSocket = clientSocket;
            this.message = message;
        }

        @Override
        public void run() {
            try {
                Object messageObject = Serializing.deserializeObject(message);
                Object answerObject = objectAnswerer.apply(messageObject);
                byte[] answer = Serializing.serializeObject(answerObject);

                OutputStream outputStream = clientSocket.getOutputStream();
                InputStream inputStream = clientSocket.getInputStream();
                DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
                dataOutputStream.writeInt(answer.length);
                outputStream.write(answer);
                outputStream.flush();

                clientSocket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class ClientReadingRunnable implements Runnable {

        private Socket clientSocket;

        public ClientReadingRunnable(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try {
                OutputStream outputStream = clientSocket.getOutputStream();
                InputStream inputStream = clientSocket.getInputStream();
                DataInputStream dataInputStream = new DataInputStream(inputStream);
                int messageSize = dataInputStream.readInt();
                byte[] message = new byte[messageSize];
                inputStream.read(message);

                ClientProcessingRunnable clientProcessingRunnable = new ClientProcessingRunnable(clientSocket, message);
                processingExecutor.submit(clientProcessingRunnable);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Runnable answerRunnable = () -> {
        Socket clientSocket;
        try {
            while (true) {
                clientSocket = serverSocket.accept();
                ClientReadingRunnable clientReadingRunnable = new ClientReadingRunnable(clientSocket);
                readingExecutor.submit(clientReadingRunnable);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    };

    private Thread answerThread;

    public ObjectSharingServer(int port, UnaryOperator<Object> objectAnswerer) {
        this.port = port;
        this.objectAnswerer = objectAnswerer;
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(port);
            answerThread = new Thread(answerRunnable);
            answerThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        readingExecutor.shutdown();
        processingExecutor.shutdown();
    }
}