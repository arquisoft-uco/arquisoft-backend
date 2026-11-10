package com.arquisoft.mapas_ruta.application.planreal.command.secondaryport;

import com.arquisoft.mapas_ruta.application.planreal.command.secondaryport.entity.PlanRealEntity;

import java.util.UUID;

public interface PlanRealOutputPort {

    void registrar(PlanRealEntity planReal);

    boolean existePorTarea(UUID tarea);

    void actualizarPorTarea(UUID tarea, int tiempoRealDias, String descripcionReal);

    boolean existePorId(UUID planReal);

    void actualizarPorId(UUID planReal, int tiempoRealDias, String descripcionReal);

    void eliminarPorId(UUID planReal);
}
