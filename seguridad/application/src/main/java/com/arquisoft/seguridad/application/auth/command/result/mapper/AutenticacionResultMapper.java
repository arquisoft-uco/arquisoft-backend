package com.arquisoft.seguridad.application.auth.command.result.mapper;

import com.arquisoft.seguridad.application.auth.command.result.AutenticacionResult;
import com.arquisoft.seguridad.application.auth.command.secondaryport.model.CredencialesProveedor;

public final class AutenticacionResultMapper {

    private AutenticacionResultMapper() {}

    public static AutenticacionResult toResult(CredencialesProveedor credenciales) {
        return new AutenticacionResult(
                credenciales.tokenAcceso(),
                credenciales.tokenRefresco(),
                credenciales.expiraEn(),
                credenciales.tipoToken(),
                credenciales.alcance());
    }
}
