package hu.qliqs.state;


import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.LevelResource;

import java.nio.file.Path;

public class FileUtils {
    public static Path getDataFile(ServerLevel world) {
        Path saveDir = world.getServer().getWorldPath(LevelResource.ROOT);
        return saveDir.resolve("stored_sounds.json");
    }
}
