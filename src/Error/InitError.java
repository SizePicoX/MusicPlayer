package Error;

/**
 * InitException的异常码
 */
public interface InitError {

    /**
     * 访问的文件不存在
     */
    int File_Not_Available = -1;

    /**
     * 文件存在但无法访问
     */
    int File_Can_Not_Access = 0;

    /**
     * 文件正常访问但是没有所需要的数据
     */
    int No_Data = 1;

    /**
     * 整个文件无法访问
     */
    int File_Unreadable = 2;

    /**
     * 文件的某部分无法读取
     */
    int Part_File_Unreadable = 3;
}
