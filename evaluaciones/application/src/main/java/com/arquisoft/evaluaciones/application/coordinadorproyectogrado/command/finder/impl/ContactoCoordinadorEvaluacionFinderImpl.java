package com.arquisoft.evaluaciones.application.coordinadorproyectogrado.command.finder.impl;

import com.arquisoft.evaluaciones.application.coordinadorproyectogrado.command.finder.ContactoCoordinadorEvaluacionFinder;
import com.arquisoft.evaluaciones.application.coordinadorproyectogrado.command.secondaryport.CoordinadorProyectoGradoOutputPort;
import com.arquisoft.evaluaciones.application.coordinadorproyectogrado.command.secondaryport.mapper.CoordinadorProyectoGradoMapper;
import com.arquisoft.evaluaciones.domain.coordinadorproyectogrado.model.ContactoCoordinador;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ContactoCoordinadorEvaluacionFinderImpl implements ContactoCoordinadorEvaluacionFinder {

    private final CoordinadorProyectoGradoOutputPort coordinadorProyectoGradoOutputPort;

    @Override
    public ContactoCoordinador obtener(UUID evaluacion) {
        return coordinadorProyectoGradoOutputPort.obtenerPorEvaluacion(evaluacion)
                .map(CoordinadorProyectoGradoMapper::toContacto)
                .orElse(ContactoCoordinador.VACIO);
    }
}
