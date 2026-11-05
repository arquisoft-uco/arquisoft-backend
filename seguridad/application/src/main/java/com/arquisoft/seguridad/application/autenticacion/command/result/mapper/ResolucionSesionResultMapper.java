package com.arquisoft.seguridad.application.autenticacion.command.result.mapper;

import com.arquisoft.seguridad.application.autenticacion.command.result.ResolucionSesionResult;
import com.arquisoft.seguridad.application.autenticacion.command.secondaryport.model.ResolucionSesion;

import java.util.UUID;

public final class ResolucionSesionResultMapper {

    private ResolucionSesionResultMapper() {}

    public static ResolucionSesionResult toResultResuelta(ResolucionSesion.Resuelta resuelta, UUID usuario) {
        return new ResolucionSesionResult.Resuelta(
                usuario.toString(),
                resuelta.nombre(),
                resuelta.correo(),
                resuelta.tenant());
    }

    public static ResolucionSesionResult toResultSinSesion() {
        return new ResolucionSesionResult.SinSesion();
    }

    public static ResolucionSesionResult toResultUsuarioNoRegistrado() {
        return new ResolucionSesionResult.UsuarioNoRegistrado();
    }
}
