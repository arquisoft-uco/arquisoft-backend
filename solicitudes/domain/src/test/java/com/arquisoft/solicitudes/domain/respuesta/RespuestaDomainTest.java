package com.arquisoft.solicitudes.domain.respuesta;

import com.arquisoft.shared.message.constant.SolicitudesCodes;
import com.arquisoft.shared.validation.DomainValidationException;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.solicitudes.domain.estadorespuesta.EstadoRespuesta;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RespuestaDomainTest {

    private static boolean tieneCodigo(ValidationResult resultado, String codigo) {
        return resultado.getErrores().stream().anyMatch(e -> e.codigoError().equals(codigo));
    }

    @Test
    void debeNacerEnRevision_cuandoLosDatosSonValidos() {
        // Arrange
        UUID solicitud = UUID.randomUUID();

        // Act
        RespuestaDomain respuesta = RespuestaDomain.crear(solicitud, "Contenido valido");

        // Assert
        assertThat(respuesta.getEstadoRespuesta()).isEqualTo(EstadoRespuesta.EN_REVISION);
        assertThat(respuesta.getSolicitud()).isEqualTo(solicitud);
        assertThat(respuesta.getId()).isNotNull();
        assertThat(respuesta.getFechaRespuesta()).isNotNull();
        assertThat(respuesta.getContenido()).isEqualTo("Contenido valido");
        assertThat(respuesta.esVacio()).isFalse();
    }

    @Test
    void debeRecortarElContenido_cuandoTieneEspaciosAlrededor() {
        // Act
        RespuestaDomain respuesta = RespuestaDomain.crear(UUID.randomUUID(), "  respuesta  ");

        // Assert
        assertThat(respuesta.getContenido()).isEqualTo("respuesta");
    }

    @Test
    void debeLanzar_cuandoElContenidoEstaEnBlanco() {
        // Act
        DomainValidationException excepcion = assertThrows(DomainValidationException.class,
                () -> RespuestaDomain.crear(UUID.randomUUID(), "   "));

        // Assert
        assertThat(tieneCodigo(excepcion.getValidationResult(),
                SolicitudesCodes.Respuesta.CONTENIDO_REQUERIDO)).isTrue();
    }

    @Test
    void debeLanzar_cuandoElContenidoExcede100Caracteres() {
        // Arrange
        String largo = "x".repeat(101);

        // Act
        DomainValidationException excepcion = assertThrows(DomainValidationException.class,
                () -> RespuestaDomain.crear(UUID.randomUUID(), largo));

        // Assert
        assertThat(tieneCodigo(excepcion.getValidationResult(),
                SolicitudesCodes.Respuesta.CONTENIDO_DEMASIADO_LARGO)).isTrue();
    }

    @Test
    void debeAcumularAmbosErrores_cuandoSolicitudYContenidoSonInvalidos() {
        // Act
        DomainValidationException excepcion = assertThrows(DomainValidationException.class,
                () -> RespuestaDomain.crear(null, ""));

        // Assert
        var resultado = excepcion.getValidationResult();
        assertThat(tieneCodigo(resultado, SolicitudesCodes.Respuesta.SOLICITUD_REQUERIDO)).isTrue();
        assertThat(tieneCodigo(resultado, SolicitudesCodes.Respuesta.CONTENIDO_REQUERIDO)).isTrue();
    }

    @Test
    void debeReconstruirSinValidar_cuandoVieneDesdeLaPersistencia() {
        // Arrange
        UUID id = UUID.randomUUID();
        UUID solicitud = UUID.randomUUID();

        // Act
        RespuestaDomain respuesta = RespuestaDomain.reconstruir(
                id, solicitud, null, "", EstadoRespuesta.APROBADA);

        // Assert
        assertThat(respuesta.getId()).isEqualTo(id);
        assertThat(respuesta.getSolicitud()).isEqualTo(solicitud);
        assertThat(respuesta.getEstadoRespuesta()).isEqualTo(EstadoRespuesta.APROBADA);
    }
}
