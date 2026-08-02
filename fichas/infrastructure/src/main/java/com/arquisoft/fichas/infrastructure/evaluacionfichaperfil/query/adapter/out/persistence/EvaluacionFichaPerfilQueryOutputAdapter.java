package com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.query.adapter.out.persistence;

import com.arquisoft.fichas.application.evaluacionfichaperfil.query.criteria.PropietarioEvaluacionCriteria;
import com.arquisoft.fichas.application.evaluacionfichaperfil.query.port.out.EvaluacionFichaPerfilQueryOutputPort;
import com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.persistence.EvaluacionFichaPerfilRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EvaluacionFichaPerfilQueryOutputAdapter implements EvaluacionFichaPerfilQueryOutputPort {

    private final EvaluacionFichaPerfilRepository evaluacionFichaPerfilRepository;

    @Override
    public boolean existePorId(UUID evaluacionFichaPerfilId) {
        return evaluacionFichaPerfilRepository.existsById(evaluacionFichaPerfilId);
    }

    @Override
    public boolean esRepresentantePropietario(PropietarioEvaluacionCriteria criteria) {
        return evaluacionFichaPerfilRepository.existsByIdAndRepresentanteComiteId(
                criteria.evaluacionFichaPerfil(),
                criteria.representanteComite());
    }
}
