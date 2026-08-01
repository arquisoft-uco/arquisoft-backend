package com.arquisoft.shared.inputport;

public interface InputPort<I, O> {

    O ejecutar(I input);
}
