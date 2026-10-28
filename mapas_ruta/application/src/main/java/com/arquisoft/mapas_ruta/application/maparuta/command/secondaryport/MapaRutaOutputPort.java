package com.arquisoft.mapas_ruta.application.maparuta.command.secondaryport;

import com.arquisoft.mapas_ruta.application.maparuta.command.secondaryport.entity.MapaRutaEntity;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface MapaRutaOutputPort {

    void registrar(MapaRutaEntity mapaRuta);

    boolean existePorProyectoGrado(UUID proyectoGrado);

    Optional<MapaRutaEntity> obtenerPorId(UUID mapaRuta);

    Optional<MapaRutaEntity> obtenerPorTarea(UUID tarea);

    void actualizarFechaFin(UUID mapaRuta, LocalDate fechaFin);
}
