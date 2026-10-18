package com.arquisoft.solicitudes.application.respuesta.command.primaryport.mapper;

import com.arquisoft.shared.validation.DomainValidationException;
import com.arquisoft.shared.message.constant.SolicitudesCodes;
import com.arquisoft.shared.message.constant.SolicitudesFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.solicitudes.application.respuesta.command.primaryport.model.ModificarEstadoRespuestaCambioAsesorCommand;
import com.arquisoft.solicitudes.domain.estadorespuesta.EstadoRespuesta;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ModificarEstadoRespuestaCambioAsesorMapperTest {

    @Test
    void debeFijarTipoCambioDeAsesor_cuandoMapeaElCommand() {
        // Arrange
        var solicitud = UtilUUID.generarNuevoUUID();
        var coordinador = UtilUUID.generarNuevoUUID();
        var command = ModificarEstadoRespuestaCambioAsesorCommand.crear(
                solicitud.toString(), "APROBADA", coordinador);

        // Act
        var dominio = ModificarEstadoRespuestaCambioAsesorMapper.toDomain(command);

        // Assert
        assertThat(dominio.getSolicitud()).isEqualTo(solicitud);
        assertThat(dominio.getResponsableUsuario()).isEqualTo(coordinador);
        assertThat(dominio.getTipoEsperado()).isEqualTo(TipoSolicitud.CAMBIO_DE_ASESOR);
        assertThat(dominio.getNuevoEstado()).isEqualTo(EstadoRespuesta.APROBADA);
    }

    @Test
    void debeLanzarValidacion_cuandoElEstadoNoExisteEnElCatalogo() {
        // Arrange
        var command = ModificarEstadoRespuestaCambioAsesorCommand.crear(
                UtilUUID.generarNuevoUUID().toString(), "XYZ", UtilUUID.generarNuevoUUID());

        // Act
        var excepcion = assertThrows(DomainValidationException.class,
                () -> ModificarEstadoRespuestaCambioAsesorMapper.toDomain(command));

        // Assert
        assertThat(excepcion.getValidationResult().getErrores()).anySatisfy(error -> {
            assertThat(error.campo()).isEqualTo(SolicitudesFields.Respuesta.ESTADO);
            assertThat(error.codigoError()).isEqualTo(SolicitudesCodes.EstadoRespuesta.ESTADO_NO_ENCONTRADO);
        });
    }
}
