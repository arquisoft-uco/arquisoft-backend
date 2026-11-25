package com.arquisoft.fichas.application.evaluacionfichaperfil.command.validator;

import com.arquisoft.fichas.domain.estadofichaperfil.EstadoFichaPerfilDomain;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.EvaluacionFichaPerfilDomain;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.model.EvaluacionExistente;

public interface RegistrarEvaluacionFichaPerfilValidator {

    void validar(EvaluacionFichaPerfilDomain evaluacion, boolean fichaExiste, boolean representanteExiste,
                 EvaluacionExistente existente, EstadoFichaPerfilDomain estadoFicha);
}
