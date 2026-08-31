package com.arquisoft.notificaciones.infrastructure.notificacion.command.secondaryadapter.mapper;

import com.arquisoft.notificaciones.application.notificacion.command.secondaryport.entity.NotificacionEntity;
import com.arquisoft.notificaciones.infrastructure.notificacion.command.secondaryadapter.entity.NotificacionJpaEntity;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilObjeto;
import com.arquisoft.shared.util.UtilTexto;

import java.time.Instant;

public final class NotificacionJpaMapper {

    private NotificacionJpaMapper() {}

    public static NotificacionEntity toEntity(NotificacionJpaEntity jpaEntity) {
        return new NotificacionEntity(
                jpaEntity.getId(),
                jpaEntity.getIdEvento(),
                jpaEntity.getTipo(),
                jpaEntity.getDestinatario(),
                jpaEntity.getAsunto(),
                jpaEntity.getDestinatarioNombre(),
                jpaEntity.getCuerpo(),
                jpaEntity.getEstado(),
                UtilTexto.aplicarTrim(jpaEntity.getDetalleError()),
                jpaEntity.getFechaCreacion(),
                UtilObjeto.aplicarPorDefecto(jpaEntity.getFechaEnvio(), UtilFecha.VACIO),
                jpaEntity.getIntentos(),
                UtilObjeto.aplicarPorDefecto(jpaEntity.getFechaUltimoIntento(), UtilFecha.VACIO));
    }

    public static NotificacionJpaEntity toJpaEntity(NotificacionEntity entity) {
        return NotificacionJpaEntity.builder()
                .id(entity.id())
                .idEvento(entity.idEvento())
                .tipo(entity.tipo())
                .destinatario(entity.destinatario())
                .asunto(entity.asunto())
                .destinatarioNombre(entity.destinatarioNombre())
                .cuerpo(entity.cuerpo())
                .estado(entity.estado())
                .detalleError(aColumna(entity.detalleError()))
                .fechaCreacion(entity.fechaCreacion())
                .fechaEnvio(aColumna(entity.fechaEnvio()))
                .intentos(entity.intentos())
                .fechaUltimoIntento(aColumna(entity.fechaUltimoIntento()))
                .build();
    }

    private static String aColumna(String texto) {
        return UtilTexto.esVacioONulo(texto) ? null : texto;
    }

    private static Instant aColumna(Instant instante) {
        return UtilObjeto.esNulo(instante) || UtilFecha.VACIO.equals(instante) ? null : instante;
    }
}
