package com.arquisoft.artefactos.application.estudianteartefacto.command.finder;

import com.arquisoft.artefactos.domain.estudianteartefacto.AgregacionEstudiantesArtefactoDomain;
import com.arquisoft.shared.finder.Finder;

import java.util.List;
import java.util.UUID;

public interface EstudiantesYaVinculadosArtefactoFinder
        extends Finder<AgregacionEstudiantesArtefactoDomain, List<UUID>> {
}
