package sagex.miniclient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sagex.miniclient.prefs.PrefStore;

/**
 * Utility methods for dealing with Server Connection Data
 */
public class Servers {
    private static final Logger log = LoggerFactory.getLogger(Servers.class);

    MiniClient client;

    public Servers(MiniClient client) {
        this.client = client;
    }

    public void saveServer(ServerInfo si) {
        si.save(client.properties());
    }

    public void deleteServer(String serverName) {
        String parts[] = null;
        ArrayList list = new ArrayList(client.properties().keys());
        Collections.sort(list);
        for (Object k : list) {
            if (k.toString().startsWith("servers/")) {
                // process server
                parts = k.toString().split("/");
                if (parts.length > 1 && parts[1].equals(serverName)) {
                    client.properties().remove((String) k);
                }
            }
        }
    }

    public ServerInfo getServer(String serverName) {
        ServerInfo si = new ServerInfo();
        si.load(serverName, client.properties());

        // not found
        if ((si.address == null || si.address.trim().length() == 0) && (si.locatorID == null || si.locatorID.trim().length() == 0)) {
            return null;
        }
        return si;
    }

    public List<ServerInfo> getSavedServers() {
        List<ServerInfo> servers = new ArrayList<ServerInfo>();
        String lastServer = null;
        String thisServer = null;
        String parts[] = null;
        ArrayList list = new ArrayList(client.properties().keys());
        Collections.sort(list);
        ServerInfo si = null;
        for (Object k : list) {
            if (k.toString().startsWith("servers/")) {
                // process server
                parts = k.toString().split("/");
                if (parts.length > 1) {
                    thisServer = parts[1];
                    if (thisServer.equals(lastServer)) continue;
                    lastServer = thisServer;
                    si = getServer(parts[1]);
                    // add Server
                    servers.add(si);
                    log.debug("Adding Saved Server: {}", si);
                }
            }
        }
        return servers;
    }

    public void setLastConnectedServer(ServerInfo si) {
        client.properties().setString(PrefStore.Keys.last_connected_server, si.name);
    }

    public ServerInfo getLastConnectedServer() {
        String last = client.properties().getString(PrefStore.Keys.last_connected_server);
        if (last == null) return null;
        ServerInfo si = getServer(last);
        return si;
    }
}
