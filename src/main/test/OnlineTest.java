import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;

public class OnlineTest {
    private final static Logger LOG = LoggerFactory.getLogger(OnlineTest.class);

    private static final String ip = "localhost";
    private static final int port = 9094;

    public static void main(String ... args) throws IOException {
        int counter = 0;
        final int MAX_EVENT_BUFFER_LIMIT = 10;
        ArrayList<Integer> buffer = new ArrayList<>();

        while (true) {
            counter++;
            if (isReachable(ip, port, 10)) {
                if (buffer.isEmpty()) {
                    LOG.info("send event...{}", counter);
                }
                else {
                    LOG.info("send event buffer...{}", buffer);
                    buffer.clear();
                }
            }
            else {
                if (buffer.size() != MAX_EVENT_BUFFER_LIMIT) {
                    LOG.info("buffer event... {}", counter);
                    buffer.add(counter);
                }
                else {
                    LOG.info("evict oldest event... {}", buffer.get(0));
                    buffer.remove(0);
                }
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean isReachable(String ip, int port, int timeoutInMillis) {
        boolean isReachable = true;
        try{
            InetSocketAddress sa = new InetSocketAddress(ip, port);
            Socket ss = new Socket();
            ss.connect(sa, timeoutInMillis);
            ss.close();
        }catch(Exception e) {
            isReachable = false;
        }
        return isReachable;
    }

}
