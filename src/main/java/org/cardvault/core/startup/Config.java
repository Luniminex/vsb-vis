package org.cardvault.core.startup;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Config {
    private Server server;
    private Database database;

    @Getter
    @Setter
    public static class Server {
        private int port;
        private String host;

    }
    @Getter
    @Setter
    public static class Database {
        private String url;
        private String username;
        private String password;
    }
}
