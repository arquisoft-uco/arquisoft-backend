package com.arquisoft.biblioteca.infrastructure.documentacion.query.secondaryadapter.repository.mapper;

import com.arquisoft.biblioteca.application.documentacion.query.readmodel.DocumentacionReadModel;
import com.arquisoft.biblioteca.infrastructure.documentacion.query.secondaryadapter.repository.DocumentacionJpaQueryEntity;

public final class DocumentacionQueryMapper {

    private DocumentacionQueryMapper() {}

    public static DocumentacionReadModel toReadModel(DocumentacionJpaQueryEntity entity) {
        return new DocumentacionReadModel(
                entity.getId(),
                entity.getUrl(),
                entity.getFechaPublicacion(),
                new DocumentacionReadModel.EntregableProyectoGradoResumen(
                        entity.getEntregableId(),
                        entity.getProyectoGradoId(),
                        entity.getEntregableVersion().intValue(),
                        entity.getEntregableEstado()),
                new DocumentacionReadModel.BibliotecarioResumen(
                        entity.getBibliotecarioId(),
                        entity.getBibliotecarioNombre()),
                new DocumentacionReadModel.EstadoDocumentacionResumen(
                        entity.getEstadoId(),
                        entity.getEstadoNombre()));
    }
}
