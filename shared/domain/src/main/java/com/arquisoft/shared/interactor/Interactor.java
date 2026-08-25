package com.arquisoft.shared.interactor;

public interface Interactor<I, O> {

    O ejecutar(I input);
}
