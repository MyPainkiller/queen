package com.github.chenqihong.queen.Watcher;

/**
 * Interface for Queen's observing instance.
 * 观察者接口
 *
 * Created by ChenQihong on 2016/2/2.
 */
public interface IQueenWatcher {
    /**
     * 获取状态
     * update the status.
     *
     * @param str the message
     */
    void update(String str);
}
