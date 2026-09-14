package com.arquisoft.evaluaciones.application.proyectoestudianteacceso.command.finder.impl;

import com.arquisoft.evaluaciones.application.proyectoestudianteacceso.command.finder.DestinatariosEvaluacionFinder;
import com.arquisoft.evaluaciones.application.proyectoestudianteacceso.command.secondaryport.ContactoEstudianteOutputPort;
import com.arquisoft.evaluaciones.application.proyectoestudianteacceso.command.secondaryport.ProyectoEstudianteAccesoOutputPort;
import com.arquisoft.evaluaciones.application.proyectoestudianteacceso.command.secondaryport.entity.DestinatarioEvaluacionEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DestinatariosEvaluacionFinderImpl implements DestinatariosEvaluacionFinder {

    private final ProyectoEstudianteAccesoOutputPort proyectoEstudianteAccesoOutputPort;
    private final ContactoEstudianteOutputPort contactoEstudianteOutputPort;

    @Override
    public List<DestinatarioEvaluacionEntity> obtener(UUID entregable) {
        Set<UUID> estudiantes =
                proyectoEstudianteAccesoOutputPort.obtenerEstudiantesConAccesoPorEntregable(entregable);

        return contactoEstudianteOutputPort.consultarContactos(estudiantes);
    }
}
