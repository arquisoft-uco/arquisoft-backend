package com.arquisoft.shared.finder;

public interface Finder<T, R> {

    R obtener(T entrada);
}
