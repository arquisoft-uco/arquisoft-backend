package com.arquisoft.solicitudes.application.solicitud.command.finder.impl;

import com.arquisoft.solicitudes.application.asignacionproyecto.command.secondaryport.AsignacionProyectoOutputPort;
import com.arquisoft.solicitudes.application.solicitud.command.finder.DestinatarioAsignadoFinder;
import com.arquisoft.solicitudes.domain.solicitud.model.ConsultaAsignacionResponsable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DestinatarioAsignadoFinderImpl implements DestinatarioAsignadoFinder {

    private final AsignacionProyectoOutputPort asignacionProyectoOutputPort;

    @Override
    public Boolean obtener(ConsultaAsignacionResponsable consulta) {
        return asignacionProyectoOutputPort.esResponsableAsignado(
                consulta.estudianteUsuario(), consulta.responsableUsuario());
    }
}
