package com.arquisoft.solicitudes.application.respuesta.command.primaryport.model;

import com.arquisoft.shared.message.constant.SolicitudesFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EliminarRespuestaAmpliacionPlazoCommandTest {

    @Test
    void debeCrearElComando_cuandoLosDatosSonValidos() {
        // Arrange
        var solicitud = UtilUUID.generarNuevoUUID();
        var coordinador = UtilUUID.generarNuevoUUID();

        // Act
        var command = EliminarRespuestaAmpliacionPlazoCommand.crear(solicitud.toString(), coordinador);

        // Assert
        assertThat(command.solicitud()).isEqualTo(solicitud);
        assertThat(command.coordinadorUsuario()).isEqualTo(coordinador);
    }

    @Test
    void debeAcumularLosErroresDeEntrada_cuandoAmbosIdentificadoresSonInvalidos() {
        // Act & Assert
        assertThatThrownBy(() -> EliminarRespuestaAmpliacionPlazoCommand.crear("  ", null))
                .isInstanceOfSatisfying(ApplicationValidationException.class, excepcion -> {
                    var resultado = excepcion.getValidationResult();
                    assertThat(resultado.tieneErroresDeCampo(SolicitudesFields.Solicitud.ID)).isTrue();
                    assertThat(resultado.tieneErroresDeCampo(SolicitudesFields.Solicitud.DESTINATARIO)).isTrue();
                });
    }

    @Test
    void debeLanzarErrorDeEntrada_cuandoLaSolicitudNoEsUuid() {
        // Arrange
        var coordinador = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> EliminarRespuestaAmpliacionPlazoCommand.crear("no-es-uuid", coordinador))
                .isInstanceOfSatisfying(ApplicationValidationException.class, excepcion ->
                        assertThat(excepcion.getValidationResult()
                                .tieneErroresDeCampo(SolicitudesFields.Solicitud.ID)).isTrue());
    }
}
