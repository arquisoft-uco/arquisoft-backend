package com.arquisoft.solicitudes.infrastructure.respuesta.query.primaryadapter.web.mapper;

import com.arquisoft.solicitudes.application.destinatario.query.readmodel.DestinatarioReadModel;
import com.arquisoft.solicitudes.application.remitente.query.readmodel.RemitenteReadModel;
import com.arquisoft.solicitudes.application.respuesta.query.readmodel.RespuestaReadModel;
import com.arquisoft.solicitudes.application.solicitud.query.readmodel.SolicitudReadModel;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RespuestaResponseMapperTest {

    @Test
    void debeMapearElReadModelAlResponseDTO_incluyendoSolicitudRemitenteYDestinatarioAnidados() {
        // Arrange
        var remitente = new RemitenteReadModel(UUID.randomUUID(), "EST-1", "Ana", "ana@uco.edu.co");
        var destinatario = new DestinatarioReadModel(
                UUID.randomUUID(), "COORD-1", "Coordinadora", "coord@uco.edu.co");
        var solicitud = new SolicitudReadModel(UUID.randomUUID(), "una novedad",
                LocalDateTime.of(2026, 3, 1, 10, 0), "NOVEDAD_PARA_EL_COORDINADOR",
                "Novedad para el Coordinador", remitente, destinatario);
        var readModel = new RespuestaReadModel(UUID.randomUUID(), "contenido de la respuesta",
                LocalDateTime.of(2026, 3, 5, 9, 0), "EN_REVISION", "En revisión", solicitud);

        // Act
        var dto = RespuestaResponseMapper.toResponse(readModel);

        // Assert
        assertThat(dto.id()).isEqualTo(readModel.id());
        assertThat(dto.contenido()).isEqualTo("contenido de la respuesta");
        assertThat(dto.fechaRespuesta()).isEqualTo(readModel.fechaRespuesta());
        assertThat(dto.estadoRespuestaId()).isEqualTo("EN_REVISION");
        assertThat(dto.estadoRespuestaNombre()).isEqualTo("En revisión");
        assertThat(dto.solicitud().id()).isEqualTo(solicitud.id());
        assertThat(dto.solicitud().mensajeSolicitud()).isEqualTo("una novedad");
        assertThat(dto.solicitud().tipoSolicitudId()).isEqualTo("NOVEDAD_PARA_EL_COORDINADOR");
        assertThat(dto.solicitud().tipoSolicitudNombre()).isEqualTo("Novedad para el Coordinador");
        assertThat(dto.solicitud().remitente().nombre()).isEqualTo("Ana");
        assertThat(dto.solicitud().remitente().email()).isEqualTo("ana@uco.edu.co");
        assertThat(dto.solicitud().destinatario().identificador()).isEqualTo("COORD-1");
        assertThat(dto.solicitud().destinatario().nombre()).isEqualTo("Coordinadora");
        assertThat(dto.solicitud().destinatario().email()).isEqualTo("coord@uco.edu.co");
    }
}
