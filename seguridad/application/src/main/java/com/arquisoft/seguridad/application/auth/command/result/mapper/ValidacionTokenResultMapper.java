package com.arquisoft.seguridad.application.auth.command.result.mapper;

import com.arquisoft.seguridad.application.auth.command.result.ValidacionTokenResult;
import com.arquisoft.seguridad.application.auth.command.secondaryport.model.IdentidadProveedor;

public final class ValidacionTokenResultMapper {

    private ValidacionTokenResultMapper() {}

    public static ValidacionTokenResult toResult(IdentidadProveedor identidad) {
        return new ValidacionTokenResult.Valida(
                identidad.identidadId(),
                identidad.correo());
    }

    public static ValidacionTokenResult toResultInvalido() {
        return new ValidacionTokenResult.Invalida();
    }
}
