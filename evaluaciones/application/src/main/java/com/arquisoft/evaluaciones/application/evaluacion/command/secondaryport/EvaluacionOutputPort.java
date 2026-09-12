package com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport;

import java.util.UUID;

public interface EvaluacionOutputPort {

    void actualizarEstado(UUID evaluacion, String estado);
}
