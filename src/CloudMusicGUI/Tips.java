package CloudMusicGUI;

import javax.swing.*;
import java.awt.*;

public class Tips extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JTextArea tipsInfo;

    /**
     * @param text 对话框中的文本
     * @param point 对话框弹出的位置
     */
    public Tips(String text, Point point) {
        /* 设置图标 */
        ImageIcon icon = new ImageIcon("src\\icon\\tipsIcon.png");
        setIconImage(icon.getImage());
        /* 设定LookAndFell */
        String lookAndFeel = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
        try {
            UIManager.setLookAndFeel(lookAndFeel);
        } catch (ClassNotFoundException | InstantiationException | UnsupportedLookAndFeelException | IllegalAccessException e) {
            e.printStackTrace();
        }

        tipsInfo.setText(text);

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(e -> dispose());
        /* 设置对话框弹出的位置 */
        setLocation(point);
    }
}
