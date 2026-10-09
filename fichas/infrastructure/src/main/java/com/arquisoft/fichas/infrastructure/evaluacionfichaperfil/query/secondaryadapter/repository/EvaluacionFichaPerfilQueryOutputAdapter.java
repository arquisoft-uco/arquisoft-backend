package com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.query.secondaryadapter.repository;

import com.arquisoft.fichas.application.evaluacionfichaperfil.query.readmodel.EvaluacionFichaPerfilCoordinadorReadModel;
import com.arquisoft.fichas.application.evaluacionfichaperfil.query.readmodel.EvaluacionFichaPerfilEstudianteReadModel;
import com.arquisoft.fichas.application.evaluacionfichaperfil.query.readmodel.EvaluacionFichaPerfilReadModel;
import com.arquisoft.fichas.application.evaluacionfichaperfil.query.secondaryport.EvaluacionFichaPerfilQueryOutputPort;
import com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.query.secondaryadapter.repository.mapper.EvaluacionFichaPerfilCoordinadorQueryMapper;
import com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.query.secondaryadapter.repository.mapper.EvaluacionFichaPerfilEstudianteQueryMapper;
import com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.query.secondaryadapter.repository.mapper.EvaluacionFichaPerfilQueryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EvaluacionFichaPerfilQueryOutputAdapter implements EvaluacionFichaPerfilQueryOutputPort {

    private final EvaluacionFichaPerfilQueryRepository evaluacionFichaPerfilQueryRepository;
    private final EvaluacionFichaPerfilEstudianteQueryRepository evaluacionFichaPerfilEstudianteQueryRepository;
    private final EvaluacionFichaPerfilCoordinadorQueryRepository evaluacionFichaPerfilCoordinadorQueryRepository;

    @Override
    public List<EvaluacionFichaPerfilReadModel> consultarPorFichaYRepresentante(
            UUID fichaPerfil, UUID representanteComite) {
        return evaluacionFichaPerfilQueryRepository
                .findByFichaPerfilIdAndRepresentanteComiteIdOrderByFechaCreacionAsc(fichaPerfil, representanteComite)
                .stream()
                .map(EvaluacionFichaPerfilQueryMapper::toReadModel)
                .toList();
    }

    @Override
    public List<EvaluacionFichaPerfilEstudianteReadModel> consultarPorFichaYEstudiante(UUID fichaPerfil, UUID estudiante) {
        return evaluacionFichaPerfilEstudianteQueryRepository
                .findByFichaPerfilIdAndEstudianteIdOrderByFechaCreacionAsc(fichaPerfil, estudiante)
                .stream()
                .map(EvaluacionFichaPerfilEstudianteQueryMapper::toReadModel)
                .toList();
    }

    @Override
    public List<EvaluacionFichaPerfilCoordinadorReadModel> consultarPorFicha(UUID fichaPerfil) {
        return evaluacionFichaPerfilCoordinadorQueryRepository
                .findByFichaPerfilIdOrderByFechaCreacionAsc(fichaPerfil)
                .stream()
                .map(EvaluacionFichaPerfilCoordinadorQueryMapper::toReadModel)
                .toList();
    }
}
