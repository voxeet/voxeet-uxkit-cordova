package com.voxeet.toolkit.utils;

import java.util.concurrent.locks.ReentrantLock;

public class SafeLock {
    private final ReentrantLock lock = new ReentrantLock();

    private void lock(ReentrantLock lock) {
        try {
            lock.lock();
        } catch (Exception e) {

        }
    }

    private void unlock(ReentrantLock lock) {
        try {
            if (lock.isLocked())
                lock.unlock();
        } catch (Exception e) {

        }
    }

    public void lock() {
        lock(lock);
    }

    public void unlock() {
        unlock(lock);
    }
}
