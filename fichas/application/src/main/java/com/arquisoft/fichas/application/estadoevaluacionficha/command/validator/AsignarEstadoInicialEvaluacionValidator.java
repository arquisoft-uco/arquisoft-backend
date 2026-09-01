package com.arquisoft.fichas.application.estadoevaluacionficha.command.validator;

import java.util.UUID;

public interface AsignarEstadoInicialEvaluacionValidator {

    void validar(UUID evaluacionFichaPerfil, boolean evaluacionExiste);
}
