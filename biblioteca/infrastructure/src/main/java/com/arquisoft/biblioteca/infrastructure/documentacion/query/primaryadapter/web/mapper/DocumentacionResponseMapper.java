package com.arquisoft.biblioteca.infrastructure.documentacion.query.primaryadapter.web.mapper;

import com.arquisoft.biblioteca.application.documentacion.query.readmodel.DocumentacionReadModel;
import com.arquisoft.biblioteca.infrastructure.documentacion.query.primaryadapter.web.dto.DocumentacionResponseDTO;

public final class DocumentacionResponseMapper {

    private DocumentacionResponseMapper() {}

    public static DocumentacionResponseDTO toResponse(DocumentacionReadModel readModel) {
        return new DocumentacionResponseDTO(
                readModel.id(),
                readModel.tituloProyecto(),
                readModel.fechaPublicacion(),
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
                        readModel.estadoDocumentacion().nombre()),
                readModel.autores().stream()
                        .map(autor -> new DocumentacionResponseDTO.AutorResponseDTO(
                                autor.id(), autor.identificador(), autor.nombre()))
                        .toList());
    }
}
