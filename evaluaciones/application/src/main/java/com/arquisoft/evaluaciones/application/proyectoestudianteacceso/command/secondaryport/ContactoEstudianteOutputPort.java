package com.arquisoft.evaluaciones.application.proyectoestudianteacceso.command.secondaryport;

import com.arquisoft.evaluaciones.application.proyectoestudianteacceso.command.secondaryport.entity.DestinatarioEvaluacionEntity;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface ContactoEstudianteOutputPort {

    List<DestinatarioEvaluacionEntity> consultarContactos(Set<UUID> estudiantes);
}
