package com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor;

import com.arquisoft.shared.util.UtilUUID;

import java.util.UUID;

public final class AccesoEvaluacionCuantitativaAsesorDomain {

    public static final AccesoEvaluacionCuantitativaAsesorDomain VACIO = reconstruir(
            UtilUUID.obtenerUUIDPorDefecto(),
            UtilUUID.obtenerUUIDPorDefecto());

    private UUID evaluacionAsesor;
    private UUID asesor;

    private AccesoEvaluacionCuantitativaAsesorDomain() {}

    public static AccesoEvaluacionCuantitativaAsesorDomain reconstruir(UUID evaluacionAsesor, UUID asesor) {
        var acceso = new AccesoEvaluacionCuantitativaAsesorDomain();
        acceso.evaluacionAsesor = evaluacionAsesor;
        acceso.asesor = asesor;
        return acceso;
    }

    public boolean esVacio() {
        return this == VACIO;
    }

    public UUID getEvaluacionAsesor() {
        return evaluacionAsesor;
    }

    public UUID getAsesor() {
        return asesor;
    }
}
