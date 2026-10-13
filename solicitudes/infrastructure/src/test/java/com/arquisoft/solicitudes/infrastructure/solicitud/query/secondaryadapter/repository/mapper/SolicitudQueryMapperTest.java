package com.arquisoft.solicitudes.infrastructure.solicitud.query.secondaryadapter.repository.mapper;

import com.arquisoft.solicitudes.infrastructure.solicitud.query.secondaryadapter.repository.SolicitudJpaQueryEntity;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class SolicitudQueryMapperTest {

    @Test
    void debeMapearTodaLaProyeccion_incluyendoRemitenteYDestinatario() {
        // Arrange
        var entity = SolicitudJpaQueryEntity.builder()
                .id(UUID.randomUUID())
                .mensajeSolicitud("una novedad")
                .fechaCreacion(Instant.parse("2026-03-01T10:00:00Z"))
                .tipoSolicitudId("NOVEDAD_PARA_EL_COORDINADOR")
                .tipoSolicitudNombre("Novedad para el Coordinador")
                .destinatarioUsuarioId(UUID.randomUUID())
                .destinatarioIdentificador("COORD-1")
                .destinatarioNombre("Coordinadora")
                .destinatarioEmail("coord@uco.edu.co")
                .remitenteUsuarioId(UUID.randomUUID())
                .remitenteIdentificador("EST-1")
                .remitenteNombre("Ana Estudiante")
                .remitenteEmail("ana@uco.edu.co")
                .build();

        // Act
        var readModel = SolicitudQueryMapper.toReadModel(entity);

        // Assert
        assertThat(readModel.id()).isEqualTo(entity.getId());
        assertThat(readModel.mensajeSolicitud()).isEqualTo("una novedad");
        assertThat(readModel.fechaCreacion()).isEqualTo(entity.getFechaCreacion());
        assertThat(readModel.tipoSolicitudId()).isEqualTo("NOVEDAD_PARA_EL_COORDINADOR");
        assertThat(readModel.tipoSolicitudNombre()).isEqualTo("Novedad para el Coordinador");
        assertThat(readModel.remitente().usuarioId()).isEqualTo(entity.getRemitenteUsuarioId());
        assertThat(readModel.remitente().identificador()).isEqualTo("EST-1");
        assertThat(readModel.remitente().nombre()).isEqualTo("Ana Estudiante");
        assertThat(readModel.remitente().email()).isEqualTo("ana@uco.edu.co");
        assertThat(readModel.destinatario().usuarioId()).isEqualTo(entity.getDestinatarioUsuarioId());
        assertThat(readModel.destinatario().identificador()).isEqualTo("COORD-1");
        assertThat(readModel.destinatario().nombre()).isEqualTo("Coordinadora");
        assertThat(readModel.destinatario().email()).isEqualTo("coord@uco.edu.co");
    }
}
