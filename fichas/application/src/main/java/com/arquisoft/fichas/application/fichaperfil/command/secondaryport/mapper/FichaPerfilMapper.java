package com.arquisoft.fichas.application.fichaperfil.command.secondaryport.mapper;

import com.arquisoft.fichas.application.fichaperfil.command.secondaryport.entity.FichaPerfilEntity;
import com.arquisoft.fichas.domain.fichaperfil.FichaPerfilDomain;

public final class FichaPerfilMapper {

    private FichaPerfilMapper() {}

    public static FichaPerfilDomain toDomain(FichaPerfilEntity entity) {
        return FichaPerfilDomain.reconstruir(
                entity.id(),
                entity.tituloProyecto(),
                entity.asesorFicha()
        );
    }

    public static FichaPerfilEntity toEntity(FichaPerfilDomain domain) {
        return new FichaPerfilEntity(domain.getId(), domain.getTituloProyecto(), domain.getAsesorFicha());
    }
}
