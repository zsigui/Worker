package sg.jackiez.worker.utils.http;

import java.net.HttpURLConnection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

class ConnectionManager {

    private static ConnectionManager sInstance;
    private ConcurrentMap<String, HttpURLConnection> mConnectionMap = new ConcurrentHashMap<>();

    public static ConnectionManager get() {
        if (sInstance == null) {
            synchronized (ConnectionManager.class) {
                if (sInstance == null) {
                    sInstance = new ConnectionManager();
                }
            }
        }
        return sInstance;
    }

    public void addConnection(String id, HttpURLConnection connection) {
        if (connection == null) {
            return;
        }
        mConnectionMap.put(id, connection);
    }

    public void removeConnection(String id) {
        if (!mConnectionMap.containsKey(id)) {
            return;
        }
        mConnectionMap.remove(id).disconnect();
    }
}
