package io.github.eylexlive.leaderboards.util.config;

import io.github.eylexlive.leaderboards.uLeaderboards;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class Config extends YamlConfiguration {

    private final File file;

    public Config(String path) {
        final String str = path.endsWith(".yml") ? path : path + ".yml";
        final uLeaderboards plugin = uLeaderboards.getInstance();

        file = new File(plugin.getDataFolder() + File.separator + "leaderboards", str);
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();

                final InputStream inputStream = plugin.getResource("temp.yml");

                if (inputStream == null) return;

                final FileConfiguration configuration = YamlConfiguration.loadConfiguration(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                configuration.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        reload();
    }

    public void reload() {
        try {
            super.load(this.file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            super.save(this.file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
