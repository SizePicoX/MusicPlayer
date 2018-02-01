package com.list;

import com.Music.Music;

/**
 * 存储Music对象的链表节点，是MusicList的最小单元
 *
 * 同时，MusicNode应当是整个软件范围内的，除了在添加乐曲时候的最小可操纵单元
 */
public class MusicNode {
    public Music music;
    public MusicNode next;
    public MusicNode prior;


    /**
     * @param music 通过给定的Music，构造它的节点
     */
    MusicNode(Music music){
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
}
