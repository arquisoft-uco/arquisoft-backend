package com.arquisoft.shared.usecase;

public interface UseCase<I, O> {

    O ejecutar(I input);
}
