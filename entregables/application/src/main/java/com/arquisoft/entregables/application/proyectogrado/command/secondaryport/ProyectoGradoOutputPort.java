package com.arquisoft.entregables.application.proyectogrado.command.secondaryport;

import com.arquisoft.entregables.application.proyectogrado.command.secondaryport.entity.ProyectoGradoEntity;

import java.util.Optional;
import java.util.UUID;

public interface ProyectoGradoOutputPort {

    void guardar(ProyectoGradoEntity proyecto);

    boolean existePorId(UUID id);

    Optional<ProyectoGradoEntity> obtenerPorId(UUID id);

    Optional<ProyectoGradoEntity> obtenerBloqueadoPorId(UUID id);

    Optional<ProyectoGradoEntity> obtenerBloqueadoPorEntregable(UUID entregable);

    void actualizarEstado(UUID proyectoGrado, String estadoProyecto);
}
