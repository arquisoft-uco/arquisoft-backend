package com.arquisoft.shared.webclient;

public record RespuestaHttp<R>(int estado, R cuerpo) {

    public boolean esExitosa() {
        return estado >= 200 && estado < 300;
    }
}
