package com.arquisoft.seguridad.application.autorizacion.command.finder.impl;

import com.arquisoft.seguridad.application.autorizacion.command.finder.RecursoSoloAutenticacionFinder;
import com.arquisoft.seguridad.application.autorizacion.command.secondaryport.PoliticaRecursoOutputPort;
import com.arquisoft.seguridad.domain.autorizacion.AccesoDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RecursoSoloAutenticacionFinderImpl implements RecursoSoloAutenticacionFinder {

    private final PoliticaRecursoOutputPort politicaRecursoOutputPort;

    @Override
    public Boolean obtener(AccesoDomain acceso) {
        return politicaRecursoOutputPort.exigeSoloAutenticacion(acceso.getMetodo(), acceso.getRecurso());
    }
}
