package com.arquisoft.solicitudes.infrastructure.respuesta.command.primaryadapter.web.mapper;

import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.solicitudes.infrastructure.respuesta.command.primaryadapter.web.dto.ResponderSolicitudAmpliacionPlazoRequestDTO;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ResponderSolicitudAmpliacionPlazoRequestMapperTest {

    @Test
    void debeArmarElComandoConLosTresDatos_cuandoMapeaElRequest() {
        // Arrange
        var solicitud = UtilUUID.generarNuevoUUID();
        var coordinador = UtilUUID.generarNuevoUUID();
        var dto = new ResponderSolicitudAmpliacionPlazoRequestDTO("Se aprueba la ampliacion");

        // Act
        var command = ResponderSolicitudAmpliacionPlazoRequestMapper.toCommand(
                dto, solicitud.toString(), coordinador);

        // Assert
        assertThat(command.solicitud()).isEqualTo(solicitud);
        assertThat(command.contenido()).isEqualTo("Se aprueba la ampliacion");
        assertThat(command.coordinadorUsuario()).isEqualTo(coordinador);
    }
}
