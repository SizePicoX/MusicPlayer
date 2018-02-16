package GUI;

import javax.swing.*;

/**
 * MusicPlayer的主界面
 */
public class CloudMusic extends JFrame {

    private JPanel panel;

    public static void main(String[] args) {
        JFrame frame = new JFrame("CloudMusic");
        frame.setContentPane(new CloudMusic().panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
