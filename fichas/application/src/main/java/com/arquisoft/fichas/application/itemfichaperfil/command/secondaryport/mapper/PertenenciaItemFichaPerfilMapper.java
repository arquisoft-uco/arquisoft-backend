package com.arquisoft.fichas.application.itemfichaperfil.command.secondaryport.mapper;

import com.arquisoft.fichas.application.estadofichaperfil.command.secondaryport.entity.EstadoFichaPerfilEntity;
import com.arquisoft.fichas.application.estadofichaperfil.command.secondaryport.mapper.EstadoFichaPerfilMapper;
import com.arquisoft.fichas.application.itemfichaperfil.command.secondaryport.entity.PertenenciaItemFichaPerfilEntity;
import com.arquisoft.fichas.domain.estadofichaperfil.EstadoFichaPerfilDomain;
import com.arquisoft.fichas.domain.itemfichaperfil.model.PertenenciaItemFichaPerfil;
import com.arquisoft.shared.util.UtilObjeto;

public final class PertenenciaItemFichaPerfilMapper {

    private PertenenciaItemFichaPerfilMapper() {}

    public static PertenenciaItemFichaPerfil toDomain(PertenenciaItemFichaPerfilEntity entity) {
        return new PertenenciaItemFichaPerfil(
                entity.fichaPerfilId(),
                entity.esPropietario(),
                estadoActual(entity));
    }

    private static EstadoFichaPerfilDomain estadoActual(PertenenciaItemFichaPerfilEntity entity) {
        return UtilObjeto.esNulo(entity.estadoId())
                ? EstadoFichaPerfilDomain.VACIO
                : EstadoFichaPerfilMapper.toDomain(new EstadoFichaPerfilEntity(
                        entity.estadoId(), entity.fichaPerfilId(),
                        entity.estadoFicha(), entity.fechaActualizacion()));
    }
}
