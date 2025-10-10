package edu.hitsz.rank;
import java.util.List;

public interface RankDAO {
    void addRecord(String name, int score);
    void deleteRecord(int rank);
    List<Record> getAllrecord();
    void showRank();
}
