package com.github.chenqihong.queen.Watcher;

/**
 * Interface for Queen's observed instance.
 * 被观察接口.
 *
 * Created by ChenQihong on 2016/2/2.
 */
public interface IObserved {

    /**
     * 注册Observer
     * register the observer.
     * @param watcher 观察者
     */
    void registerObserver(IQueenWatcher watcher);

    /**
     * 取消Observer
     * cancel the observer.
     * @param watcher 观察者
     */
    void unregisterObserver(IQueenWatcher watcher);

    /**
     * 通知所有observer数据有所改变
     * notify data changed for observer.
     * @param message 观察者
     */
    void notifyDataChanged(String message);
}
