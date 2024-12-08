package org.akazukin.library.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.akazukin.library.utils.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class ScoreboardManager {
    @Getter
    final String id;

    @Getter
    final Objective objective;

    final List<Score> newScores = Collections.synchronizedList(new ArrayList<>());
    final List<Score> currentScores = new ArrayList<>();

    public ScoreboardManager(final String id) {
        this.id = id;
        this.objective = Bukkit.getScoreboardManager().getNewScoreboard().registerNewObjective(id, id, "");
    }

    public String getDisplayName() {
        return this.objective.getDisplayName();
    }

    public void setDisplayName(final String name) {
        this.objective.setDisplayName(name);
    }

    public DisplaySlot getDisplaySlot() {
        return this.objective.getDisplaySlot();
    }

    public void setDisplaySlot(final DisplaySlot slot) {
        this.objective.setDisplaySlot(slot);
    }

    public Score getOrNew(final String id, final Supplier<Score> newScore) {
        final Score s = this.get(id);
        if (s != null) return s;

        final Score s2 = newScore.get();
        this.newScores.add(s2);
        return s2;
    }

    public Score get(final String id) {
        synchronized (this.newScores) {
            return this.newScores.stream().filter(s -> s.id.equals(id)).findFirst().orElse(null);
        }
    }

    public void addScore(final Score score) {
        this.removeScore(score.id);
        this.newScores.add(score);
    }

    public void removeScore(final String id) {
        this.newScores.removeIf(s -> s.id.equals(id));
    }

    public void removeScore(final Score score) {
        this.newScores.remove(score);
    }

    public boolean containsScore(final Score score) {
        return this.containsScore(score.id);
    }

    public boolean containsScore(final String id) {
        synchronized (this.newScores) {
            return this.newScores.stream().anyMatch(s -> s.id.equals(id));
        }
    }

    public void clearScores() {
        this.newScores.clear();
    }

    public Score[] getNewScores() {
        return this.newScores.toArray(new Score[0]);
    }

    public void updateScoreBoard() {
        synchronized (this.currentScores) {
            synchronized (this.newScores) {
                final List<String> currentScoreNames = this.currentScores.stream()
                        .map(Score::getName).collect(Collectors.toList());
                final List<String> newScoreNames = this.newScores.stream()
                        .map(Score::getName).collect(Collectors.toList());

                final Score[] removes = this.currentScores.stream()
                        .filter(s -> !newScoreNames.contains(s.name))
                        .toArray(Score[]::new);
                final Score[] adds = this.newScores.stream()
                        .filter(s -> !currentScoreNames.contains(s.name))
                        .toArray(Score[]::new);
                final List<Score> modifies = this.newScores.stream()
                        .filter(s -> this.currentScores.stream().anyMatch(s2 -> s.name.equals(s2.name) && s.score != s2.score))
                        .collect(Collectors.toList());

                final Scoreboard sb = this.objective.getScoreboard();
                for (final Score r : removes) sb.resetScores(r.name);
                for (final Score a : adds) this.objective.getScore(a.name).setScore(a.score);
                for (final Score m : modifies) this.objective.getScore(m.name).setScore(m.score);

                this.currentScores.clear();
                this.currentScores.addAll(this.newScores.stream().map(Score::clone).collect(Collectors.toList()));
            }
        }
    }

    @FieldDefaults(level = AccessLevel.PRIVATE)
    @Setter
    @Getter
    @EqualsAndHashCode
    public static class Score implements Cloneable {
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

        @Override
        protected Score clone() {
            final Score s = new Score(this.id, this.name);
            s.score = this.score;
            return s;
        }
    }
}
