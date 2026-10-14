package com.arquisoft.solicitudes.application.respuesta.command.primaryport.mapper;

import com.arquisoft.solicitudes.application.respuesta.command.primaryport.model.ModificarEstadoRespuestaNovedadAsesorCommand;
import com.arquisoft.solicitudes.domain.estadorespuesta.EstadoRespuesta;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ModificarEstadoRespuestaNovedadAsesorMapperTest {

    @Test
    void debeCopiarSolicitudResponsableNuevoEstadoYTipo_cuandoMapeaElComando() {
        // Arrange
        var solicitud = UUID.randomUUID();
        var asesor = UUID.randomUUID();
        var command = ModificarEstadoRespuestaNovedadAsesorCommand.crear(
                solicitud.toString(), "NO_APROBADA", asesor);

        // Act
        var dominio = ModificarEstadoRespuestaNovedadAsesorMapper.toDomain(command);

        // Assert
        assertThat(dominio.getSolicitud()).isEqualTo(solicitud);
        assertThat(dominio.getResponsableUsuario()).isEqualTo(asesor);
        assertThat(dominio.getTipoEsperado()).isEqualTo(TipoSolicitud.NOVEDAD_PARA_EL_ASESOR);
        assertThat(dominio.getNuevoEstado()).isEqualTo(EstadoRespuesta.NO_APROBADA);
    }
}
