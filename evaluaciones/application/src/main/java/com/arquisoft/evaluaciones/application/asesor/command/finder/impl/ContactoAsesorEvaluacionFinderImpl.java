package com.arquisoft.evaluaciones.application.asesor.command.finder.impl;

import com.arquisoft.evaluaciones.application.asesor.command.finder.ContactoAsesorEvaluacionFinder;
import com.arquisoft.evaluaciones.application.asesor.command.secondaryport.AsesorOutputPort;
import com.arquisoft.evaluaciones.domain.asesor.model.ContactoAsesor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ContactoAsesorEvaluacionFinderImpl implements ContactoAsesorEvaluacionFinder {

    private final AsesorOutputPort asesorOutputPort;

    @Override
    public ContactoAsesor obtener(UUID evaluacion) {
        return asesorOutputPort.obtenerContactoPorEvaluacion(evaluacion)
                .map(asesor -> new ContactoAsesor(asesor.id(), asesor.nombre(), asesor.email()))
                .orElse(ContactoAsesor.VACIO);
    }
}
