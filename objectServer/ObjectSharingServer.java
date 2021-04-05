package objectServer;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;
import java.util.function.UnaryOperator;

import etc.Serializing;
import interpreter.Interpreter;

public class ObjectSharingServer {

    private UnaryOperator<Object> objectAnswerer;
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

    private Interpreter consoleAnswerer;

    public ObjectSharingServer(int port, UnaryOperator<Object> objectAnswerer, Interpreter consoleAnswerer) {
        this.port = port;
        this.objectAnswerer = objectAnswerer;
        clientProfile = new HashMap<Integer, ServerClientProfile>();
        this.consoleAnswerer = consoleAnswerer;
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
                Object answerObject = objectAnswerer.apply(clientObject);
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
            
            boolean worksNow = false;

            BufferedReader consoleIn = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                worksNow = false;
                if (consoleIn.ready()) {
                    worksNow = true;
                    consoleAnswerer.interpret(new Scanner(System.in), System.out, true);
                }
                if (selector.selectNow() != 0) {
                    worksNow = true;
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
                if (!worksNow) {
                    Thread.sleep(20);
                }
            }
        } catch (IOException e) {
            System.out.println("[error] "+e.getMessage());
            return -1;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return -1;
        }
    }

}