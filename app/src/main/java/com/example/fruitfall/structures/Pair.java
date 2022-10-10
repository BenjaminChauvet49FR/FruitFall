package com.example.fruitfall.structures;

// Credits : https://stackoverflow.com/questions/6271731/whats-the-best-way-to-return-a-pair-of-values-in-java
public class Pair<T, U> {
    public final T t;
    public final U u;

    public Pair(T t, U u) {
        this.t= t;
        this.u= u;
    }
}
// Note : I think we won't be able to derivate Spacecoors out of this ; also their properties are x,y.