package CloudMusicGUI;

import Implements.Implements;
import com.EnjoyYourMusic;
import com.List.MusicList;
import com.List.MusicNode;
import com.List.PlayMode;
import com.MusicPlayer;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * MusicList的鼠标右键菜单
 */
public class MusicListRightButtonMenu extends PopupMenu{
    private MenuItem play;//播放
    private MenuItem deleteList;//删除

    /**
     * 用户所选择的播放列表
     */
    public MusicList selectedMusicList = null;

    //UI
    private Font font = new Font("楷体",Font.ITALIC + Font.BOLD, 12);

    public static MusicListRightButtonMenu musicListRightButtonMenu;

    private MusicListRightButtonMenu(){
        play = new MenuItem("Play");
        deleteList = new MenuItem("Delete");

        //设定UI
        play.setFont(font);
        deleteList.setFont(font);

        //设定监听器
        //播放用户所选择歌单.采用getNextMusic来播放音乐
        play.addActionListener(e -> {
            //当所选择歌单里有乐曲时才执行播放
            if (selectedMusicList.sum != 0){
                //设定当前播放列表
                MusicPlayer.currentMusicList = selectedMusicList;
                if (CloudMusic.isReplaceCurrentMusicList){
                    //切换歌单时，必须要切换到所选歌单乐曲以播放.否则随机播放时将造成错误
                    MusicPlayer.currentMusicNode = selectedMusicList.getFirstMusic();
                    CloudMusic.isReplaceCurrentMusicList = false;
                }

                try {
                    if (CloudMusicThreadManager.playMusic.isAlive()){
                        //中断当前播放乐曲
                        EnjoyYourMusic.buffer.close();
                    }
                    //当播放线程没有启动时且歌单里有乐曲时，则启动
                    else {
                        //随机播放时，置一个随机值
                        if (MusicPlayer.currentPlayMode == PlayMode.Mode_Random){
                            int cnt = Implements.GetRandomNum(selectedMusicList.sum);
                            if (cnt < 0) cnt *= -1;
                            MusicNode node = selectedMusicList.getFirstMusic();
                            while (cnt > 0){
                                node = node.next;
                                --cnt;
                            }
                            MusicPlayer.currentMusicNode = node;
                        }
                        //否则选取所选列表的第一个值
                        else {
                            //初始化MusicPlayer.currentMusicNode
                            MusicPlayer.currentMusicNode = selectedMusicList.getFirstMusic();
                        }
                        CloudMusicThreadManager.playMusic.start();
                        CloudMusicThreadManager.musicProgressBar.start();
                    }
                } catch (IOException e1) {
                    //do nothing
                }
            }
        });
        //删除用户所选择歌单
        deleteList.addActionListener(e -> {
            //获取被删除的歌单在数组中的下标
            int index = MusicPlayer.TotalMusicListFileName.indexOf(selectedMusicList.toString());
            //当用户正在播放被删除的歌单时,将被删除歌单的前一个置为当前播放歌单.
            //同时当前正在播放的乐曲将被置为上诉歌单的第一首乐曲
            if (MusicPlayer.getIndexOfCurrentMusicList() == index){
                MusicPlayer.currentMusicList = MusicPlayer.TotalMusicList.get(index - 1);
                //不需要管列表是否为空
                MusicPlayer.currentMusicNode = MusicPlayer.currentMusicList.getFirstMusic();
            }
            MusicPlayer.TotalMusicList.remove(index);
            //删除序列化文件
            File file = new File("src\\serFile\\" + MusicPlayer.TotalMusicListFileName.get(index) + ".ser");
            if (file.delete()) --MusicPlayer.sum;
            MusicPlayer.TotalMusicListFileName.remove(index);
            //更新GUI
            SwingUtilities.invokeLater(() -> {
                //更新歌单列表
                CloudMusic.musicListModel.clear();
                for (String str : MusicPlayer.TotalMusicListFileName){
                    CloudMusic.musicListModel.addElement(str);
                }
                //显示被删除列表的上一个乐曲列表
                CloudMusic.currentMusicListModel.clear();
                int cnt = MusicPlayer.currentMusicList.sum;
                if (cnt != 0){
                    MusicNode node = MusicPlayer.currentMusicList.getFirstMusic();
                    while (cnt > 0){
                        CloudMusic.currentMusicListModel.addElement(Implements.renderer(node.toString()));
                        node = node.next;
                        --cnt;
                    }
                }
                //更新控件显示值
                CloudMusic.cloudMusic.musicListName.setText(MusicPlayer.currentMusicList.toString());
            });
        });

        add(play);
    }

    public void initMenu(boolean isDefaultMusicList){
        //当为默认列表时，不显示删除按钮
        if (isDefaultMusicList) remove(deleteList);
        //否则将删除按钮添加上去
        else add(deleteList);
    }

    public static MusicListRightButtonMenu getMusicListRightButtonMenu(){
        return new MusicListRightButtonMenu();
    }
}
