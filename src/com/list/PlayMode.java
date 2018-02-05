package com.list;

/**
 * 可选的播放模式和随机播放时指针一次最多跳转的偏移量
 */
public interface PlayMode {

    /* 顺序播放,该模式下不需要生成额外的播放列表，直接调用MusicList */
    /* 由于是循环双链表，故顺序播放等价于“列表循环” */
    int Mode_Sequential = 1;

    /* 随机播放，该模式下每次点击下一首或者上一首就生成一个随机数作为偏移量并且记录下来，然后在运行期间不变 */
    int Mode_Random = 2;

    /*
    单曲循环,该模式下也不需要生成额外的播放列表,用户点击下一首按钮返回的就是MusicList中的下一首歌,
    只不过如果用户不点击，那么currentMusicNode将一直保持不变
    */
    int Mode_Loop = 3;

}
