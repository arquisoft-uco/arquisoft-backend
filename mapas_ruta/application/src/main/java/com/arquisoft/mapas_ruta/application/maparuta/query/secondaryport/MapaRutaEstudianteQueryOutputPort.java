package com.arquisoft.mapas_ruta.application.maparuta.query.secondaryport;

import com.arquisoft.mapas_ruta.application.maparuta.query.readmodel.MapaRutaEstudianteReadModel;

import java.util.Optional;
import java.util.UUID;

public interface MapaRutaEstudianteQueryOutputPort {

    Optional<MapaRutaEstudianteReadModel> consultarPorProyectoGrado(UUID proyectoGrado);
}
