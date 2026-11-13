package com.arquisoft.biblioteca.infrastructure.autor.command.secondaryadapter.mapper;

import com.arquisoft.biblioteca.application.autor.command.secondaryport.entity.AutorEntity;
import com.arquisoft.biblioteca.infrastructure.autor.command.secondaryadapter.entity.AutorJpaEntity;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilObjeto;

import java.time.Instant;

public final class AutorJpaMapper {

    private AutorJpaMapper() {}

    public static AutorEntity toEntity(AutorJpaEntity jpaEntity) {
        return new AutorEntity(
                jpaEntity.getId(),
                jpaEntity.getIdentificador(),
                jpaEntity.getNombre(),
                jpaEntity.getEmail(),
                jpaEntity.getOcurridoEn(),
                UtilObjeto.aplicarPorDefecto(jpaEntity.getEliminadoEn(), UtilFecha.VACIO));
    }

    public static AutorJpaEntity toJpaEntity(AutorEntity entity) {
        return AutorJpaEntity.builder()
                .id(entity.id())
                .identificador(entity.identificador())
                .nombre(entity.nombre())
                .email(entity.email())
                .ocurridoEn(entity.ocurridoEn())
                .eliminadoEn(aColumna(entity.eliminadoEn()))
                .build();
    }

    public static Instant aColumna(Instant eliminadoEn) {
        return UtilFecha.VACIO.equals(eliminadoEn) ? null : eliminadoEn;
    }
}
