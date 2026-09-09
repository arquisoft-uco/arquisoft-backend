package com.arquisoft.fichas.infrastructure.estadofichaperfil.query.secondaryadapter.repository;

import com.arquisoft.fichas.application.estadofichaperfil.query.readmodel.EstadoFichaPerfilReadModel;
import com.arquisoft.fichas.application.estadofichaperfil.query.secondaryport.EstadoFichaPerfilQueryOutputPort;
import com.arquisoft.fichas.infrastructure.estadofichaperfil.query.secondaryadapter.repository.mapper.EstadoFichaPerfilQueryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EstadoFichaPerfilQueryOutputAdapter implements EstadoFichaPerfilQueryOutputPort {

    private final EstadoFichaPerfilEstudianteQueryRepository estadoFichaPerfilEstudianteQueryRepository;

    @Override
    public List<EstadoFichaPerfilReadModel> consultarPorFichaYEstudiante(UUID fichaPerfil, UUID estudiante) {
        return estadoFichaPerfilEstudianteQueryRepository
                .findByFichaPerfilIdAndEstudianteIdOrderByFechaActualizacionDesc(fichaPerfil, estudiante)
                .stream()
                .map(EstadoFichaPerfilQueryMapper::toReadModel)
                .toList();
    }
}
