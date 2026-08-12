package com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.command.secondaryadapter.repository;

import com.arquisoft.fichas.domain.evaluacionfichaperfil.EvaluacionFichaPerfilDomain;
import com.arquisoft.fichas.application.evaluacionfichaperfil.command.secondaryport.EvaluacionFichaPerfilOutputPort;
import com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.persistence.EvaluacionFichaPerfilRepository;
import com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.persistence.EvaluacionFichaPerfilMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EvaluacionFichaPerfilCommandOutputAdapter
        implements EvaluacionFichaPerfilOutputPort {

    private final EvaluacionFichaPerfilRepository repository;
    private final EvaluacionFichaPerfilMapper mapper;

    @Override
    public void registrarEvaluacion(EvaluacionFichaPerfilDomain evaluacion) {
        var entity = mapper.toEntity(evaluacion);
        repository.save(entity);
    }

    @Override
    public boolean existePorId(UUID id) {
        return repository.existsById(id);
    }

    @Override
    public boolean existePorRepresentanteYFicha(UUID representanteComiteId, UUID fichaPerfilId) {
        return repository.existsByRepresentanteComiteIdAndFichaPerfilId(
                representanteComiteId,
                fichaPerfilId);
    }
    @Override
    public boolean esRepresentantePropietario(UUID evaluacionFichaPerfil, UUID representanteComite) {
        return repository.existsByIdAndRepresentanteComiteId(evaluacionFichaPerfil, representanteComite);
    }
}
