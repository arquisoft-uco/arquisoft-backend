package com.arquisoft.solicitudes.application.respuesta.command.primaryport.model;

import com.arquisoft.shared.message.constant.SolicitudesFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EliminarRespuestaRegistroModificacionUsuariosCommandTest {

    @Test
    void debeCrearElComando_cuandoLosDatosSonValidos() {
        // Arrange
        var solicitud = UtilUUID.generarNuevoUUID();
        var administrador = UtilUUID.generarNuevoUUID();

        // Act
        var command = EliminarRespuestaRegistroModificacionUsuariosCommand.crear(solicitud.toString(), administrador);

        // Assert
        assertThat(command.solicitud()).isEqualTo(solicitud);
        assertThat(command.administradorUsuario()).isEqualTo(administrador);
    }

    @Test
    void debeAcumularLosErroresDeEntrada_cuandoAmbosIdentificadoresSonInvalidos() {
        // Act & Assert
        assertThatThrownBy(() -> EliminarRespuestaRegistroModificacionUsuariosCommand.crear("  ", null))
                .isInstanceOfSatisfying(ApplicationValidationException.class, excepcion -> {
                    var resultado = excepcion.getValidationResult();
                    assertThat(resultado.tieneErroresDeCampo(SolicitudesFields.Solicitud.ID)).isTrue();
                    assertThat(resultado.tieneErroresDeCampo(SolicitudesFields.Solicitud.DESTINATARIO)).isTrue();
                });
    }

    @Test
    void debeLanzarErrorDeEntrada_cuandoLaSolicitudNoEsUuid() {
        // Arrange
        var administrador = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> EliminarRespuestaRegistroModificacionUsuariosCommand.crear("no-es-uuid", administrador))
                .isInstanceOfSatisfying(ApplicationValidationException.class, excepcion ->
                        assertThat(excepcion.getValidationResult()
                                .tieneErroresDeCampo(SolicitudesFields.Solicitud.ID)).isTrue());
    }
}
