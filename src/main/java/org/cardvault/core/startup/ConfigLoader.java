package org.cardvault.core.startup;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;

public class ConfigLoader {
    public static Config loadConfig(String fileName) {
        Yaml yaml = new Yaml();
        try (InputStream in = ConfigLoader.class.getClassLoader().getResourceAsStream(fileName)) {
            return yaml.loadAs(in, Config.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
