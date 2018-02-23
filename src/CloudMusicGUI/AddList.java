package CloudMusicGUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class AddList extends JDialog {
    private JPanel contentPane;
    private JTextArea textArea;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JPanel buttonPanel;
    private JLabel label;

    /**
     * 标记textArea里面是否有有效数据
     */
    private static boolean isValueEffective = false;

    AddList(Point point) {

        setUI();

        setTitle("新建歌单");

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
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

        setLocation(point);
    }

    /**
     * buttonOK的监听器
     */
    private void onOK() {
//        MusicList newList = new MusicList();
//        newList.setInitFileName(textArea.getText());
        //将新添加的歌单添加到GUI中以显示

        dispose();
    }

    /**
     * buttonCancel的监听器
     */
    private void onCancel() {
        dispose();
    }

    private void setUI(){
        /* 设置图标 */
        ImageIcon icon = new ImageIcon("src\\icon\\tipsIcon.png");
        setIconImage(icon.getImage());

        MouseAdapter listener = new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        };
        buttonOK.addMouseListener(listener);
        buttonCancel.addMouseListener(listener);
    }
}
