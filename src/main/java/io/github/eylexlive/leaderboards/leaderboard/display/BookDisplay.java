package io.github.eylexlive.leaderboards.leaderboard.display;

import io.github.eylexlive.leaderboards.leaderboard.Leaderboard;
import io.github.eylexlive.leaderboards.leaderboard.LeaderboardStat;
import io.github.eylexlive.leaderboards.util.ColorUtil;
import io.github.eylexlive.leaderboards.util.ReplaceUtil;
import io.github.eylexlive.leaderboards.util.book.BookUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class BookDisplay implements ILeaderboardDisplay {

    private final Map<Leaderboard, BookUtil.BookBuilder> bookMap = new HashMap<>();

    @Override
    public void setup(Leaderboard leaderboard) {
        final BookUtil.BookBuilder bookBuilder = BookUtil.writtenBook();
        bookBuilder.title("Leaderboard");
        bookMap.put(leaderboard, bookBuilder);
    }

    @Override
    public void unSetup(Leaderboard leaderboard) {
        bookMap.remove(leaderboard);
    }

    public List<String> getPages(Leaderboard leaderboard, Player player) {
        final List<String> stringList = ColorUtil.translate(
                leaderboard.getStringListMap().get("bookPages")
        );

        if (stringList.size() < 1) stringList.addAll(ColorUtil.translate(Collections.singletonList("&4&lKİTAP SAYFALARI GÖSTERİLEMİYOR!\n&f\n&cSayfalar configde bulunamadı!")));

        stringList.replaceAll(x -> x.replace("%n", "\n"));
        stringList.replaceAll(x -> x.replace("{pos}", plugin.getLeaderboardManager()
                .getPosition(player, leaderboard) + "")
        );

        for (int i = 1; i <= leaderboard.getLimit(); i++) {
            final LeaderboardStat stat = plugin.getLeaderboardManager().getStat(leaderboard, i, true);

            ReplaceUtil.replacePlh(
                    stringList,
                    "lb_" + i + ":" + stat.getName(),
                    "lb_" + i + "_value:" + stat.getValue()
            );
        }
        return stringList;
    }

    public BookUtil.BookBuilder getBook(Leaderboard leaderboard) {
        if (bookMap.get(leaderboard) == null)
            setup(leaderboard);
        return bookMap.get(leaderboard);
    }

    @Override
    public boolean setup(Leaderboard leaderboard, Location location, int index) {
        return true;
    }

    @Override
    public void unSetup(Leaderboard leaderboard, int index) {

    }
}
