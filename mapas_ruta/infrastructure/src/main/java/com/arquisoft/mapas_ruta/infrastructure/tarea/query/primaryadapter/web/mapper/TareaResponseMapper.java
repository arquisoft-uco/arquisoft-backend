package com.arquisoft.mapas_ruta.infrastructure.tarea.query.primaryadapter.web.mapper;

import com.arquisoft.mapas_ruta.application.tarea.query.readmodel.TareaReadModel;
import com.arquisoft.mapas_ruta.infrastructure.tarea.query.primaryadapter.web.dto.TareaResponseDTO;

public final class TareaResponseMapper {

    private TareaResponseMapper() {}

    public static TareaResponseDTO toResponse(TareaReadModel r) {
        return new TareaResponseDTO(
                r.id(),
                r.descripcion(),
                r.mapaRuta(),
                r.fechaInicioMapaRuta(),
                r.fechaFinMapaRuta(),
                r.estadoItem(),
                r.nombreEstadoItem(),
                r.asesor(),
                r.nombreAsesor(),
                r.emailAsesor());
    }
}
