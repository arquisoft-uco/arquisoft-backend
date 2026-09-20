package com.arquisoft.solicitudes.application.respuesta.command.primaryport.mapper;

import com.arquisoft.solicitudes.application.respuesta.command.primaryport.model.EliminarRespuestaNovedadAsesorCommand;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EliminarRespuestaNovedadAsesorMapperTest {

    @Test
    void debeCopiarSolicitudYAsesorUsuario_cuandoMapeaElComando() {
        // Arrange
        var solicitud = UUID.randomUUID();
        var asesor = UUID.randomUUID();
        var command = EliminarRespuestaNovedadAsesorCommand.crear(solicitud.toString(), asesor);

        // Act
        var dominio = EliminarRespuestaNovedadAsesorMapper.toDomain(command);

        // Assert
        assertThat(dominio.getSolicitud()).isEqualTo(solicitud);
        assertThat(dominio.getAsesorUsuario()).isEqualTo(asesor);
    }
}
