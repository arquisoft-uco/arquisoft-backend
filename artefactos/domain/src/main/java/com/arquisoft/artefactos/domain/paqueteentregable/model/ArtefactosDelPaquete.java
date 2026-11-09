package com.arquisoft.artefactos.domain.paqueteentregable.model;

import java.util.List;

public record ArtefactosDelPaquete(
        List<ArtefactoIncluidoPaquete> incluidos,
        List<ArtefactoExcluidoPaquete> excluidos
) {

    public ArtefactosDelPaquete {
        incluidos = List.copyOf(incluidos);
        excluidos = List.copyOf(excluidos);
    }
}
