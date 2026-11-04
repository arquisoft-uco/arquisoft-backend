package com.arquisoft.solicitudes.infrastructure.respuesta.command.primaryadapter.web.mapper;

import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import com.arquisoft.solicitudes.infrastructure.respuesta.command.primaryadapter.web.dto.ModificarEstadoRespuestaAmpliacionPlazoRequestDTO;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ModificarEstadoRespuestaAmpliacionPlazoRequestMapperTest {

    @Test
    void debeConstruirElCommand_cuandoLosDatosSonValidos() {
        // Arrange
        var solicitud = UtilUUID.generarNuevoUUID();
        var coordinador = UtilUUID.generarNuevoUUID();
        var dto = new ModificarEstadoRespuestaAmpliacionPlazoRequestDTO("APROBADA");

        // Act
        var command = ModificarEstadoRespuestaAmpliacionPlazoRequestMapper.toCommand(
                dto, solicitud.toString(), coordinador);

        // Assert
        assertThat(command.solicitud()).isEqualTo(solicitud);
        assertThat(command.nuevoEstado()).isEqualTo("APROBADA");
        assertThat(command.coordinadorUsuario()).isEqualTo(coordinador);
    }

    @Test
    void debeLanzarValidacion_cuandoElSolicitudIdNoEsUuid() {
        // Arrange
        var dto = new ModificarEstadoRespuestaAmpliacionPlazoRequestDTO("APROBADA");
        var coordinador = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThrows(ApplicationValidationException.class,
                () -> ModificarEstadoRespuestaAmpliacionPlazoRequestMapper.toCommand(dto, "no-es-uuid", coordinador));
    }
}
