package ID3InfoDecoder;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.BitstreamException;
import java.io.*;

/**
 * 用以将Mp3文件的ID3头部提取出来
 */
public class Decoder {
    /**
     * 给定MP3文件路径
     */
    private String mp3FilePath;
    /**
     * 除去ID3头部的10个字节之外的标签帧大小总和
     */
    private int frameSize;
    /**
     * 正常情况下应当是"ID3"
     */
    private String ID3_TAG;
    /**
     * 缓存除去ID3头部的10个字节之外的标签帧的所有信息
     */
    private byte[] buf;

    /**
     * 修改位，当调用构造函数由于任何原因失败时值为false
     */
    private boolean isModify = false;


    /**
     * @return 根据Decoder给定的MP3的路径，使用jl1.0.1中的软件包得到MP3的时长
     * P.S.但是这种方法会损失精度
     */
    private String getSongTime(){
        String SongTime = null;
        File file = new File(mp3FilePath);
        try{
            FileInputStream fis = new FileInputStream(file);
            try {
                int b=fis.available();
                Bitstream bt=new Bitstream(fis);
                try {
                    Header h = bt.readFrame();
                    int time = (int) h.total_ms(b);
                    int i = time/1000;

                    /*--------------------------------------------------------------------------------------------------------------
                    实际是不应该直接返回时长的字符串，应该返回秒数或者分数和秒数分开返回
                    到时候需要做修改
                    --------------------------------------------------------------------------------------------------------------*/
                    SongTime = String.valueOf(i / 60) + ":" + String.valueOf(i % 60);

                }catch (BitstreamException ex){
                    ex.printStackTrace();
                }
            }catch (FileNotFoundException ex){
                ex.printStackTrace();
            }
        }catch (IOException ex){
            ex.printStackTrace();
        }
        return SongTime;
    }



    /**由于每个标签帧内容的第一个字节都保存着这些内容是以什么方式编码的，故需要根据这个字节来设定解码方式
     * @return 每个标签帧内容的第一个字节,设定解码器根据什么字符集来解码
     * P.S.encoding一定在是1，2，3，4中的一个值
     */
    private String setEncoding(byte encoding){
        if (encoding == 0) return "ISO-8859-1";
        if (encoding == 1) return "UTF-16";
        if (encoding == 2) return "UTF-16BE";
        else return "UTF-8";
    }


    /**
     * 初始化解码器
     * @param mp3FilePath 给定MP3文件路径
     */
    public Decoder(String mp3FilePath){
        /* 设定文件路径 */
        this.mp3FilePath = mp3FilePath;

        /* 用于获取文件的ID3头部 */
        LabelHeader ID3Header = new LabelHeader(mp3FilePath);

        //当LabelHeader失败时，直接返回，本次解码失败
        if (!ID3Header.isModify()){
            return;
        }

        //获取除去ID3头部的10个字节之外的标签帧大小总和
        frameSize = ID3Header.getSize() - 10;
        //获取ID3头部标签
        ID3_TAG = ID3Header.getHearer();

        /* 再次打开MP3文件，并且以上面得到的frameSize为大小构造一个字符数组，并将MP3的ID3数据读取进去 */
        try {
            buf = new byte[frameSize];
            RandomAccessFile raf = new RandomAccessFile(mp3FilePath,"r");
            raf.seek(10);//向前移动10个字节，达到第一个标签帧
            raf.read(buf);
            raf.close();
            //此时构造函数成功
            isModify = true;
        }catch (IOException ex){
            //打印程序出错的位置及原因
            ex.printStackTrace();
        }
    }

    /**
     * @return 如果MP3文件头是ID3，则返回true
     */
    public boolean IS_ID3_OR_NOT(){
        return ID3_TAG.equalsIgnoreCase("ID3");
    }

    /**
     * 解码器的核心，用于获取MP3文件的主要信息
     * @return 返回一个长度为 5 的字符串数组，依次表示：
     * SongName(ID3标签为：TIT2),歌曲名
     * Artist(ID3标签为：TPE1),歌手
     * Album(ID3标签为：TALB),专辑
     * SongTime(ID3标签为：TIME),歌曲时长
     * ID3头部文件长度
     * MP3文件路径
     */
    public String[] getMusicInfo() throws UnsupportedEncodingException {
        String[] MusicInfo = new String[6];
        /*--------------------------------------------------------------------------------------------------------------
        开始解码
        --------------------------------------------------------------------------------------------------------------*/
        int offset = 0;//指向每个标签帧头部的第一个字节
        int count = 0;//计数器，当为4时终止循环，代表以取得全部所需要的MP3的ID3信息

        //Frame代表每个标签帧
        LabelFrame Frame = new LabelFrame(buf);

        while (count < 3){

            //通过每个标签帧头部第一个字节在字节数组buf的下标offset，初始化每个标签帧
            Frame.setLabelFrame(offset);

            int sign = Frame.judge();//记录此时offset所指向的标签帧的类型
            if (sign != -1){
                ++count;//每当自增1，说明得到了一个需要的值
                switch (sign){
                    //标签帧为“歌曲名字”
                    case 1:MusicInfo[0] = new String(buf,offset + 10 + 1,Frame.getSize() - 1,setEncoding(buf[offset + 10]));break;
                    //标签帧为“歌手”
                    case 2:MusicInfo[1] = new String(buf,offset + 10 + 1,Frame.getSize() - 1,setEncoding(buf[offset + 10]));break;
                    //标签帧为“专辑”
                    case 3:MusicInfo[2] = new String(buf,offset + 10 + 1,Frame.getSize() - 1,setEncoding(buf[offset + 10]));break;
                    /*--------------------------------------------------------------------------------------------------------------

                    --------------------------------------------------------------------------------------------------------------*/
                    //case 4:MusicInfo[3] = new String(buf,offset + 10 + 1,Frame.getSize() - 1,setEncoding(buf[offset + 10]));break;
                }
            }
            offset = offset + 10 + Frame.getSize();
        }
        /*
        获取歌曲时间
        目前无法通过检索ID3标签的方式得到歌曲时长
        故通过引入的包中的类来实现
        */
        MusicInfo[3] = getSongTime();

        //此时解码成功，
        //MusicInfo的5号元素置为当前MP3文件路径
        MusicInfo[4] = mp3FilePath;
        MusicInfo[5] = Integer.toString(frameSize + 10);
        return MusicInfo;
    }






    //--------------------------------------------------------------------------------------------------------------

    //目前无法通过检索ID3标签的方式得到歌曲时长

    //故通过引入的包中的类来实现

    //--------------------------------------------------------------------------------------------------------------


    /**
     * @return  如果本次Decoder构造函数调用成功，则返回true
     */
    public boolean isModify() {
        return isModify;
    }
}
