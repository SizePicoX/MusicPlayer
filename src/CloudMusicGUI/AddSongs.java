package CloudMusicGUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class AddSongs extends JDialog {
    private JPanel contentPane;
    private JPanel buttonPanel;
    private JCheckBox myMusicCheckBox;
    private JCheckBox downloadCheckBox;
    private JCheckBox windowsMediaPlayerCheckBox;
    private JLabel label;
    private JButton buttonOK;
    private JButton choseOtherFolder;
    private JPanel myPanel;

    AddSongs(Point point) {
        setUI();

        setTitle("选择本地音乐文件夹");

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        choseOtherFolder.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        setLocation(point);
    }

    /**
     * buttonOK的监听器
     */
    private void onOK() {
        // add your code here
        dispose();
    }

    /**
     * choseOtherFolder的监听器
     */
    private void onCancel() {
        // add your code here if necessary
        dispose();
    }
    private void setUI(){
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

        MouseAdapter UIListener = new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        };

        buttonOK.addMouseListener(UIListener);
        choseOtherFolder.addMouseListener(UIListener);
    }
}
