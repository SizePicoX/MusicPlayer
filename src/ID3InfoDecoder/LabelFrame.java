package ID3InfoDecoder;
import Implements.Implements;
import java.io.UnsupportedEncodingException;

/**
 * 用于存放MP3文件中每个标签帧的头部
 * 使用于Music中
 */
public class LabelFrame {
    private String FrameHeader;//标签帧头部，4字节，用于表示此标签内容是什么
    private int Size;//标签帧内容大小，4字节，不包括标签头，不小于1
    private int Flag;//存放标志，2字节，只定义了6 位，没什么用直接置0

    private byte[] buf;//待处理的存放所有标签帧的缓冲区

    LabelFrame(byte[] buf){
        this.buf = buf;
    }
    /**
     * @param offset 每个标签帧头部第一个字节在字节数组buf的下标
     * @throws UnsupportedEncodingException
     */
    public void setLableFrame(int offset) throws UnsupportedEncodingException{
        FrameHeader = new String(buf,offset,4,"utf-8");

        /*--------------------------------------------------------------------------------------------------------------
        计算Size的算法不能直接是如下形式：
                Size = buf[offset+4]*0X10000000 + buf[offset+5]*0X10000+ buf[offset+6]*0X100 + buf[offset+7];
        由于数字在内存中是补码的形式，故对于每一个字节，必须通过按位与运算得到 其表示的无符号数的值，如，
        179在内存中是 1011 0011 ，而这是补码，如果表示有符号数，则表示的是-77，但是我们需要无符号数，故对于每一字节都需要转换
         --------------------------------------------------------------------------------------------------------------*/

        Size = Implements.SignedToUnsigned(buf[offset+4]) * 0X10000000 + Implements.SignedToUnsigned(buf[offset+5]) * 0X10000 +
                Implements.SignedToUnsigned(buf[offset+6]) * 0X100 + Implements.SignedToUnsigned(buf[offset+7]);
        Flag = 0;
    }


    public String getFrameHeader() {
        return FrameHeader;
    }

    public int getSize() {
        return Size;
    }

    public int getFlag() {
        return Flag;
    }

    /**
     * @return 判断当前标签帧是什么类型，如果这条标签帧是TIT2(乐曲名字)，TPE1(歌手)，TALB(专辑),TIME(歌曲时长),则分别返回1,2,3,4
     * 否则返回-1
     */

    int judge(){
        if (FrameHeader.equalsIgnoreCase("TIT2")) return 1;//标签帧为“歌曲名字”
        else if (FrameHeader.equalsIgnoreCase("TPE1")) return 2;//标签帧为“歌手”
        else if (FrameHeader.equalsIgnoreCase("TALB")) return 3;//标签帧为“专辑”
        else if (FrameHeader.equalsIgnoreCase("TIME")) return 4;//标签帧为“歌曲时长”
        else return -1;
    }
}
