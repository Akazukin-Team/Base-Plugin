package net.akazukin.library.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import net.akazukin.library.utils.ArrayUtils;
import org.bukkit.ChatColor;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Setter
public class ScoreboardManager {
    @Getter
    final String id;
    final List<Score> scores = Collections.synchronizedList(new ArrayList<>());
    @Getter
    String displayName;

    public ScoreboardManager(final String id) {
        this.id = id;
    }

    public void addScore(final Score score) {
        this.scores.add(score);
    }

    public void removeScore(final Score score) {
        this.scores.remove(score);
    }

    public void removeScore(final String id) {
        this.scores.removeIf(s -> s.id.equals(id));
    }

    public boolean containsScore(final String id) {
        return this.scores.stream().anyMatch(s -> s.id.equals(id));
    }

    public void clearScores() {
        this.scores.clear();
    }

    public Score[] getScores() {
        return this.scores.toArray(new Score[0]);
    }

    @FieldDefaults(level = AccessLevel.PRIVATE)
    @Setter
    @Getter
    public static class Score {
        final String id;
        String name;
        int score = 0;

        public Score(final String id, final String name) {
            this.id = id;
            this.name = name;
        }


        public Score(final String id) {
            this.id = id;
            this.name = ArrayUtils.join("",
                    Arrays.stream(ChatColor.values()).limit(8).map(c -> "§" + c.getChar()).collect(ArrayUtils.toShuffle()));
        }
    }
}
