package com.arquisoft.mapas_ruta.application.proyectogrado.command.secondaryport;

import com.arquisoft.mapas_ruta.application.proyectogrado.command.secondaryport.entity.ProyectoGradoEntity;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface ProyectoGradoOutputPort {

    void guardar(ProyectoGradoEntity proyecto);

    boolean existePorId(UUID proyectoGrado);

    Optional<ProyectoGradoEntity> obtenerPorId(UUID proyectoGrado);

    void actualizarEstado(UUID proyectoGrado, String estadoProyecto, Instant ocurridoEn);
}
