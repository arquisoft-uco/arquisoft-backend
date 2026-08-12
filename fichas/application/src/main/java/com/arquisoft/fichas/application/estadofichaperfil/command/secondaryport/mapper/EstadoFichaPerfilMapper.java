package com.arquisoft.fichas.application.estadofichaperfil.command.secondaryport.mapper;

import com.arquisoft.fichas.application.estadoficha.command.secondaryport.entity.EstadoFichaEntity;
import com.arquisoft.fichas.application.estadofichaperfil.command.secondaryport.entity.EstadoFichaPerfilEntity;
import com.arquisoft.fichas.domain.estadoficha.EstadoFicha;
import com.arquisoft.fichas.domain.estadofichaperfil.EstadoFichaPerfilDomain;

public final class EstadoFichaPerfilMapper {

    private EstadoFichaPerfilMapper() {}

    public static EstadoFichaPerfilEntity toEntity(EstadoFichaPerfilDomain aggregate) {
        return EstadoFichaPerfilEntity.builder()
                .id(aggregate.getId())
                .fichaPerfilId(aggregate.getFichaPerfil())
                .estadoFicha(EstadoFichaEntity.builder()
                        .id(aggregate.getEstadoFicha().getId())
                        .build())
                .fechaActualizacion(aggregate.getFechaActualizacion())
                .build();
    }

    public static EstadoFichaPerfilDomain toDomain(EstadoFichaPerfilEntity entity) {
        return EstadoFichaPerfilDomain.reconstruir(
                entity.getId(),
                entity.getFichaPerfilId(),
                convertir(entity.getEstadoFicha().getId()),
                entity.getFechaActualizacion()
        );
    }

    /**
     * Un identificador del catalogo que ya no tiene constante equivalente degrada a
     * {@link EstadoFicha#VACIO} en lugar de romper: la regla de dominio ya trata ese valor como
     * "estado no encontrado" (422) y propagar un {@code IllegalArgumentException} lo convertiria
     * en un 500.
     */
    private static EstadoFicha convertir(String estadoFichaId) {
        try {
            return EstadoFicha.valueOf(estadoFichaId);
        } catch (IllegalArgumentException ex) {
            return EstadoFicha.VACIO;
        }
    }
}
