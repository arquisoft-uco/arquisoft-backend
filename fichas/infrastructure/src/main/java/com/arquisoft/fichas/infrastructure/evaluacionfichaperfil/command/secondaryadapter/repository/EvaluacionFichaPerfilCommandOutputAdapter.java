package com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.command.secondaryadapter.repository;

import com.arquisoft.fichas.application.evaluacionfichaperfil.command.secondaryport.EvaluacionFichaPerfilOutputPort;
import com.arquisoft.fichas.application.evaluacionfichaperfil.command.secondaryport.entity.EvaluacionFichaPerfilEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EvaluacionFichaPerfilCommandOutputAdapter
        implements EvaluacionFichaPerfilOutputPort {

    private final EvaluacionFichaPerfilCommandRepository repository;

    @Override
    public void registrarEvaluacion(EvaluacionFichaPerfilEntity evaluacion) {
        repository.save(evaluacion);
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
