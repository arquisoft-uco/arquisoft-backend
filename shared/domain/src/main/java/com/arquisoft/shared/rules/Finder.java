package com.arquisoft.shared.rules;

public interface Finder<T, R> {

    R obtener(T entrada);
}
