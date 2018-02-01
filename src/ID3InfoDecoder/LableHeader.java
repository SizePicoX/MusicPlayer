package ID3InfoDecoder;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;


/**
 * 存放MP3文件的最开始10个字节的ID3头部
 */
public class LableHeader {
    private String Hearer;//MP3文件标签头，3字节// /*必须是ID3，否则认为不存在*/
    private int Version;//MP3文件所使用的ID3的版本号，1字节，如果是ID3V2.3 就记录3// /*一般为3*/
    private int Revision;//副版本号，1字节// /*一般为0*/
    private int Flag;//存放标签,仅使用3位，一个字节，一般都为0
    private int Size;//标签大小，4字节，包括标签头的10个字节和所有的标签帧的大小

    /**
     * @param mp3FilePath
     * 用以获取MP3文件的ID3头部
     */
    public LableHeader(String mp3FilePath){
        byte []buf = new byte[10];
        try{
            RandomAccessFile raf = new RandomAccessFile(mp3FilePath,"r");
            raf.read(buf);
            raf.close();
        }catch (IOException ex){}
        try {
            Hearer = new String(buf,0,3,"utf-8");
            Version = buf[3];
            Revision = buf[4];
            Flag = buf[5];
            //计算标签大小
            /*
            根据ID3标签的标准，通过按位与运算得到下面的算法
             */
            Size = (buf[6] & 0X7F) * 0x200000 + (buf[7] & 0X7F) * 0x400 + (buf[8] & 0X7F) * 0X80 +(buf[9] & 0X7F);
        }catch (UnsupportedEncodingException ex){}
    }

    /**
     * @return 返回所给定的MP3文件的最开始的3个字节以utf-8的编码方式得到的字符串
     */
    public String getHearer() {
        return Hearer;
    }

    public int getVersion() {
        return Version;
    }

    public int getRevision() {
        return Revision;
    }

    public int getFlag() {
        return Flag;
    }

    /**
     * @return 返回包括标签头的10个字节和所有的标签帧的大小
     */
    public int getSize() {
        return Size;
    }
}
