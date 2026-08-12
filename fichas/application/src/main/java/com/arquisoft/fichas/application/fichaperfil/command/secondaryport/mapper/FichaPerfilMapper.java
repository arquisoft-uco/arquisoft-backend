package com.arquisoft.fichas.application.fichaperfil.command.secondaryport.mapper;

import com.arquisoft.fichas.application.asesorficha.command.secondaryport.mapper.AsesorFichaMapper;
import com.arquisoft.fichas.application.asesorficha.query.AsesorFichaReadModel;
import com.arquisoft.fichas.application.fichaperfil.command.secondaryport.entity.FichaPerfilEntity;
import com.arquisoft.fichas.application.fichaperfil.query.readmodel.FichaPerfilReadModel;
import com.arquisoft.fichas.domain.fichaperfil.FichaPerfilDomain;

public final class FichaPerfilMapper {

    private FichaPerfilMapper() {}

    public static FichaPerfilDomain toDomain(FichaPerfilEntity entity) {
        return FichaPerfilDomain.reconstruir(
                entity.getId(),
                entity.getTituloProyecto(),
                entity.getAsesorFicha().getId()
        );
    }

    public static FichaPerfilReadModel toReadModel(FichaPerfilEntity entity) {
        return FichaPerfilReadModel.builder()
                .id(entity.getId())
                .tituloProyecto(entity.getTituloProyecto())
                .asesorFicha(AsesorFichaReadModel.builder()
                        .id(entity.getAsesorFicha().getId())
                        .identificador(entity.getAsesorFicha().getIdentificador())
                        .nombre(entity.getAsesorFicha().getNombre())
                        .email(entity.getAsesorFicha().getEmail())
                        .build())
                .build();
    }

    public static FichaPerfilEntity toEntity(FichaPerfilDomain domain) {
        return FichaPerfilEntity.builder()
                .id(domain.getId())
                .tituloProyecto(domain.getTituloProyecto())
                .asesorFicha(AsesorFichaMapper.toReferencia(domain.getAsesorFicha()))
                .build();
    }
}
