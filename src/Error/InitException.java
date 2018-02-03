package Error;

/**
 * 初始化错误
 * 代表以任何原因发生的初始化Music对象的错误
 * 目的：无论哪中原因导致了构造Music对象失败，
 * 都抛出错误使得本次构造作废，且不影响下一次构造，并输出到控制台或者日志文件
 */
public class InitException extends DecoderException{
    public InitException(String errorMessage){
        super(errorMessage);
    }
}
