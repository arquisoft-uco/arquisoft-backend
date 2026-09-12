package com.arquisoft.solicitudes.infrastructure.respuesta.command.secondaryadapter.mapper;

import com.arquisoft.solicitudes.application.respuesta.command.secondaryport.entity.RespuestaEntity;
import com.arquisoft.solicitudes.infrastructure.respuesta.command.secondaryadapter.entity.EstadoRespuestaJpaEntity;
import com.arquisoft.solicitudes.infrastructure.respuesta.command.secondaryadapter.entity.RespuestaJpaEntity;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RespuestaJpaMapperTest {

    @Test
    void debeConvertirLaEntityADjpaEntity_conLaReferenciaDelEstado() {
        // Arrange
        UUID id = UUID.randomUUID();
        UUID solicitud = UUID.randomUUID();
        LocalDateTime fecha = LocalDateTime.of(2026, 3, 1, 9, 0, 0);
        var entity = new RespuestaEntity(id, solicitud, fecha, "contenido", "EN_REVISION");

        // Act
        RespuestaJpaEntity jpa = RespuestaJpaMapper.toJpaEntity(entity);

        // Assert
        assertThat(jpa.getId()).isEqualTo(id);
        assertThat(jpa.getSolicitudId()).isEqualTo(solicitud);
        assertThat(jpa.getFechaRespuesta()).isEqualTo(fecha);
        assertThat(jpa.getContenido()).isEqualTo("contenido");
        assertThat(jpa.getEstadoRespuesta().getId()).isEqualTo("EN_REVISION");
    }

    @Test
    void debeAplanarLaJpaEntity_cuandoConvierteDesdeElAgregadoJpa() {
        // Arrange
        UUID id = UUID.randomUUID();
        UUID solicitud = UUID.randomUUID();
        LocalDateTime fecha = LocalDateTime.of(2026, 3, 1, 9, 0, 0);
        RespuestaJpaEntity jpa = RespuestaJpaEntity.builder()
                .id(id)
                .solicitudId(solicitud)
                .fechaRespuesta(fecha)
                .contenido("contenido")
                .estadoRespuesta(EstadoRespuestaJpaEntity.builder().id("APROBADA").build())
                .build();

        // Act
        RespuestaEntity entity = RespuestaJpaMapper.toEntity(jpa);

        // Assert
        assertThat(entity).isEqualTo(new RespuestaEntity(id, solicitud, fecha, "contenido", "APROBADA"));
    }
}
