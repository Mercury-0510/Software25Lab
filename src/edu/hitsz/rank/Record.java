package edu.hitsz.rank;

public class Record {
    private String name;
    private int score;
    private long time;
    private int ranknum;

    // 构造函数
    public Record(String name, int score, long time, int ranknum) {
        this.name = name;
        this.score = score;
        this.time = time;
        this.ranknum = ranknum;
    }

    // Getter 和 Setter
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getRankNum() {
        return ranknum;
    }

    public void setRankNum(int ranknum) {
        this.ranknum = ranknum;
    }

}

