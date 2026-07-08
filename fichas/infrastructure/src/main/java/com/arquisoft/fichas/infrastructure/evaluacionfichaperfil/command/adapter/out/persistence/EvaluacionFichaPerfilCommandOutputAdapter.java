package com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.command.adapter.out.persistence;

import com.arquisoft.fichas.domain.evaluacionfichaperfil.aggregate.EvaluacionFichaPerfilAggregate;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.port.out.EvaluacionFichaPerfilOutputPort;
import com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.persistence.EvaluacionFichaPerfilJpaRepository;
import com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.persistence.EvaluacionFichaPerfilMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EvaluacionFichaPerfilCommandOutputAdapter
        implements EvaluacionFichaPerfilOutputPort {

    private final EvaluacionFichaPerfilJpaRepository jpaRepository;
    private final EvaluacionFichaPerfilMapper mapper;

    @Override
    public void guardar(EvaluacionFichaPerfilAggregate evaluacion) {
        var entity = mapper.toJpaEntity(evaluacion);
        jpaRepository.save(entity);
    }

    @Override
    public boolean existsById(UUID id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public boolean existsByRepresentanteAndFicha(UUID representanteComiteId, UUID fichaPerfilId) {
        return jpaRepository.existsByRepresentanteComiteIdAndFichaPerfilId(
                representanteComiteId,
                fichaPerfilId);
    }
}
