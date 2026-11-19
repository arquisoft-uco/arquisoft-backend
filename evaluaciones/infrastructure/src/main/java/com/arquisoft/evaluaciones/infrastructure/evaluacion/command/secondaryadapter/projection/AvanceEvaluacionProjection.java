package com.arquisoft.evaluaciones.infrastructure.evaluacion.command.secondaryadapter.projection;

import java.util.UUID;

public interface AvanceEvaluacionProjection {

    UUID getEvaluacion();

    String getEstado();

    UUID getEntregable();

    String getProyecto();

    int getVersion();

    long getJuradosVigentes();

    long getJuradosEvaluados();

    boolean getAsesorCompleto();
}
