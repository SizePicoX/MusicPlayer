package CloudMusicGUI;

import com.MusicPlayer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


/**
 * CloudMusic在系统托盘的组件
 */
public class CloudMusicTray {

    private  JPopupMenu menu;//弹出式菜单

    private  JLabel label;//替代方案.目前无法实现当菜单失去focus时自动消失.故替代为当点击label时消失
    public   TrayIcon trayIcon;//系统托盘图标的实例.为了能够动态的修改显示文本
    public   JComboBox<String> playMode;//播放模式.包括顺序播放，随机播放和单曲循环
    public   JComboBox<String> frameMode;//播放器窗体模式.可以是完整模式或者迷你模式
    public   JMenuItem play_pause;//播放，暂停
    private  JMenuItem priorMusic;//上一首
    private  JMenuItem nextMusic;//下一首
    private  JMenuItem exit;//退出

    private Color color = new Color(235, 227, 212);
    private Font font = new Font("楷体",Font.ITALIC + Font.BOLD, 12);

    /**
     * 唯一的系统托盘.使用范例:
     * CloudMusicTray.tray = CloudMusicTray.getCloudMusicTray();
     */
    public static CloudMusicTray tray;

    /**
     * 不允许自建CloudMusicTray对象
     */
    private CloudMusicTray(){
        initTray();
    }
    /**
     * 初始化系统托盘.包括设定托盘图标,图标的右键菜单,以及监听器.
     */
    private void initTray(){
        /* 获取本操作系统的托盘的实例 */
        SystemTray tray = SystemTray.getSystemTray();
        /* 创建一个右键弹出式菜单 */
        menu = new JPopupMenu();

        //初始化label
        label = new JLabel("CloudMusic");
        /* 初始化JComboBox */
        playMode = new JComboBox<>();
        playMode.addItem("顺序播放");
        playMode.addItem("随机播放");
        playMode.addItem("单曲循环");
        // 上一次播放结束时设定的播放模式
        //int index = MusicPlayer.currentPlayMode;
        int index = 0;
        playMode.setSelectedIndex(index);
        /* 初始化JCheckBox.默认为完整模式 */
        frameMode = new JComboBox<>();
        frameMode.addItem("完整模式");
        frameMode.addItem("mini模式");
        /* 初始化其他菜单项 */
        play_pause = new JMenuItem("播放");
        priorMusic = new JMenuItem("上一首");
        nextMusic = new JMenuItem("下一首");
        exit = new JMenuItem("退出");

        /* 按钮的数组 */
        JMenuItem[] items = {play_pause,priorMusic,nextMusic,exit};
        ImageIcon[] icons = {new ImageIcon("src\\icon\\playTrayIcon.png"),new ImageIcon("src\\icon\\priorTrayIcon.png"),
                new ImageIcon("src\\icon\\nextTrayIcon.png"),new ImageIcon("src\\icon\\closeTrayIcon.png")};
        /* 为所有AbstractButton设置UI和高亮 */
        for (int i = 0 ; i < items.length ; ++i){
            JMenuItem item = items[i];
            /* 为所有按钮设置UI以及相应的图标 */
            setUI(item,icons[i]);
            /* 为所有按钮添加鼠标经过的监听器，使得鼠标经过时按钮高亮 */
            setHighLight(item);
        }
        /* 设定JComboBox的UI和高亮 */
        setUI(playMode);
        setUI(frameMode);
        setHighLight(playMode);
        setHighLight(frameMode);
        /* 替代方案的UI */
        label.setBackground(color);
        label.setFont(font);
        /* 替代方案的监听器 */
        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                menu.setVisible(false);
            }
        });
        /* 菜单项的鼠标点击事件的监听器 */
        setMouseClickListener();
        /* 将组件添加进菜单里面 */

        menu.add(label);//替代方案
        menu.add(frameMode);//窗体模式
        menu.add(playMode);//播放模式
        menu.add(play_pause);//播放或者暂停
        menu.add(priorMusic);//上一首
        menu.add(nextMusic);//下一首
        menu.add(exit);//退出

        menu.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }
        });

        //设置系统托盘图标
        String tooltip = MusicPlayer.getCurrentMusicInfo();
        ImageIcon icon = new ImageIcon("src\\icon\\formatTrayIcon.png");
        trayIcon = new TrayIcon(icon.getImage(), tooltip);
        /* 托盘图标监听器 */
        trayIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1){
                    /* 当鼠标左键点击1次时才将cloudMusic显示出来 */
                    if (e.getClickCount() == 1){
                        MiniCloudMusic.miniCloudMusic.setVisible(false);
                        if (!CloudMusic.cloudMusic.isVisible()){
                            CloudMusic.cloudMusic.setVisible(true);
                            frameMode.setSelectedIndex(0);
                        }
                    }
                }
                /* 当鼠标右键点击时,展开菜单栏 */
                if (e.getButton() == MouseEvent.BUTTON3){
                    /* 得到鼠标点击时的位置 */
                    Point point = MouseInfo.getPointerInfo().getLocation();
                    menu.setLocation(point);
                    menu.setVisible(true);
                }
            }
        });

        /* 将CloudMusic添加到系统托盘 */
        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }
    /**为JMenuItem设定UI和对应的图标
     * @param item 选定的组件
     */
    private void setUI(JMenuItem item,ImageIcon icon){
        /* 设定按钮图标 */
        item.setIcon(icon);
        /* 设定按钮背景 */
        item.setBackground(color);
        /* 设定按钮字体 */
        item.setFont(font);
        /* 设定按钮边框 */
        item.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 5));
    }
    /**
     * 为JComboBox设置UI
     * @param comboBox 选定的组件
     */
    private void setUI(JComboBox comboBox){
        comboBox.setBackground(color);
        comboBox.setFont(font);
    }
    /**为JMenuItem设定监听器，使得鼠标经过时按钮高亮
     * @param item 选定的组件
     */
    private void setHighLight(JMenuItem item){
        item.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                item.setBackground(Color.lightGray);
            }
            /* 鼠标离开时恢复原状态 */
            @Override
            public void mouseExited(MouseEvent e) {
                item.setBackground(color);
            }
        });
    }
    /**
     * 为JComboBox设置高亮
     * @param comboBox 选定的组件
     */
    private void setHighLight(JComboBox comboBox){
        comboBox.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                comboBox.setBackground(Color.lightGray);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                comboBox.setBackground(color);
            }
        });
    }
    /**
     * 为JMenuItem设定监听器，使之处理对应的点击时间
     */
    private void setMouseClickListener(){
        frameMode.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED){
                    /* 当选择完整模式时 */
                    if (frameMode.getSelectedIndex() == 0){
                        if (!CloudMusic.cloudMusic.isVisible()){
                            MiniCloudMusic.miniCloudMusic.setVisible(false);
                            CloudMusic.cloudMusic.setVisible(true);
                            menu.setVisible(false);
                        }
                    }
                    /* 当选择mini模式时 */
                    else {
                        if (!MiniCloudMusic.miniCloudMusic.isVisible()){
                            MiniCloudMusic.miniCloudMusic.setVisible(true);
                            CloudMusic.cloudMusic.setVisible(false);
                            menu.setVisible(false);
                        }
                    }
            }
        });
        playMode.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED){
                /* 选择顺序播放时 */
                if (playMode.getSelectedIndex() == 0 && MusicPlayer.currentPlayMode != 0){
                    MusicPlayer.currentPlayMode = 0;
                    CloudMusic.cloudMusic.playMode.setSelectedIndex(0);
                }
                /* 选择随机播放时 */
                else if (playMode.getSelectedIndex() == 1 && MusicPlayer.currentPlayMode != 1){
                    MusicPlayer.currentPlayMode = 1;
                    CloudMusic.cloudMusic.playMode.setSelectedIndex(1);
                }
                /* 选择单曲循环时 */
                else if (playMode.getSelectedIndex() == 2 && MusicPlayer.currentPlayMode != 2){
                    MusicPlayer.currentPlayMode = 2;
                    CloudMusic.cloudMusic.playMode.setSelectedIndex(2);
                }
            }
            menu.setVisible(false);
        });
        /* 播放暂停按钮 */
        play_pause.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                /* 当鼠标左键点击时才视为事件 */
                if (e.getButton() == MouseEvent.BUTTON1){
                    CloudMusic.cloudMusic.SET_PLAY_OR_PAUSE();
                    play_pause.setBackground(color);
                    menu.setVisible(false);
                }
            }
        });
        priorMusic.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1){
                    priorMusic.setBackground(color);
                    menu.setVisible(false);
                    CloudMusic.cloudMusic.playPriorMusic();
                }
            }
        });
        nextMusic.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1){
                    nextMusic.setBackground(color);
                    menu.setVisible(false);
                    CloudMusic.cloudMusic.playNextMusic();
                }
            }
        });
        exit.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1){
                    // TODO: 2018/2/16 MusicPlayer的Save方法
                    //MusicPlayer.Save();
                    System.exit(0);
                }
            }
        });
    }
    public static CloudMusicTray getCloudMusicTray(){
        return new CloudMusicTray();
    }
}
