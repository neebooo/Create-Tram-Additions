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
    private static final Gson GSON = new Gson();
    private static final Type MAP_TYPE = new TypeToken<Map<String, String>>() {}.getType();
    private static Map<String, String> map = new HashMap<>();

    public static void load(ServerLevel world) {
        Path path = FileUtils.getDataFile(world);
        if (Files.exists(path)) {
            try {
                String json = Files.readString(path);
                map = GSON.fromJson(json, MAP_TYPE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void save(ServerLevel world) {
        Path path = FileUtils.getDataFile(world);
        try {
            String json = GSON.toJson(map);
            Files.writeString(path, json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Map<String, String> getMap() {
        return map;
    }

    public static void update(ServerLevel world, String key, String value) {
        map.put(key, value);
        save(world);
    }
}
