package GUI;

import javax.swing.*;
import java.awt.event.*;

public class tips extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JPanel myPanel;
    private JTextArea textArea;

    /**
     * @param text 对话框现实的文本
     * @param OKText OK按钮的文本.当为null时不显示该按钮
     * @param CancelText Cancel的文本.当为null时不显示该按钮
     * @param buttonOKListener OK按钮的监听器.当为空时采用默认的监听器
     */
    tips(String text, String OKText, String CancelText,ActionListener buttonOKListener) {
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

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        textArea.setText(text);

        if (OKText != null){
            if (buttonOKListener != null){
                buttonOK.addActionListener(buttonOKListener);
            }
            else buttonOK.addActionListener(e -> onOK());
            buttonOK.setText(OKText);
        }
        else {
            myPanel.remove(buttonOK);
        }

        if (CancelText != null){
            buttonCancel.addActionListener(e -> onCancel());
            buttonCancel.setText(CancelText);
        }
        else {
            myPanel.remove(buttonCancel);
        }

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

        //显示对话框
        pack();
        setVisible(true);
    }

    private void onOK() {
        // add your code here
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    //测试
    public static void main(String[] args) {
        tips dialog = new tips("?",null,"haha",null);
    }
}
