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
    private MenuItem deleteList;//删除

    /**
     * 用户所选择的播放列表
     */
    public MusicList selectedMusicList = null;

    //UI
    private Font font = new Font("楷体",Font.ITALIC + Font.BOLD, 12);

    public static MusicListRightButtonMenu musicListRightButtonMenu;

    private MusicListRightButtonMenu(){
        deleteList = new MenuItem("Delete");

        //设定UI
        deleteList.setFont(font);

        //设定监听器
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

        add(deleteList);
    }
    public static MusicListRightButtonMenu getMusicListRightButtonMenu(){
        return new MusicListRightButtonMenu();
    }
}
