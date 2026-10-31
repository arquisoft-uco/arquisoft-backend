package com.arquisoft.artefactos.domain.observacion.model;

import com.arquisoft.artefactos.domain.revisionasesor.RevisionAsesorDomain;

import java.util.UUID;

public record SituacionReaperturaObservacion(
        UUID observacion, EstadoObservacion destino,
        int numeroVersion, int numeroUltimaVersion, RevisionAsesorDomain revisionVersionSiguiente) {

    public boolean esReapertura() {
        return destino.esReabierta();
    }

    public int diferenciaVersiones() {
        return numeroUltimaVersion - numeroVersion;
    }
}
