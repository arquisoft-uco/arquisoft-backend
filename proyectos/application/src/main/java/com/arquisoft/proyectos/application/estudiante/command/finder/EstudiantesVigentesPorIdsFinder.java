package com.arquisoft.proyectos.application.estudiante.command.finder;

import com.arquisoft.proyectos.domain.estudiante.EstudianteDomain;
import com.arquisoft.shared.finder.Finder;

import java.util.List;
import java.util.UUID;

public interface EstudiantesVigentesPorIdsFinder extends Finder<List<UUID>, List<EstudianteDomain>> {
}
