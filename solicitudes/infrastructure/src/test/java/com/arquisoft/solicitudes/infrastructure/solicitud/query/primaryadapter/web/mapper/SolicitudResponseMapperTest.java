package com.arquisoft.solicitudes.infrastructure.solicitud.query.primaryadapter.web.mapper;

import com.arquisoft.solicitudes.application.destinatario.query.readmodel.DestinatarioReadModel;
import com.arquisoft.solicitudes.application.remitente.query.readmodel.RemitenteReadModel;
import com.arquisoft.solicitudes.application.solicitud.query.readmodel.SolicitudReadModel;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class SolicitudResponseMapperTest {

    @Test
    void debeMapearElReadModelAlResponseDTO_incluyendoRemitenteYDestinatario() {
        // Arrange
        var remitente = new RemitenteReadModel(UUID.randomUUID(), "EST-1", "Ana", "ana@uco.edu.co");
        var destinatario = new DestinatarioReadModel(
                UUID.randomUUID(), "COORD-1", "Coordinadora", "coord@uco.edu.co");
        var readModel = new SolicitudReadModel(UUID.randomUUID(), "una novedad",
                LocalDateTime.of(2026, 3, 1, 10, 0), "NOVEDAD_PARA_EL_COORDINADOR",
                "Novedad para el Coordinador", remitente, destinatario);

        // Act
        var dto = SolicitudResponseMapper.toResponse(readModel);

        // Assert
        assertThat(dto.id()).isEqualTo(readModel.id());
        assertThat(dto.mensajeSolicitud()).isEqualTo("una novedad");
        assertThat(dto.fechaCreacion()).isEqualTo(readModel.fechaCreacion());
        assertThat(dto.tipoSolicitudId()).isEqualTo("NOVEDAD_PARA_EL_COORDINADOR");
        assertThat(dto.tipoSolicitudNombre()).isEqualTo("Novedad para el Coordinador");
        assertThat(dto.remitente().usuarioId()).isEqualTo(remitente.usuarioId());
        assertThat(dto.remitente().identificador()).isEqualTo("EST-1");
        assertThat(dto.remitente().nombre()).isEqualTo("Ana");
        assertThat(dto.remitente().email()).isEqualTo("ana@uco.edu.co");
        assertThat(dto.destinatario().usuarioId()).isEqualTo(destinatario.usuarioId());
        assertThat(dto.destinatario().identificador()).isEqualTo("COORD-1");
        assertThat(dto.destinatario().nombre()).isEqualTo("Coordinadora");
        assertThat(dto.destinatario().email()).isEqualTo("coord@uco.edu.co");
    }
}
