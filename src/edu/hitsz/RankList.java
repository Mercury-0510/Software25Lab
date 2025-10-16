package edu.hitsz;

import edu.hitsz.rank.RankDAO;
import edu.hitsz.rank.Record;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class RankList extends JFrame {
    // GUI Designer 绑定的组件
    private JPanel mainPanel;
    private JLabel titleLabel;
    private JTable rankTable;
    private JButton deleteButton;
    private JButton closeButton;
    private JPanel buttonPanel;
    
    // 业务逻辑相关
    private DefaultTableModel tableModel;
    private RankDAO rankDAO;
    private String difficulty;
    
    public RankList(RankDAO rankDAO, String difficulty) {
        this.rankDAO = rankDAO;
        this.difficulty = difficulty;
        
        // 设置窗口
        setTitle("排行榜 - 难度: " + difficulty);
        setContentPane(mainPanel);
        
        // 初始化表格模型
        initTable();
        
        // 绑定事件
        deleteButton.addActionListener(e -> deleteSelectedRecord());
        closeButton.addActionListener(e -> dispose());
        
        // 加载数据
        loadRankData();
        
        setSize(600, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }
    
    /**
     * 初始化表格
     */
    private void initTable() {
        String[] columnNames = {"排名", "玩家姓名", "得分", "时间"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 不可编辑
            }
        };
        rankTable.setModel(tableModel);
        rankTable.setRowHeight(30);
        rankTable.getTableHeader().setFont(rankTable.getFont().deriveFont(16f).deriveFont(java.awt.Font.BOLD));
        rankTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }
    
    /**
     * 加载排行榜数据
     */
    private void loadRankData() {
        tableModel.setRowCount(0); // 清空表格
        
        List<Record> records = rankDAO.getAllrecord();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        for (Record record : records) {
            Object[] row = {
                record.getRankNum(),
                record.getName(),
                record.getScore(),
                sdf.format(new Date(record.getTime()))
            };
            tableModel.addRow(row);
        }
    }
    
    /**
     * 删除选中的记录
     */
    private void deleteSelectedRecord() {
        int selectedRow = rankTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "请先选择要删除的记录!", 
                "提示", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int rank = (int) tableModel.getValueAt(selectedRow, 0);
        String name = (String) tableModel.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "确定要删除 " + name + " 的记录吗?",
            "确认删除",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            rankDAO.deleteRecord(rank);
            loadRankData(); // 重新加载数据
        }
    }
    
    /**
     * 显示玩家姓名输入对话框
     * @param parent 父窗口
     * @param score 游戏得分
     * @param difficulty 游戏难度
     * @return 玩家输入的姓名,如果取消或为空则返回"匿名玩家"
     */
    public static String showInputDialog(JFrame parent, int score, String difficulty) {
        String playerName = JOptionPane.showInputDialog(
                parent,
                "游戏结束，你的得分是：" + score + "\n难度：" + difficulty + "\n请输入名字计入排行榜："
        );
        
        if (playerName == null || playerName.trim().isEmpty()) {
            playerName = "匿名";
        }
        
        return playerName;
    }
}
