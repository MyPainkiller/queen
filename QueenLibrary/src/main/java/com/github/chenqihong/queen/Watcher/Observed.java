package com.github.chenqihong.queen.Watcher;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ChenQihong on 2016/2/2.
 */
public class Observed implements IObserved{
    private List<IQueenWatcher> list = new ArrayList<>();

    @Override
    public void registerObserver(IQueenWatcher watcher) {
        list.add(watcher);
    }

    @Override
    public void unregisterObserver(IQueenWatcher watcher) {
        list.remove(watcher);
    }

    @Override
    public void notifyDataChanged(String message) {
        for(IQueenWatcher watcher : list){
            watcher.update(message);
        }
    }
}
