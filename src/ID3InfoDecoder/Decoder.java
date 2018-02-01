package ID3InfoDecoder;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.BitstreamException;
import java.io.*;

/**
 * 用以将Mp3文件的ID3头部提取出来
 */
public class Decoder {
    private String mp3DilePath;//给定MP3文件路径
    private int frameSize;//除去ID3头部的10个字节之外的标签帧大小总和
    private String ID3_TAG;
    private byte[] buf;//缓存除去ID3头部的10个字节之外的标签帧的所有信息

    public Decoder(String mp3DilePath){
        this.mp3DilePath = mp3DilePath;//设定文件路径
        LableHeader ID3Header = new LableHeader(mp3DilePath);
        frameSize = ID3Header.getSize() - 10;
        ID3_TAG = ID3Header.getHearer();
        /*--------------------------------------------------------------------------------------------------------------
        再次打开MP3文件，并且以上面得到的frameSize为大小构造一个字符数组，并将MP3的ID3数据读取进去
        --------------------------------------------------------------------------------------------------------------*/
        try {
            buf = new byte[frameSize];
            RandomAccessFile raf = new RandomAccessFile(mp3DilePath,"r");
            raf.seek(10);//向前移动10个字节，达到第一个标签帧
            raf.read(buf);
            raf.close();
        }catch (IOException ex){}
    }

    /**
     * @return 如果MP3文件头是ID3，则返回true
     */
    public boolean IS_ID3_OR_NOT(){
        if (ID3_TAG.equalsIgnoreCase("ID3")) return true;
        else return false;
    }

    /**
     * @return 返回一个长度为4的字符串数组，依次表示：
     * SongName(ID3标签为：TIT2),歌曲名
     * Artist(ID3标签为：TPE1),歌手
     * Album(ID3标签为：TALB),专辑
     * SongTime(ID3标签为：TIME),歌曲时长
     */
    public String[] getMusicInfo() throws UnsupportedEncodingException {
        String[] MusicInfo = new String[4];
        /*--------------------------------------------------------------------------------------------------------------
        开始解码
        --------------------------------------------------------------------------------------------------------------*/
        int offset = 0;//指向每个标签帧头部的第一个字节
        int count = 0;//计数器，当为4时终止循环，代表以取得全部所需要的MP3的ID3信息

        //Frame代表每个标签帧
        LableFrame Frame = new LableFrame(buf);

        while (count < 3){

            //通过每个标签帧头部第一个字节在字节数组buf的下标offset，初始化每个标签帧
            Frame.setLableFrame(offset);

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
                    //标签帧为“歌曲时长”
                    /*--------------------------------------------------------------------------------------------------------------
                    目前无法通过检索ID3标签的方式得到歌曲时长
                    故通过引入的包中的类来实现
                    --------------------------------------------------------------------------------------------------------------*/
                    //case 4:MusicInfo[3] = new String(buf,offset + 10 + 1,Frame.getSize() - 1,setEncoding(buf[offset + 10]));break;
                }
            }
            offset = offset + 10 + Frame.getSize();
        }
        return MusicInfo;
    }


    /*--------------------------------------------------------------------------------------------------------------
    由于每个标签帧内容的第一个字节都保存着这些内容是以什么方式编码的，故需要根据这个字节来设定解码方式
    --------------------------------------------------------------------------------------------------------------*/
    /**
     * @return 每个标签帧内容的第一个字节,设定解码器根据什么字符集来解码
     * P.S.encoding一定在是1，2，3，4中的一个值
     */
    private String setEncoding(byte encoding){
        if (encoding == 0) return "ISO-8859-1";
        if (encoding == 1) return "UTF-16";
        if (encoding == 2) return "UTF-16BE";
        else return "UTF-8";
    }


    //

    //--------------------------------------------------------------------------------------------------------------

    //目前无法通过检索ID3标签的方式得到歌曲时长

    //故通过引入的包中的类来实现

    //--------------------------------------------------------------------------------------------------------------

    //

    /**
     * @return 根据Decoder给定的MP3的路径，使用jl1.0.1中的软件包得到MP3的时长
     * P.S.但是这种方法会损失精度
     */
    public String getSongTime(){
        String SongTime = null;
        File file = new File(mp3DilePath);
        try{
            FileInputStream fis=new FileInputStream(file);
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

                }catch (BitstreamException ex){}
        }catch (FileNotFoundException ex){
                    ex.printStackTrace();
            }
        }catch (IOException ex){
            ex.printStackTrace();
        }
        return SongTime;
    }
}
