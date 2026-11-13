package com.arquisoft.biblioteca.infrastructure.documentacion.query.primaryadapter.web.mapper;

import com.arquisoft.biblioteca.application.documentacion.query.readmodel.EnlaceDocumentacionReadModel;
import com.arquisoft.biblioteca.infrastructure.documentacion.query.primaryadapter.web.dto.DocumentacionResponseDTO;
import com.arquisoft.biblioteca.infrastructure.documentacion.query.primaryadapter.web.dto.EnlaceDocumentacionResponseDTO;

public final class EnlaceDocumentacionResponseMapper {

    private EnlaceDocumentacionResponseMapper() {}

    public static EnlaceDocumentacionResponseDTO toResponse(EnlaceDocumentacionReadModel readModel) {
        return new EnlaceDocumentacionResponseDTO(
                readModel.enlace(),
                readModel.id(),
                new DocumentacionResponseDTO.EntregableProyectoGradoResponseDTO(
                        readModel.entregableProyectoGrado().id(),
                        readModel.entregableProyectoGrado().proyectoGrado(),
                        readModel.entregableProyectoGrado().versionEntregable(),
                        readModel.entregableProyectoGrado().estadoEntregable()),
                new DocumentacionResponseDTO.BibliotecarioResponseDTO(
                        readModel.bibliotecario().id(),
                        readModel.bibliotecario().nombre()),
                new DocumentacionResponseDTO.EstadoDocumentacionResponseDTO(
                        readModel.estadoDocumentacion().id(),
                        readModel.estadoDocumentacion().nombre()));
    }
}
