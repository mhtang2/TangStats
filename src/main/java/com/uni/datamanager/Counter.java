package com.uni.datamanager;

public class Counter {
    int x;

    Counter(int n) {
        x = n;
    }

    void reset() {
        x = 0;
    }

    void inc() {
        x++;
    }

    int val() {
        return x;
    }
}
