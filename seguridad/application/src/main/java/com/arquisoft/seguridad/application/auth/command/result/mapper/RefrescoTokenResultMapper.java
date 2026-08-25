package com.arquisoft.seguridad.application.auth.command.result.mapper;

import com.arquisoft.seguridad.application.auth.command.result.RefrescoTokenResult;
import com.arquisoft.seguridad.application.auth.command.secondaryport.model.CredencialesProveedor;

public final class RefrescoTokenResultMapper {

    private RefrescoTokenResultMapper() {}

    public static RefrescoTokenResult toResult(CredencialesProveedor credenciales) {
        return new RefrescoTokenResult(
                credenciales.tokenAcceso(),
                credenciales.tokenRefresco(),
                credenciales.expiraEn(),
                credenciales.tipoToken(),
                credenciales.alcance());
    }
}
