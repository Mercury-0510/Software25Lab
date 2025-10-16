package edu.hitsz;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Menu {

    private JPanel MainPanel;
    private JButton easyButton;
    private JButton normalButton;
    private JButton hardButton;
    private JRadioButton radioButton;
    
    private String difficulty = "easy";
    private boolean soundEnabled = true;
    private JFrame menuFrame;
    private GameStartListener gameStartListener;

    public interface GameStartListener {
        void onGameStart(String difficulty, boolean soundEnabled);
    }

    public Menu() {
        easyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                difficulty = "easy";
                startGame();
            }
        });
        normalButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                difficulty = "normal";
                startGame();
            }
        });
        hardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                difficulty = "hard";
                startGame();
            }
        });
        radioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                soundEnabled = radioButton.isSelected();
            }
        });
    }


    public void setGameStartListener(GameStartListener listener) {
        this.gameStartListener = listener;
    }

    private void startGame() {
        if (gameStartListener != null) {
            gameStartListener.onGameStart(difficulty, soundEnabled);
        }
        if (menuFrame != null) {
            menuFrame.dispose();
        }
    }

    public void show() {
        menuFrame = new JFrame("飞机大战 - 选择难度");
        menuFrame.setContentPane(this.MainPanel);
        menuFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        menuFrame.pack();
        menuFrame.setLocationRelativeTo(null);
        menuFrame.setVisible(true);
    }

    public JPanel getMainPanel() {
        return MainPanel;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public boolean isSoundEnabled() {
        return soundEnabled;
    }

}
