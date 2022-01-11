package net.kunmc.lab.configlib.config;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Team;

import java.io.IOException;

public class TeamTypeAdapter extends TypeAdapter<Team> {
    @Override
    public void write(JsonWriter out, Team value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            out.value(value.getName());
        }
    }

    @Override
    public Team read(JsonReader in) throws IOException {
        String s = in.nextString();
        Team t = Bukkit.getScoreboardManager().getMainScoreboard().getTeam(s);
        if (t == null) {
            throw new IOException(String.format("Team %s is not found from MainScoreboard", s));
        }

        return t;
    }
}