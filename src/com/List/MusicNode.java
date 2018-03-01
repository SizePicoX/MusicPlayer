package com.List;

import com.Music.Music;
import java.io.Serializable;

/**
 * 存储Music对象的链表节点，是MusicList的最小单元
 *
 * 同时，MusicNode应当是整个软件范围内的，除了在添加乐曲时候的最小可操纵单元
 */
public class MusicNode implements Serializable{
    public Music music;
    public MusicNode next;
    public MusicNode prior;


    /**
     * 仅在向音乐列表添加乐曲时被调用
     * @param music 通过给定的Music，构造它的节点
     */
     public MusicNode(Music music){
        this.music = music;
        next = null;
        prior = null;
    }

    /**
     * @param selectedNode 将要被删除的节点，使用该方法将节点的指针释放
     */
    public static void DeleteSelectedNode(MusicNode selectedNode){
        selectedNode.prior = null;
        selectedNode.next = null;
        selectedNode.music = null;
    }

    /**
     * @return 该MusicNode的基本信息
     */
    @Override
    public String toString() {
        return music.getSongName() + "," + music.getArtist() + "," +
                music.getAlbum() + "," + music.getSongTime();
    }

    /**
     * 比较两个MusicNode所指向的Music的内容是否完全一样.
     * @param node 待比较的MusicNode
     * @return  当两个MusicNode所指向的Music的内容完全一样时，返回true
     */
    public boolean isSame(MusicNode node){
        return music.isSame(node.music);
    }

    /**
     * @return 当前乐曲的时长.以秒来计算
     */
    public int getSongTime(){
        String[] str = music.getSongTime().split(":");
        return Integer.parseInt(str[0]) * 60 + Integer.parseInt(str[1]);
    }

}
