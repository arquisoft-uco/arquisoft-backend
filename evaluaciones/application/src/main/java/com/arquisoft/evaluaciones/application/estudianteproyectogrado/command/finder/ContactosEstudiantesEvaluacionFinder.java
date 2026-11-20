package com.arquisoft.evaluaciones.application.estudianteproyectogrado.command.finder;

import com.arquisoft.evaluaciones.domain.estudianteproyectogrado.model.ContactoEstudiante;
import com.arquisoft.shared.finder.Finder;

import java.util.List;
import java.util.UUID;

public interface ContactosEstudiantesEvaluacionFinder extends Finder<UUID, List<ContactoEstudiante>> {
}
