package com.arquisoft.solicitudes.application.respuesta.command.secondaryport.mapper;

import com.arquisoft.solicitudes.application.respuesta.command.secondaryport.entity.RespuestaEntity;
import com.arquisoft.solicitudes.domain.estadorespuesta.EstadoRespuesta;
import com.arquisoft.solicitudes.domain.respuesta.RespuestaDomain;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RespuestaMapperTest {

    @Test
    void debeConvertirElDominioAEntity_persistiendoElIdDelEstado() {
        // Arrange
        RespuestaDomain domain = RespuestaDomain.crear(UUID.randomUUID(), "contenido");

        // Act
        RespuestaEntity entity = RespuestaMapper.toEntity(domain);

        // Assert
        assertThat(entity.id()).isEqualTo(domain.getId());
        assertThat(entity.solicitud()).isEqualTo(domain.getSolicitud());
        assertThat(entity.contenido()).isEqualTo("contenido");
        assertThat(entity.estadoRespuesta()).isEqualTo(EstadoRespuesta.EN_REVISION.getId());
    }

    @Test
    void debeReconstruirElDominio_resolviendoElEstadoDesdeElCatalogo() {
        // Arrange
        UUID id = UUID.randomUUID();
        UUID solicitud = UUID.randomUUID();
        LocalDateTime fecha = LocalDateTime.of(2026, 3, 1, 9, 0, 0);
        var entity = new RespuestaEntity(id, solicitud, fecha, "contenido", "APROBADA");

        // Act
        RespuestaDomain domain = RespuestaMapper.toDomain(entity);

        // Assert
        assertThat(domain.getId()).isEqualTo(id);
        assertThat(domain.getSolicitud()).isEqualTo(solicitud);
        assertThat(domain.getFechaRespuesta()).isEqualTo(fecha);
        assertThat(domain.getContenido()).isEqualTo("contenido");
        assertThat(domain.getEstadoRespuesta()).isEqualTo(EstadoRespuesta.APROBADA);
    }
}
