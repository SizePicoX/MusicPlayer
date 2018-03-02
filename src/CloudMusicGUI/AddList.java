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

    public  String songListName = null;//输入的歌单名字

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
        while (true){
            //将新添加的歌单添加到GUI中以显示
            //不允许歌单重名
            //获取新建的歌单名字
            String name = textArea.getText();
            //歌单不为空时，扫描歌单检测是否同名
            if (!CloudMusic.musicListModel.isEmpty()){
                int i = 0;
                for (; i < CloudMusic.musicListModel.getSize() ; ++i){
                    if (name.equalsIgnoreCase(CloudMusic.musicListModel.get(i))) break;
                }
                if (i == CloudMusic.musicListModel.getSize()){
                    songListName = name;
                    break;
                }
            }
            //歌单为空时直接添加
            else {
                songListName = name;
                break;
            }
        }
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
        ImageIcon icon = new ImageIcon("D:\\JAVA CODE\\MusicPlayer\\icon\\tipsIcon.png");
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
