package com.arquisoft.shared.query;

import com.arquisoft.shared.exception.DomainException;

public class FiltroException extends DomainException {

    public FiltroException(String mensaje, String errorCode) {
        super(mensaje, errorCode);
    }
}
