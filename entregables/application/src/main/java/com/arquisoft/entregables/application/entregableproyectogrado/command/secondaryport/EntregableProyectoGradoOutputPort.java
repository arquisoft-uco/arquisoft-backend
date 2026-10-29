package com.arquisoft.entregables.application.entregableproyectogrado.command.secondaryport;

import com.arquisoft.entregables.application.entregableproyectogrado.command.secondaryport.entity.EntregableProyectoGradoEntity;

import java.util.Optional;
import java.util.UUID;

public interface EntregableProyectoGradoOutputPort {

    void registrar(EntregableProyectoGradoEntity entregable);

    int obtenerUltimaVersion(UUID proyectoGrado);

    Optional<EntregableProyectoGradoEntity> obtenerPorId(UUID id);

    void actualizarEstado(UUID entregableProyectoGrado, String estadoEntregable);
}
