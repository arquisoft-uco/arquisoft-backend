package com.arquisoft.mapas_ruta.infrastructure.maparuta.query.secondaryadapter.repository;

import com.arquisoft.mapas_ruta.application.maparuta.query.readmodel.MapaRutaEstudianteReadModel;
import com.arquisoft.mapas_ruta.application.maparuta.query.secondaryport.MapaRutaEstudianteQueryOutputPort;
import com.arquisoft.mapas_ruta.infrastructure.maparuta.query.secondaryadapter.repository.mapper.MapaRutaEstudianteQueryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MapaRutaEstudianteQueryOutputAdapter implements MapaRutaEstudianteQueryOutputPort {

    private final MapaRutaEstudianteQueryRepository mapaRutaEstudianteQueryRepository;

    @Override
    public Optional<MapaRutaEstudianteReadModel> consultarPorProyectoGrado(UUID proyectoGrado) {
        return mapaRutaEstudianteQueryRepository.findByProyectoGradoId(proyectoGrado)
                .map(MapaRutaEstudianteQueryMapper::toReadModel);
    }
}
