package com.arquisoft.mapas_ruta.application.maparuta.command.secondaryport;

import com.arquisoft.mapas_ruta.application.maparuta.command.secondaryport.entity.MapaRutaEntity;

import java.util.UUID;

public interface MapaRutaOutputPort {

    void registrar(MapaRutaEntity mapaRuta);

    boolean existePorProyectoGrado(UUID proyectoGrado);
}
