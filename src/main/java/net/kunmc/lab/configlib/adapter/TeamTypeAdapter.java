package net.kunmc.lab.configlib.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
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
            LogManager.getLogger().log(Level.WARN,
                    String.format("Team [%s] is not found at MainScoreboard.", s));
        }

        return t;
    }
}
