package com.arquisoft.evaluaciones.application.proyectoestudianteacceso.command.finder;

import com.arquisoft.evaluaciones.application.proyectoestudianteacceso.command.secondaryport.entity.DestinatarioEvaluacionEntity;
import com.arquisoft.shared.finder.Finder;

import java.util.List;
import java.util.UUID;

public interface DestinatariosEvaluacionFinder extends Finder<UUID, List<DestinatarioEvaluacionEntity>> {
}
