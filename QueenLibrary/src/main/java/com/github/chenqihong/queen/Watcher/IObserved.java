package com.github.chenqihong.queen.Watcher;

/**
 * Created by ChenQihong on 2016/2/2.
 */
public interface IObserved {
    void registerObserver(IQueenWatcher watcher);
    void unregisterObserver(IQueenWatcher watcher);
    void notifyDataChanged(String message);
}
