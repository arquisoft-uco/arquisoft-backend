package com.arquisoft.biblioteca.infrastructure.documentacion.query.secondaryadapter.repository.mapper;

import com.arquisoft.biblioteca.application.documentacion.query.readmodel.DocumentacionReadModel;
import com.arquisoft.biblioteca.infrastructure.documentacion.query.secondaryadapter.repository.AutorDeDocumentacionJpaQueryEntity;
import com.arquisoft.biblioteca.infrastructure.documentacion.query.secondaryadapter.repository.DocumentacionJpaQueryEntity;
import com.arquisoft.biblioteca.infrastructure.documentacion.query.secondaryadapter.repository.EstadoEntregablePersistencia;

import java.util.List;

public final class DocumentacionQueryMapper {

    private DocumentacionQueryMapper() {}

    public static DocumentacionReadModel toReadModel(
            DocumentacionJpaQueryEntity entity, List<AutorDeDocumentacionJpaQueryEntity> autores) {
        return new DocumentacionReadModel(
                entity.getId(),
                entity.getTituloProyecto(),
                entity.getFechaPublicacion(),
                new DocumentacionReadModel.EntregableProyectoGradoResumen(
                        entity.getEntregableId(),
                        entity.getProyectoGradoId(),
                        entity.getEntregableVersion().intValue(),
                        EstadoEntregablePersistencia.nombreDe(entity.getEntregableEstadoId())),
                new DocumentacionReadModel.BibliotecarioResumen(
                        entity.getBibliotecarioId(),
                        entity.getBibliotecarioNombre()),
                new DocumentacionReadModel.EstadoDocumentacionResumen(
                        entity.getEstadoId(),
                        entity.getEstadoNombre()),
                autores.stream()
                        .map(autor -> new DocumentacionReadModel.AutorResumen(
                                autor.getAutorId(), autor.getIdentificador(), autor.getNombre()))
                        .toList());
    }
}
