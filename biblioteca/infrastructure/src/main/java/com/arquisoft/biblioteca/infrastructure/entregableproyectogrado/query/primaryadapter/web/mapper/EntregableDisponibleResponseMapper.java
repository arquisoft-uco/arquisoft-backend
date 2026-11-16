package com.arquisoft.biblioteca.infrastructure.entregableproyectogrado.query.primaryadapter.web.mapper;

import com.arquisoft.biblioteca.application.entregableproyectogrado.query.readmodel.EntregableDisponibleReadModel;
import com.arquisoft.biblioteca.infrastructure.entregableproyectogrado.query.primaryadapter.web.dto.EntregableDisponibleResponseDTO;
import com.arquisoft.biblioteca.infrastructure.entregableproyectogrado.query.primaryadapter.web.dto.EntregableDisponibleResponseDTO.AutorResponseDTO;

public final class EntregableDisponibleResponseMapper {

    private EntregableDisponibleResponseMapper() {}

    public static EntregableDisponibleResponseDTO toResponse(EntregableDisponibleReadModel readModel) {
        return new EntregableDisponibleResponseDTO(
                readModel.id(),
                readModel.proyectoGrado(),
                readModel.tituloProyecto(),
                readModel.versionEntregable(),
                readModel.nombreArchivo(),
                readModel.autores().stream()
                        .map(autor -> new AutorResponseDTO(autor.id(), autor.identificador(), autor.nombre()))
                        .toList());
    }
}
