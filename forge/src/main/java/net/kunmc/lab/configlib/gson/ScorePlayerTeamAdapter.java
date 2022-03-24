package net.kunmc.lab.configlib.gson;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.io.IOException;

public class ScorePlayerTeamAdapter extends TypeAdapter<ScorePlayerTeam> {
    @Override
    public void write(JsonWriter out, ScorePlayerTeam value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            out.value(value.getName());
        }
    }

    @Override
    public ScorePlayerTeam read(JsonReader in) throws IOException {
        String s = in.nextString();
        if (ServerLifecycleHooks.getCurrentServer() == null) {
            return null;
        }

        return ServerLifecycleHooks.getCurrentServer().getScoreboard().getTeam(s);
    }
}
