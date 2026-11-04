package com.arquisoft.solicitudes.application.respuesta.command.primaryport.mapper;

import com.arquisoft.shared.validation.DomainValidationException;
import com.arquisoft.shared.message.constant.SolicitudesCodes;
import com.arquisoft.shared.message.constant.SolicitudesFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.solicitudes.application.respuesta.command.primaryport.model.ModificarEstadoRespuestaAmpliacionPlazoCommand;
import com.arquisoft.solicitudes.domain.estadorespuesta.EstadoRespuesta;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ModificarEstadoRespuestaAmpliacionPlazoMapperTest {

    @Test
    void debeFijarTipoAmpliacionDePlazo_cuandoMapeaElCommand() {
        // Arrange
        var solicitud = UtilUUID.generarNuevoUUID();
        var coordinador = UtilUUID.generarNuevoUUID();
        var command = ModificarEstadoRespuestaAmpliacionPlazoCommand.crear(
                solicitud.toString(), "APROBADA", coordinador);

        // Act
        var dominio = ModificarEstadoRespuestaAmpliacionPlazoMapper.toDomain(command);

        // Assert
        assertThat(dominio.getSolicitud()).isEqualTo(solicitud);
        assertThat(dominio.getResponsableUsuario()).isEqualTo(coordinador);
        assertThat(dominio.getTipoEsperado()).isEqualTo(TipoSolicitud.AMPLIACION_DE_PLAZO);
        assertThat(dominio.getNuevoEstado()).isEqualTo(EstadoRespuesta.APROBADA);
    }

    @Test
    void debeLanzarValidacion_cuandoElEstadoNoExisteEnElCatalogo() {
        // Arrange
        var command = ModificarEstadoRespuestaAmpliacionPlazoCommand.crear(
                UtilUUID.generarNuevoUUID().toString(), "XYZ", UtilUUID.generarNuevoUUID());

        // Act
        var excepcion = assertThrows(DomainValidationException.class,
                () -> ModificarEstadoRespuestaAmpliacionPlazoMapper.toDomain(command));

        // Assert
        assertThat(excepcion.getValidationResult().getErrores()).anySatisfy(error -> {
            assertThat(error.campo()).isEqualTo(SolicitudesFields.Respuesta.ESTADO);
            assertThat(error.codigoError()).isEqualTo(SolicitudesCodes.EstadoRespuesta.ESTADO_NO_ENCONTRADO);
        });
    }
}
