package GUI;

import com.List.PlayMode;
import com.MusicPlayer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


/**
 * CloudMusic在系统托盘的组件
 */
public class CloudMusicTray {

    private  JPopupMenu menu;//弹出式菜单
    private  JFrame currentFrame;//当前显示的Frame

    private  JMenuItem play_pause;//播放或者暂停
    private  JMenuItem priorMusic;//上一首
    private  JMenuItem nextMusic;//下一首
    private  JMenuItem frameMode;//窗体模式.包括mini模式和完整模式u
    private  JMenuItem miniMode;//mini模式
    private  JMenuItem enlargeMode;//完整模式
    private  JMenuItem playMode;//播放模式.包括顺序,随机,单曲循环.
    private  JMenuItem Mode_Sequential;//顺序播放
    private  JMenuItem Mode_Random;//随机播放
    private  JMenuItem Mode_Loop;//单曲循环
    private  JMenuItem exit;//退出
    /**
     * 创建系统托盘
     */
    CloudMusicTray(){
        initTray();
    }
    /**设定当前鼠标点击托盘图标时将要显示的界面
     * @param currentFrame MiniCloudMusic或者CloudMusic
     */
    public void setCurrentFrame(JFrame currentFrame){
        this.currentFrame = currentFrame;
    }
    /**
     * 初始化系统托盘.包括设定托盘图标,图标的右键菜单,以及监听器.
     */
    private void initTray(){
        /* 获取本操作系统的托盘的实例 */
        SystemTray tray = SystemTray.getSystemTray();
        /* 创建一个右键弹出式菜单 */
        menu = new JPopupMenu();

        // TODO: 2018/2/15  /* 设定菜单UI */
        MyMenuUI menuUI = new MyMenuUI();
        menu.setUI(menuUI);

        // TODO: 2018/2/17 按钮部分需要修改.尽量做跟网易云一样.
        play_pause = new JMenuItem("播放");
        priorMusic = new JMenuItem("上一首");
        nextMusic = new JMenuItem("下一首");
        frameMode = new JMenuItem("完整模式");
        /* miniMode和enlargeMode都是鼠标停留在frameMode上时才显示的 */
        miniMode = new JMenuItem("迷你模式");
        enlargeMode = new JMenuItem("完整模式");
        /*
        playMode的初始化文本需要根据MusicPlayer的初始化方法得到的上一次播放模式以确定.
        为了测试，暂时直接设定为顺序播放
        */
//        String playModeText = "顺序播放";
//        int currentPlayMode = MusicPlayer.currentPlayMode;
//        if (currentPlayMode == PlayMode.Mode_Sequential) playModeText = "顺序播放";
//        else if (currentPlayMode == PlayMode.Mode_Random) playModeText = "随机播放";
//        else playModeText = "单曲循环";
//        playMode = new JMenuItem(playModeText);
        playMode = new JMenuItem("顺序播放");
        Mode_Sequential = new JMenuItem("顺序播放");
        Mode_Random = new JMenuItem("随机播放");
        Mode_Loop = new JMenuItem("单曲循环");
        exit = new JMenuItem("退出");

        /* 为所有按钮设置UI以及相应的图标 */
        setUI(priorMusic,new ImageIcon("src\\icon\\priorTrayIcon.png"));
        setUI(nextMusic,new ImageIcon("src\\icon\\nextTrayIcon.png"));
        setUI(miniMode,new ImageIcon("src\\icon\\miniTrayIcon.png"));
        setUI(enlargeMode,new ImageIcon("src\\icon\\enlargeTrayIcon.png"));
        setUI(exit,new ImageIcon("src\\icon\\closeTrayIcon.png"));

        /* 为所有按钮添加鼠标经过的监听器，使得鼠标经过时按钮高亮 */
        setHighLight(priorMusic);
        setHighLight(nextMusic);
        setHighLight(miniMode);
        setHighLight(enlargeMode);
        setHighLight(exit);

        /* 菜单项的鼠标点击事件的监听器 */
        setMouseClickListener();

        /* 将组件添加进菜单里面 */
        menu.add(priorMusic);
        menu.add(nextMusic);
        menu.add(miniMode);
        menu.add(enlargeMode);
        menu.add(exit);

        //设置系统托盘图标
        // TODO: 2018/2/15  设定CloudMusic的系统托盘图标提示
        //String tooltip = MusicPlayer.getCurrentMusicInfo();//获取当前播放乐曲信息.默认返回"CloudMusic"
        ImageIcon icon = new ImageIcon("src\\icon\\formatTrayIcon.png");
        TrayIcon trayIcon = new TrayIcon(icon.getImage(), "CloudMusic");
        /* 将CloudMusic添加到系统托盘 */
        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            e.printStackTrace();
        }

        /* 点击托盘图标时 */
        trayIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                /* 当鼠标左键点击时才将当前界面currentFrame显示出来 */
                if (e.getButton() == MouseEvent.BUTTON1){
                    currentFrame.setExtendedState(JFrame.NORMAL);
                    currentFrame.setVisible(true);
                }
                /* 当鼠标右键点击时,展开菜单栏 */
                if (e.getButton() == MouseEvent.BUTTON3){
                    Point point = MouseInfo.getPointerInfo().getLocation();
                    menu.setLocation(point);
                    menu.setVisible(true);
                }
            }
        });
    }
    /**为JMenuItem设定UI和对应的图标
     * @param item 选定的组件
     */
    private void setUI(JMenuItem item,ImageIcon icon){
        /* 设定按钮图标 */
        item.setIcon(icon);
        /* 设定按钮背景 */
        item.setBackground(new Color(235, 227, 212));
        /* 设定按钮1字体 */
        item.setFont(new Font("楷体",Font.ITALIC + Font.BOLD, 12));
        /* 设定按钮边框 */
        item.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 5));
    }
    /**为JMenuItem设定监听器，使得鼠标经过时按钮高亮
     * @param item 选定的组件
     */
    private void setHighLight(JMenuItem item){
        item.addMouseListener(new MouseAdapter() {
            /* 鼠标进入时狗啊辆 */
            @Override
            public void mouseEntered(MouseEvent e) {
                item.setBackground(Color.lightGray);
            }
            /* 鼠标离开时恢复原状态 */
            @Override
            public void mouseExited(MouseEvent e) {
                item.setBackground(new Color(235, 227, 212));
            }

        });
    }
    /**
     * 为JMenuItem设定监听器，使之处理对应的点击时间
     */
    private void setMouseClickListener(){
        priorMusic.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println(1);
                priorMusic.setBackground(new Color(235, 227, 212));
                menu.setVisible(false);
            }
        });
        nextMusic.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println(2);
                nextMusic.setBackground(new Color(235, 227, 212));
                menu.setVisible(false);
            }
        });
        miniMode.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println(3);
                miniMode.setBackground(new Color(235, 227, 212));
                menu.setVisible(false);
            }
        });
        enlargeMode.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println(4);
                enlargeMode.setBackground(new Color(235, 227, 212));
                menu.setVisible(false);
            }
        });
        exit.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // TODO: 2018/2/16 MusicPlayer的Save方法
                //MusicPlayer.Save();
                // TODO: 2018/2/16 这种方法真的能终止所有线程吗？
                System.exit(0);
            }
        });
    }
}
