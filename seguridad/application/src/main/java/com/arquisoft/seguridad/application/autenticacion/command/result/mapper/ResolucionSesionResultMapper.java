package com.arquisoft.seguridad.application.autenticacion.command.result.mapper;

import com.arquisoft.seguridad.application.autenticacion.command.result.ResolucionSesionResult;
import com.arquisoft.seguridad.application.autenticacion.command.secondaryport.model.ResolucionSesion;

public final class ResolucionSesionResultMapper {

    private ResolucionSesionResultMapper() {}

    public static ResolucionSesionResult toResult(ResolucionSesion resolucion) {
        return switch (resolucion) {
            case ResolucionSesion.Resuelta resuelta -> toResultResuelta(resuelta);
            case ResolucionSesion.SinSesion ignorada -> toResultSinSesion();
        };
    }

    public static ResolucionSesionResult toResultResuelta(ResolucionSesion.Resuelta resuelta) {
        return new ResolucionSesionResult.Resuelta(
                resuelta.sujeto(),
                resuelta.nombre(),
                resuelta.correo(),
                resuelta.tenant());
    }

    public static ResolucionSesionResult toResultSinSesion() {
        return new ResolucionSesionResult.SinSesion();
    }
}
