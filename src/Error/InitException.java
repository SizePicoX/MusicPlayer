package Error;

/**
 * 任何地方的初始化错误
 * 包括解码器，以及MusicList等初始化错误
 */
public class InitException extends RuntimeException implements InitError{
    private int errorCode;

    /**建立一个初始化异常
     * @param errorMessage 错误提示信息
     * @param errorCode  对应的错误码
     */
    public InitException(String errorMessage,int errorCode ){
        super(errorMessage);
        this.errorCode = errorCode;
    }

    /**
     * @return 本次InitException异常的异常码
     */
    public int getErrorCode() {
        return errorCode;
    }
}
