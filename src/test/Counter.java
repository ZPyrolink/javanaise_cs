package test;

import proxy.ReadWrite;

public class Counter implements ReadWrite<Integer> {
    private int data;


    @Override
    public void write(Integer value) {
        data = value;
    }

    @Override
    public Integer read() {
        return data;
    }
}
