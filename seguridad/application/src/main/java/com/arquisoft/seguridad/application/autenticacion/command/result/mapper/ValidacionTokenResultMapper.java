package com.arquisoft.seguridad.application.autenticacion.command.result.mapper;

import com.arquisoft.seguridad.application.autenticacion.command.result.ValidacionTokenResult;
import com.arquisoft.seguridad.application.autenticacion.command.secondaryport.model.IdentidadProveedor;

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
