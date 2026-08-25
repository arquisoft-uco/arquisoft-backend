package com.arquisoft.shared.rules;

public interface DomainRule<T> {

    void validar(T entrada);
}
