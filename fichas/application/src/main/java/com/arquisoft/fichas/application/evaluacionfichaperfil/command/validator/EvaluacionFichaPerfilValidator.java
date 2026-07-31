package com.arquisoft.fichas.application.evaluacionfichaperfil.command.validator;

import com.arquisoft.fichas.application.evaluacionfichaperfil.exception.EvaluacionFichaPerfilDuplicadaException;
import com.arquisoft.fichas.application.evaluacionfichaperfil.exception.RepresentanteComiteNoEncontradoException;
import com.arquisoft.fichas.application.representantecomite.query.port.out.RepresentanteComiteQueryOutputPort;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.port.out.EvaluacionFichaPerfilOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Validaciones reutilizables de existencia y unicidad de evaluaciones de ficha de perfil.
 */
@Component
@RequiredArgsConstructor
public class EvaluacionFichaPerfilValidator {

    private final RepresentanteComiteQueryOutputPort representanteComiteQueryOutputPort;
    private final EvaluacionFichaPerfilOutputPort evaluacionFichaPerfilOutputPort;

    public void validarRepresentanteExiste(UUID representanteComite) {
        if (!representanteComiteQueryOutputPort.existePorId(representanteComite)) {
            throw new RepresentanteComiteNoEncontradoException(representanteComite);
        }
    }

    /** Un representante solo puede registrar una evaluación por ficha. */
    public void validarEvaluacionNoDuplicada(UUID representanteComite, UUID fichaPerfil) {
        if (evaluacionFichaPerfilOutputPort.existePorRepresentanteYFicha(representanteComite, fichaPerfil)) {
            throw new EvaluacionFichaPerfilDuplicadaException(representanteComite, fichaPerfil);
        }
    }
}
