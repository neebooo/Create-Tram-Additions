package hu.qliqs.state;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.minecraft.server.level.ServerLevel;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class JsonMapStorage {
    private final Gson GSON = new Gson();
    private final Type MAP_TYPE = new TypeToken<Map<String, String>>() {}.getType();
    private Map<String, String> map = new HashMap<>();
    private final ServerLevel world;

    public JsonMapStorage(ServerLevel world) {
        this.world = world;
    }

    public void load(ServerLevel world) {
        Path path = FileUtils.getDataFile(world);
        if (Files.exists(path)) {
            try {
                String json = Files.readString(path);
                this.map = GSON.fromJson(json, MAP_TYPE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void save() {
        Path path = FileUtils.getDataFile(this.world);
        try {
            String json = GSON.toJson(this.map);
            Files.writeString(path, json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void delete() {
        map.clear();
    }

    public Map<String, String> getMap() {
        return map;
    }

    public void update(String key, String value) {
        map.put(key, value);
        save();
    }
}
