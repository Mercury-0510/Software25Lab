package edu.hitsz.rank;
import java.io.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;

public class RankDAOImpl implements RankDAO {

    private List<Record> rank = new ArrayList<>();
    private String filePath;

    public RankDAOImpl(String filePath) {
        this.filePath = filePath;
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("//")) {
                    continue; // 跳过空行和注释
                }
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    this.rank.add(new Record(
                            parts[0].trim(),
                            Integer.parseInt(parts[1].trim()),
                            Long.parseLong(parts[2].trim()),
                            Integer.parseInt(parts[3].trim())
                    ));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addRecord(String name, int score) {
        // 获取当前时间戳
        long currentTime = System.currentTimeMillis();
        
        // 创建新记录，初始排名设为0，后续会重新计算
        Record newRecord = new Record(name, score, currentTime, 0);
        rank.add(newRecord);
        
        // 按分数降序排序（分数高的排名靠前）
        Collections.sort(rank, new Comparator<Record>() {
            @Override
            public int compare(Record r1, Record r2) {
                // 先按分数降序排序
                int scoreCompare = Integer.compare(r2.getScore(), r1.getScore());
                if (scoreCompare != 0) {
                    return scoreCompare;
                }
                // 分数相同时按时间升序排序（早的排名靠前）
                return Long.compare(r1.getTime(), r2.getTime());
            }
        });
        
        // 重新计算排名
        updateRankNumbers();
        
        // 保存到文件
        saveToFile();
    }

    @Override
    public void deleteRecord(int rankNum) {
        // 查找指定排名的记录并删除
        for (int i = 0; i < rank.size(); i++) {
            if (rank.get(i).getRankNum() == rankNum) {
                rank.remove(i);
                break;
            }
        }
        
        // 重新计算排名
        updateRankNumbers();
        
        // 保存到文件
        saveToFile();
    }

    @Override
    public List<Record> getAllrecord() {
        return new ArrayList<>(rank); // 返回副本以避免外部修改
    }
    
    /**
     * 更新所有记录的排名编号
     */
    private void updateRankNumbers() {
        for (int i = 0; i < rank.size(); i++) {
            rank.get(i).setRankNum(i + 1);
        }
    }
    
    /**
     * 将当前排行榜数据保存到文件
     */
    private void saveToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println("// 排行榜数据文件");
            writer.println("// 格式: 姓名,分数,时间戳,排名");
            for (Record record : rank) {
                writer.println(record.getName() + "," + 
                             record.getScore() + "," + 
                             record.getTime() + "," + 
                             record.getRankNum());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 显示排行榜信息
     * 以格式化的方式在控制台输出所有排行榜记录
     */
    public void showRank(){
        System.out.println("==================== 排行榜 ====================");
        System.out.printf("%-8s %-15s %-10s %-20s%n", "排名", "玩家姓名", "分数", "游戏时间");
        System.out.println("================================================");
        
        if (rank.isEmpty()) {
            System.out.println("暂无排行榜记录");
        } else {
            for (Record record : rank) {
                // 将时间戳转换为可读格式
                java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String formattedTime = dateFormat.format(new java.util.Date(record.getTime()));
                
                System.out.printf("%-8d %-15s %-10d %-20s%n", 
                    record.getRankNum(), 
                    record.getName(), 
                    record.getScore(), 
                    formattedTime);
            }
        }
        System.out.println("================================================");
    }
}


