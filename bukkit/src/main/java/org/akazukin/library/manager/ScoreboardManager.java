package org.akazukin.library.manager;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.akazukin.library.utils.ArrayUtils;
import org.akazukin.util.utils.ListUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ScoreboardManager {
    @Getter
    String id;

    Map<Long, Score> newScores = new ConcurrentHashMap<>();
    Map<Long, Score> curScores = new HashMap<>();

    @Getter
    Objective objective;

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

    public Score getOrNew(final long id, final Supplier<Score> newScore) {
        return this.newScores.computeIfAbsent(id, (id_) -> newScore.get());
    }

    public Score get(final long id) {
        return this.newScores.get(id);
    }

    public void addScore(final Score score) {
        this.newScores.put(score.id, score);
    }

    public void removeScore(final long id) {
        this.newScores.remove(id);
    }

    public void removeScore(@NotNull final Score score) {
        this.newScores.remove(score.id, score);
    }

    public boolean containsScore(@NotNull final Score score) {
        return this.newScores.get(score.id) == score;
    }

    public boolean containsScore(final long id) {
        return this.newScores.containsKey(id);
    }

    public void clearScores() {
        this.newScores.clear();
    }

    public Score[] getNewScores() {
        return this.newScores.values().toArray(new Score[0]);
    }

    public void updateScoreBoard() {
        synchronized (this.curScores) {
            synchronized (this.newScores) {
                final List<String> curScoreNames = this.curScores.values()
                        .stream()
                        .map(Score::getName).collect(Collectors.toList());
                final List<String> newScoreNames = this.newScores.values()
                        .stream()
                        .map(Score::getName).collect(Collectors.toList());


                final String[] removes = curScoreNames.stream()
                        .filter(s -> !newScoreNames.contains(s))
                        .toArray(String[]::new);
                final Score[] adds = this.newScores.values()
                        .stream()
                        .filter(s -> !curScoreNames.contains(s.name))
                        .toArray(Score[]::new);
                final Score[] modifies = this.newScores.values()
                        .stream()
                        .filter(s -> this.curScores.values()
                                .stream()
                                .anyMatch(s2 -> s.name.equals(s2.name) && s.score != s2.score))
                        .toArray(Score[]::new);

                final Scoreboard sb = this.objective.getScoreboard();
                for (final String r : removes) {
                    sb.resetScores(r);
                }
                for (final Score a : adds) {
                    this.objective.getScore(a.name).setScore(a.score);
                }
                for (final Score m : modifies) {
                    this.objective.getScore(m.name).setScore(m.score);
                }

                this.curScores.clear();
                this.curScores.putAll(this.newScores);
            }
        }
    }

    @FieldDefaults(level = AccessLevel.PRIVATE)
    @Setter
    @Getter
    @EqualsAndHashCode
    public static class Score implements Cloneable {
        final long id;
        String name;
        int score;

        public Score(final long id, final String name) {
            this.id = id;
            this.name = name;
        }


        public Score(final long id) {
            this.id = id;
            this.name = ListUtils.join("",
                    Arrays.stream(ChatColor.values()).limit(8)
                            .map(c -> "§" + c.getChar())
                            .collect(ArrayUtils.toShuffle()));
        }

        @Override
        public Score clone() {
            final Score s = new Score(this.id, this.name);
            s.score = this.score;
            return s;
        }
    }
}
