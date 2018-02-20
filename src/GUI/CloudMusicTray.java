package GUI;

import Error.InitException;
import com.EnjoyYourMusic;
import com.List.MusicNode;
import com.MusicPlayer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


/**
 * CloudMusic在系统托盘的组件
 */
public class CloudMusicTray {

    private  JPopupMenu menu;//弹出式菜单
    private  JFrame currentFrame;//当前显示的Frame

    private  JLabel label;//替代方案.目前无法实现当菜单失去focus时自动消失.故替代为当点击label时消失
    private  MyComboBox playMode;//播放模式.包括顺序播放，随机播放和单曲循环
    private  MyComboBox frameMode;//播放器窗体模式.可以是完整模式或者迷你模式
    private  MyMenuItem play_pause;//播放，暂停
    private  MyMenuItem priorMusic;//上一首
    private  MyMenuItem nextMusic;//下一首
    private  MyMenuItem exit;//退出

    private Color color = new Color(235, 227, 212);
    private Font font = new Font("楷体",Font.ITALIC + Font.BOLD, 12);

    /**
     * 创建系统托盘
     */
    CloudMusicTray(){
        try {
            initTray();
        }catch (InitException ex){
            /* 弹出对话框以提示不支持系统托盘 */
            new tips("本操作系统不支持系统托盘.","好的",null,null);
        }
    }
    /**设定当前鼠标点击托盘图标时将要显示的界面
     * 当Frame被显示和被关闭时都必须调用
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
        /* 当操作系统支持系统托盘时 */
        if (SystemTray.isSupported()){
            /* 创建一个右键弹出式菜单 */
            menu = new JPopupMenu();

            //初始化label
            label = new JLabel("CloudMusic");
            /* 初始化JComboBox */
            playMode = new MyComboBox();
            playMode.addItem("顺序播放");
            playMode.addItem("随机播放");
            playMode.addItem("单曲循环");
            // 上一次播放结束时设定的播放模式
            //int index = MusicPlayer.currentPlayMode;
            int index = 0;
            playMode.setSelectedIndex(index);
            /* 初始化JCheckBox.默认为完整模式 */
            frameMode = new MyComboBox();
            frameMode.addItem("完整模式");
            frameMode.addItem("mini模式");
            /* 初始化其他菜单项 */
            play_pause = new MyMenuItem("播放");
            priorMusic = new MyMenuItem("上一首");
            nextMusic = new MyMenuItem("下一首");
            exit = new MyMenuItem("退出");

            /* 按钮的数组 */
            MyMenuItem[] items = {play_pause,priorMusic,nextMusic,exit};
            ImageIcon[] icons = {new ImageIcon("src\\icon\\playTrayIcon.png"),new ImageIcon("src\\icon\\priorTrayIcon.png"),
                    new ImageIcon("src\\icon\\nextTrayIcon.png"),new ImageIcon("src\\icon\\closeTrayIcon.png")};
            /* 为所有AbstractButton设置UI和高亮 */
            for (int i = 0 ; i < items.length ; ++i){
                MyMenuItem item = items[i];
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
                    System.out.println(1);
                }
            });

            //设置系统托盘图标
            // TODO: 2018/2/15  设定CloudMusic的系统托盘图标提示
            //String tooltip = MusicPlayer.getCurrentMusicInfo();//获取当前播放乐曲信息.默认返回"demo"
            ImageIcon icon = new ImageIcon("src\\icon\\formatTrayIcon.png");
            TrayIcon trayIcon = new TrayIcon(icon.getImage(), "CloudMusic");
            /* 托盘图标监听器 */
            trayIcon.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getButton() == MouseEvent.BUTTON1){
                        /* 当鼠标左键点击1次时才将当前界面currentFrame显示出来 */
                        if (e.getClickCount() == 1){
                            // TODO: 2018/2/19 解决该问题的方法就是，当某一个Frame被显示和被关闭时，都调用setCurrentFrame
                            if (!currentFrame.isVisible()){
                                currentFrame.setExtendedState(JFrame.NORMAL);
                                currentFrame.setVisible(true);
                            }
                        }
                        /* 当鼠标左键点击2次时，显示主界面 */
                        else if (e.getClickCount() == 2){
                            if (!CloudMusic.cloudMusic.isVisible()){
                                MiniCloudMusic.miniCloudMusic.setVisible(false);
                                CloudMusic.cloudMusic.setVisible(true);
                            }
                        }
                    }
                    /* 当鼠标右键点击时,展开菜单栏 */
                    if (e.getButton() == MouseEvent.BUTTON3){
                        /* 将所有组件的flag全部置为true */
//                        playMode.flag = true;
//                        frameMode.flag = true;
//                        play_pause.flag = true;
//                        priorMusic.flag = true;
//                        nextMusic.flag = true;
//                        exit.flag = true;
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
        else {
            throw new InitException("该操作系统不支持系统托盘！", InitException.Tray_Cannot_Supported);
        }
    }
    /**为JMenuItem设定UI和对应的图标
     * @param item 选定的组件
     */
    private void setUI(MyMenuItem item,ImageIcon icon){
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
    private void setUI(MyComboBox comboBox){
        comboBox.setBackground(color);
        comboBox.setFont(font);
    }
    /**为JMenuItem设定监听器，使得鼠标经过时按钮高亮
     * @param item 选定的组件
     */
    private void setHighLight(MyMenuItem item){
        item.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                //item.flag = true;
                item.setBackground(Color.lightGray);
            }
            /* 鼠标离开时恢复原状态 */
            @Override
            public void mouseExited(MouseEvent e) {
                //item.flag = false;
                item.setBackground(color);
//                //当所有的按钮都被退出时
//                if (!(playMode.flag || frameMode.flag || play_pause.flag || priorMusic.flag || nextMusic.flag || exit.flag)){
//                    menu.setVisible(false);
//                }
            }
        });
    }
    /**
     * 为JComboBox设置高亮
     * @param comboBox 选定的组件
     */
    private void setHighLight(MyComboBox comboBox){
        comboBox.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                //comboBox.flag = true;
                comboBox.setBackground(Color.lightGray);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                //comboBox.flag = false;
                comboBox.setBackground(color);
//                //当所有的按钮都被退出时
//                if (!(playMode.flag || frameMode.flag || play_pause.flag || priorMusic.flag || nextMusic.flag || exit.flag)){
//                    menu.setVisible(false);
//                }
            }
        });
    }
    /**
     * 为JMenuItem设定监听器，使之处理对应的点击时间
     */
    private void setMouseClickListener(){
        frameMode.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED){
//                    /* 当选择完整模式时 */
//                    if (frameMode.getSelectedIndex() == 0){
//                        if (!demo.CloudMusic.isVisible()){
//                            MiniCloudMusic.miniCloudMusic.setVisible(false);
//                            demo.CloudMusic.setVisible(true);
//                            setCurrentFrame(demo.CloudMusic);
//                            menu.setVisible(false);
//                        }
//                    }
//                    /* 当选择mini模式时 */
//                    else {
//                        if (!MiniCloudMusic.miniCloudMusic.isVisible()){
//                            MiniCloudMusic.miniCloudMusic.setVisible(true);
//                            demo.CloudMusic.setVisible(false);
//                            setCurrentFrame(MiniCloudMusic.miniCloudMusic);
//                            menu.setVisible(false);
//                        }
//                    }
            }
        });
        playMode.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED){
//                        /* 选择顺序播放时 */
//                    if (playMode.getSelectedIndex() == 0 && MusicPlayer.currentPlayMode != 0){
//                        MusicPlayer.currentPlayMode = 0;
//                        menu.setVisible(false);
//                    }
//                        /* 选择随机播放时 */
//                    else if (playMode.getSelectedIndex() == 1 && MusicPlayer.currentPlayMode != 1){
//                        MusicPlayer.currentPlayMode = 1;
//                        menu.setVisible(false);
//                    }
//                        /* 选择单曲循环时 */
//                    else if (playMode.getSelectedIndex() == 2 && MusicPlayer.currentPlayMode != 2){
//                        MusicPlayer.currentPlayMode = 2;
//                        menu.setVisible(false);
//                    }
            }
        });
        /* 播放暂停按钮 */
        play_pause.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                /* 当鼠标左键点击时才视为事件 */
                if (e.getButton() == MouseEvent.BUTTON1){
                    //播放
                    if (!play_pause.isSelected()){
                        // TODO: 2018/2/17 这里存在问题.当播放器初始化时和放了一会然后暂停然后又继续播放时怎么办?
                        // TODO: 2018/2/17 当然，这不是GUI要去处理的事情
                        System.out.println(-1);
                        MusicNode currentMusicNode = MusicPlayer.currentMusicNode;
                        if (currentMusicNode.music.getMp3FilePath() != null){
                            // TODO: 2018/2/18 同时，play方法在无法播放时候报错或者放别的歌都可以
                            EnjoyYourMusic.play(currentMusicNode);
                            play_pause.setIcon(new ImageIcon("src\\icon\\pauseTrayIcon.png"));
                            play_pause.setText("暂停");
                            play_pause.setSelected(true);
                        }
                    }
                    //暂停
                    else {
                        System.out.println(0);
                        MusicPlayer.stop();
                        play_pause.setIcon(new ImageIcon("src\\icon\\playTrayIcon.png"));
                        play_pause.setText("播放");
                        play_pause.setSelected(false);
                    }
                    play_pause.setBackground(color);
                    menu.setVisible(false);
                }
            }
        });
        priorMusic.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1){
                    System.out.println(1);
                    priorMusic.setBackground(color);
                    menu.setVisible(false);
                }
            }
        });
        nextMusic.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1){
                    System.out.println(2);
                    nextMusic.setBackground(color);
                    menu.setVisible(false);
                }
            }
        });
        exit.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1){
                    // TODO: 2018/2/16 MusicPlayer的Save方法
                    //MusicPlayer.Save();
                    // TODO: 2018/2/16 这种方法真的能终止所有线程吗？
                    System.exit(0);
                }
            }
        });
    }
}
