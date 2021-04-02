package objectServer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.function.UnaryOperator;

import etc.Serializing;

public class ObjectSharingServer {

    private UnaryOperator<Object> answerer;
    private int port;

    private int lastID = 0;
    private HashMap<Integer, ServerClientProfile> clientProfile;

    private final static int CLIENT_SUMMARY_BUFFER_SIZE = 4096;
    private final static int ANSWER_SUMMARY_BUFFER_SIZE = 4096;
    private final static int CLIENT_PACKAGE_BUFFER_SIZE = 256;

    Selector selector;
    ServerSocketChannel serverSocket;
    ByteBuffer packageBuffer;
    ByteBuffer consoleInputBuffer;

    public ObjectSharingServer(int port, UnaryOperator<Object> answerer) {
        this.port = port;
        this.answerer = answerer;
        clientProfile = new HashMap<Integer, ServerClientProfile>();
    }

    private void register() throws IOException {
        SocketChannel client = serverSocket.accept();
        client.configureBlocking(false);
        SelectionKey key = client.register(selector, SelectionKey.OP_READ);
        key.attach(++lastID);
        ServerClientProfile thisClientProfile = new ServerClientProfile(CLIENT_SUMMARY_BUFFER_SIZE);
        clientProfile.put(lastID, thisClientProfile);
    }

    private void answer(SelectionKey key) throws IOException {
        SocketChannel client = (SocketChannel) key.channel();
        int messageLength = client.read(packageBuffer);
        Integer thisID = (Integer) key.attachment();
        ServerClientProfile thisProfile = clientProfile.get(thisID);
        if (messageLength == -1) {
            client.close();
            clientProfile.remove(thisID);
        } else {
            packageBuffer.flip();
            thisProfile.addPackage(packageBuffer);
            packageBuffer.clear();
        }
        int currentLength = thisProfile.getCurrentLength();
        int summaryLength = thisProfile.getSummatyLength();
        if (currentLength >= summaryLength + 4) {
            ByteBuffer summaryDataBuffer = thisProfile.getSummaryData();
            byte[] clientObjectData = new byte[summaryLength];
            summaryDataBuffer.position(4);
            summaryDataBuffer.get(clientObjectData, 0, summaryLength);
            Object clientObject = null;
            try {
                clientObject = Serializing.deserializeObject(clientObjectData);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            if (clientObject != null) {
                Object answerObject = answerer.apply(clientObject);
                // there we send object back
                byte[] answerData = Serializing.serializeObject(answerObject);
                ByteBuffer answerDataBuffer = ByteBuffer.allocate(answerData.length+4);
                answerDataBuffer.putInt(answerData.length);
                answerDataBuffer.put(answerData);
                answerDataBuffer.flip();
                client.write(answerDataBuffer);
            }
            client.close();
            clientProfile.remove(thisID);
        }
    }

    public int run() {
        try {
            selector = Selector.open();
            serverSocket = ServerSocketChannel.open();
            serverSocket.bind(new InetSocketAddress("localhost", port));
            serverSocket.configureBlocking(false);
            serverSocket.register(selector, SelectionKey.OP_ACCEPT);
            packageBuffer = ByteBuffer.allocate(CLIENT_PACKAGE_BUFFER_SIZE);

            while (true) {
                selector.select();
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> iter = selectedKeys.iterator();
                while (iter.hasNext()) {
                    SelectionKey key = iter.next();
                    if (key.isAcceptable()) {
                        register();
                    }
                    if (key.isReadable()) {
                        answer(key);
                    }
                    iter.remove();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

}