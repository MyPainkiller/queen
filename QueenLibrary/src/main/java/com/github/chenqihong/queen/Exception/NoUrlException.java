package com.github.chenqihong.queen.Exception;

/**
 * 当用户没有设置URL时抛出异常.
 * Exception threw when the user dose not give the server's url.
 *
 * Created by ChenQihong on 2016/2/1.
 */
public class NoUrlException extends Exception {
    public NoUrlException(){
        super("No URL prepared");
    }
    public NoUrlException(String msg){
        super(msg);
    }

    public NoUrlException(Exception e){
        super("No URL prepared",e);
    }

    public NoUrlException(String msg , Exception e){
        super(msg,e);
    }

}
