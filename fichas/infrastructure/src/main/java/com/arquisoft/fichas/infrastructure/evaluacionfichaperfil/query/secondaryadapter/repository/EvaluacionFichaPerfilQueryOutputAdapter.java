package com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.query.secondaryadapter.repository;

import com.arquisoft.fichas.application.evaluacionfichaperfil.query.readmodel.EvaluacionFichaPerfilReadModel;
import com.arquisoft.fichas.application.evaluacionfichaperfil.query.secondaryport.EvaluacionFichaPerfilQueryOutputPort;
import com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.query.secondaryadapter.repository.mapper.EvaluacionFichaPerfilQueryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EvaluacionFichaPerfilQueryOutputAdapter implements EvaluacionFichaPerfilQueryOutputPort {

    private final EvaluacionFichaPerfilQueryRepository evaluacionFichaPerfilQueryRepository;

    @Override
    public List<EvaluacionFichaPerfilReadModel> consultarPorFichaYRepresentante(
            UUID fichaPerfil, UUID representanteComite) {
        return evaluacionFichaPerfilQueryRepository
                .findByFichaPerfilIdAndRepresentanteComiteIdOrderByFechaCreacionAsc(fichaPerfil, representanteComite)
                .stream()
                .map(EvaluacionFichaPerfilQueryMapper::toReadModel)
                .toList();
    }
}
