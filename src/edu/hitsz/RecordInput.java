package edu.hitsz;

import javax.swing.*;

public class RecordInput extends JDialog {
    // GUI Designer 绑定的组件
    private JPanel mainPanel;
    private JLabel titleLabel;
    private JLabel difficultyLabel;
    private JLabel scoreLabel;
    private JLabel promptLabel;
    private JTextField nameTextField;
    private JButton confirmButton;
    
    // 业务逻辑相关
    private String playerName = null;
    
    public RecordInput(JFrame parent, int score, String difficulty) {
        super(parent, "游戏结束", true);
        
        // 设置对话框内容
        setContentPane(mainPanel);
        
        // 设置显示内容
        difficultyLabel.setText("难度: " + difficulty);
        scoreLabel.setText("得分: " + score);
        
        // 绑定确认按钮事件
        confirmButton.addActionListener(e -> {
            playerName = nameTextField.getText().trim();
            if (playerName.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "姓名不能为空!", 
                    "错误", 
                    JOptionPane.ERROR_MESSAGE);
            } else {
                dispose();
            }
        });
        
        // 回车键确认
        nameTextField.addActionListener(e -> confirmButton.doClick());
        
        pack();
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    }
    
    public String getPlayerName() {
        return playerName;
    }
}
