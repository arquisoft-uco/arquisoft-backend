package com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.command.adapter.out.persistence;

import com.arquisoft.fichas.domain.evaluacionfichaperfil.aggregate.EvaluacionFichaPerfilAggregate;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.model.PropietarioEvaluacionCriteria;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.port.out.EvaluacionFichaPerfilOutputPort;
import com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.persistence.EvaluacionFichaPerfilRepository;
import com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.persistence.EvaluacionFichaPerfilMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EvaluacionFichaPerfilCommandOutputAdapter
        implements EvaluacionFichaPerfilOutputPort {

    private final EvaluacionFichaPerfilRepository jpaRepository;
    private final EvaluacionFichaPerfilMapper mapper;

    @Override
    public void guardar(EvaluacionFichaPerfilAggregate evaluacion) {
        var entity = mapper.toEntity(evaluacion);
        jpaRepository.save(entity);
    }

    @Override
    public boolean existePorId(UUID id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public boolean existePorRepresentanteYFicha(UUID representanteComiteId, UUID fichaPerfilId) {
        return jpaRepository.existsByRepresentanteComiteIdAndFichaPerfilId(
                representanteComiteId,
                fichaPerfilId);
    }
    @Override
    public boolean esRepresentantePropietario(PropietarioEvaluacionCriteria criteria) {
        return jpaRepository.existsByIdAndRepresentanteComiteId(
                criteria.evaluacionFichaPerfil(), criteria.representanteComite());
    }
}
