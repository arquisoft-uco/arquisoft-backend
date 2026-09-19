package com.arquisoft.solicitudes.domain.solicitud.rules.impl;

import com.arquisoft.solicitudes.domain.solicitud.exception.DestinatarioNoAsignadoException;
import com.arquisoft.solicitudes.domain.solicitud.model.ExistenciaAsignacionResponsable;
import com.arquisoft.solicitudes.domain.solicitud.rules.DestinatarioAsignadoRule;

public class DestinatarioAsignadoRuleImpl implements DestinatarioAsignadoRule {

    @Override
    public void validar(ExistenciaAsignacionResponsable asignacion) {
        if (!asignacion.asignado()) {
            throw new DestinatarioNoAsignadoException(
                    asignacion.responsableUsuario(), asignacion.estudianteUsuario());
        }
    }
}
