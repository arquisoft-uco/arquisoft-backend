package com.arquisoft.usuarios.infrastructure.jurado.command.secondaryadapter.mapper;

import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilObjeto;
import com.arquisoft.usuarios.application.jurado.command.secondaryport.entity.JuradoEntity;
import com.arquisoft.usuarios.infrastructure.jurado.command.secondaryadapter.entity.JuradoJpaEntity;

import java.time.Instant;

public final class JuradoJpaMapper {

    private JuradoJpaMapper() {}

    public static JuradoEntity toEntity(JuradoJpaEntity jpaEntity) {
        return new JuradoEntity(
                jpaEntity.getUsuarioId(),
                UtilObjeto.aplicarPorDefecto(jpaEntity.getEliminadoEn(), UtilFecha.VACIO));
    }

    public static JuradoJpaEntity toJpaEntity(JuradoEntity entity) {
        return JuradoJpaEntity.builder()
                .usuarioId(entity.usuario())
                .eliminadoEn(aColumna(entity.eliminadoEn()))
                .build();
    }

    public static Instant aColumna(Instant eliminadoEn) {
        return UtilFecha.VACIO.equals(eliminadoEn) ? null : eliminadoEn;
    }
}
