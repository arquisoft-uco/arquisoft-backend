package com.arquisoft.fichas.infrastructure.fichaperfil.query.secondaryadapter.repository.mapper;

import com.arquisoft.fichas.application.asesorficha.query.readmodel.AsesorFichaReadModel;
import com.arquisoft.fichas.application.estadofichaperfil.query.readmodel.EstadoFichaPerfilReadModel;
import com.arquisoft.fichas.application.estudiantefichaperfil.query.readmodel.EstudianteFichaPerfilReadModel;
import com.arquisoft.fichas.application.fichaperfil.query.readmodel.FichaPerfilEstudianteReadModel;
import com.arquisoft.fichas.infrastructure.fichaperfil.query.secondaryadapter.repository.FichaPerfilEstudianteJpaQueryEntity;

import java.util.List;

public final class FichaPerfilEstudianteQueryMapper {

    private FichaPerfilEstudianteQueryMapper() {}

    public static FichaPerfilEstudianteReadModel toReadModel(
            FichaPerfilEstudianteJpaQueryEntity entity,
            List<EstudianteFichaPerfilReadModel> estudiantes) {

        return new FichaPerfilEstudianteReadModel(
                entity.getId(),
                entity.getTituloProyecto(),
                new AsesorFichaReadModel(
                        entity.getAsesorId(),
                        entity.getAsesorIdentificador(),
                        entity.getAsesorNombre(),
                        entity.getAsesorEmail()),
                new EstadoFichaPerfilReadModel(
                        entity.getEstadoId(),
                        entity.getEstadoNombre(),
                        entity.getEstadoFechaActualizacion()),
                estudiantes);
    }
}
