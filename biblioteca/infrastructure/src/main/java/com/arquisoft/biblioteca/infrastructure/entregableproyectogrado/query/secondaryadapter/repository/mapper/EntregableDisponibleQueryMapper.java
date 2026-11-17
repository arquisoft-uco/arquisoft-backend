package com.arquisoft.biblioteca.infrastructure.entregableproyectogrado.query.secondaryadapter.repository.mapper;

import com.arquisoft.biblioteca.application.entregableproyectogrado.query.readmodel.EntregableDisponibleReadModel;
import com.arquisoft.biblioteca.application.entregableproyectogrado.query.readmodel.EntregableDisponibleReadModel.AutorResumen;
import com.arquisoft.biblioteca.infrastructure.entregableproyectogrado.query.secondaryadapter.repository.AutorEntregableDisponibleJpaQueryEntity;
import com.arquisoft.biblioteca.infrastructure.entregableproyectogrado.query.secondaryadapter.repository.EntregableDisponibleJpaQueryEntity;

import java.util.List;

public final class EntregableDisponibleQueryMapper {

    private EntregableDisponibleQueryMapper() {}

    public static EntregableDisponibleReadModel toReadModel(
            EntregableDisponibleJpaQueryEntity entity, List<AutorEntregableDisponibleJpaQueryEntity> autores) {
        return new EntregableDisponibleReadModel(
                entity.getId(),
                entity.getProyectoGradoId(),
                entity.getTituloProyecto(),
                Math.toIntExact(entity.getVersionEntregable()),
                entity.getNombreArchivo(),
                autores.stream()
                        .map(autor -> new AutorResumen(autor.getAutorId(), autor.getIdentificador(), autor.getNombre()))
                        .toList());
    }
}
