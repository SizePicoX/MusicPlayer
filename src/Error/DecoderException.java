package Error;

/**
 * 解码器中出现的异常
 */
public class DecoderException extends RuntimeException{
    public DecoderException(String errorMessage){
        super(errorMessage);
    }
}
