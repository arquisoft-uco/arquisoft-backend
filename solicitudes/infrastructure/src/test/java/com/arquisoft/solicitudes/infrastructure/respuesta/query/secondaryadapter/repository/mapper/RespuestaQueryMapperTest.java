package com.arquisoft.solicitudes.infrastructure.respuesta.query.secondaryadapter.repository.mapper;

import com.arquisoft.solicitudes.infrastructure.respuesta.query.secondaryadapter.repository.RespuestaJpaQueryEntity;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RespuestaQueryMapperTest {

    @Test
    void debeMapearTodaLaProyeccion_incluyendoSolicitudRemitenteYDestinatario() {
        // Arrange
        var entity = RespuestaJpaQueryEntity.builder()
                .id(UUID.randomUUID())
                .contenido("contenido de la respuesta")
                .fechaRespuesta(LocalDateTime.of(2026, 3, 5, 9, 0))
                .estadoRespuestaId("EN_REVISION")
                .estadoRespuestaNombre("En revisión")
                .solicitudId(UUID.randomUUID())
                .mensajeSolicitud("una novedad")
                .fechaCreacion(LocalDateTime.of(2026, 3, 1, 10, 0))
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
        var readModel = RespuestaQueryMapper.toReadModel(entity);

        // Assert
        assertThat(readModel.id()).isEqualTo(entity.getId());
        assertThat(readModel.contenido()).isEqualTo("contenido de la respuesta");
        assertThat(readModel.fechaRespuesta()).isEqualTo(entity.getFechaRespuesta());
        assertThat(readModel.estadoRespuestaId()).isEqualTo("EN_REVISION");
        assertThat(readModel.estadoRespuestaNombre()).isEqualTo("En revisión");
        assertThat(readModel.solicitud().id()).isEqualTo(entity.getSolicitudId());
        assertThat(readModel.solicitud().mensajeSolicitud()).isEqualTo("una novedad");
        assertThat(readModel.solicitud().fechaCreacion()).isEqualTo(entity.getFechaCreacion());
        assertThat(readModel.solicitud().tipoSolicitudId()).isEqualTo("NOVEDAD_PARA_EL_COORDINADOR");
        assertThat(readModel.solicitud().tipoSolicitudNombre()).isEqualTo("Novedad para el Coordinador");
        assertThat(readModel.solicitud().remitente().usuarioId()).isEqualTo(entity.getRemitenteUsuarioId());
        assertThat(readModel.solicitud().remitente().identificador()).isEqualTo("EST-1");
        assertThat(readModel.solicitud().remitente().nombre()).isEqualTo("Ana Estudiante");
        assertThat(readModel.solicitud().remitente().email()).isEqualTo("ana@uco.edu.co");
        assertThat(readModel.solicitud().destinatario().usuarioId()).isEqualTo(entity.getDestinatarioUsuarioId());
        assertThat(readModel.solicitud().destinatario().identificador()).isEqualTo("COORD-1");
        assertThat(readModel.solicitud().destinatario().nombre()).isEqualTo("Coordinadora");
        assertThat(readModel.solicitud().destinatario().email()).isEqualTo("coord@uco.edu.co");
    }
}
