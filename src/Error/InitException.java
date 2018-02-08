package Error;

/**
 * 任何地方的初始化错误
 * 包括解码器，以及MusicList等初始化错误
 */
public class InitException extends RuntimeException{
    public InitException(String errorMessage){
        super(errorMessage);
    }
}
