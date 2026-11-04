package com.arquisoft.mapas_ruta.application.planestimado.command.secondaryport;

import com.arquisoft.mapas_ruta.application.planestimado.command.secondaryport.entity.PlanEstimadoEntity;

import java.util.UUID;

public interface PlanEstimadoOutputPort {

    void registrar(PlanEstimadoEntity planEstimado);

    boolean existePorTarea(UUID tarea);

    boolean existePorId(UUID planEstimado);

    void actualizarDescripcion(UUID planEstimado, String descripcionEstimado);

    void eliminar(UUID planEstimado);

    long sumarDiasEstimadosPorMapaRutaDeTarea(UUID tarea);
}
