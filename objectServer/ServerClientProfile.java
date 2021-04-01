package objectServer;

import java.nio.ByteBuffer;

public class ServerClientProfile {
    
    private int receivedPackagesCount = 0;
    private int summaryLength = 0;
    private ByteBuffer summaryData;

    public ServerClientProfile(int bufferSize) {
        summaryData = ByteBuffer.allocate(bufferSize);
    }

    public int getReceivedPackagesCount() {
        return receivedPackagesCount;
    }

    public void addPackage(ByteBuffer packageData) {
        summaryData.put(packageData);
        if (summaryData.position() >= 4 && summaryLength == 0) {
            ByteBuffer copy = summaryData.duplicate();
            copy.flip();
            summaryLength = copy.getInt();
        }
        ++receivedPackagesCount;
    }

    public ByteBuffer getSummaryData() {
        summaryData.flip();
        return summaryData;
    }

    public int getSummatyLength() {
        return summaryLength;
    }

    public int getCurrentLength() {
        return summaryData.position();
    }
}
