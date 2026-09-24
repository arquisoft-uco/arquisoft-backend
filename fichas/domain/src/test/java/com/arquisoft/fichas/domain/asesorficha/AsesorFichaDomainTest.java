package com.arquisoft.fichas.domain.asesorficha;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AsesorFichaDomainTest {

    @Test
    void debeCrearAsesorFicha_cuandoLosDatosSonValidos() {
        // Arrange
        var id = UUID.randomUUID();
        var ocurridoEn = Instant.now();

        // Act
        var asesorFicha = AsesorFichaDomain.crear(
                id, "20161020123", "Juan Pérez", "juan.perez@example.com", ocurridoEn);

        // Assert
        assertThat(asesorFicha.getId()).isEqualTo(id);
        assertThat(asesorFicha.getIdentificador()).isEqualTo("20161020123");
        assertThat(asesorFicha.getNombre()).isEqualTo("Juan Pérez");
        assertThat(asesorFicha.getEmail()).isEqualTo("juan.perez@example.com");
        assertThat(asesorFicha.getOcurridoEn()).isEqualTo(ocurridoEn);
    }

    @Test
    void debeAcumularErrores_cuandoIdentificadorYEmailFaltan() {
        // Act & Assert
        assertThatThrownBy(() -> AsesorFichaDomain.crear(
                UUID.randomUUID(), " ", "Juan Pérez", " ", Instant.now()))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> {
                    var errores = ((DomainValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores)
                            .extracting(e -> e.campo())
                            .contains(FichasFields.AsesorFicha.IDENTIFICADOR, FichasFields.AsesorFicha.EMAIL);
                    assertThat(errores)
                            .extracting(e -> e.codigoError())
                            .contains(FichasCodes.AsesorFicha.IDENTIFICADOR_REQUERIDO,
                                    FichasCodes.AsesorFicha.EMAIL_REQUERIDO);
                });
    }

    @Test
    void debeLanzarValidacion_cuandoOcurridoEnEsNulo() {
        // Act & Assert
        assertThatThrownBy(() -> AsesorFichaDomain.crear(
                UUID.randomUUID(), "20161020123", "Juan Pérez", "juan.perez@example.com", null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> {
                    var errores = ((DomainValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores)
                            .anySatisfy(e -> assertThat(e.campo())
                                    .isEqualTo(FichasFields.AsesorFicha.OCURRIDO_EN));
                });
    }

    @Test
    void debeReconstruir_cuandoDatosValidos() {
        // Arrange
        var id = UUID.randomUUID();
        var identificador = "1234567890";
        var nombre = "Juan Pérez";
        var email = "juan.perez@example.com";
        var ocurridoEn = Instant.now();

        // Act
        var asesorFicha = AsesorFichaDomain.reconstruir(id, identificador, nombre, email, ocurridoEn);

        // Assert
        assertThat(asesorFicha).isNotNull();
        assertThat(asesorFicha.getId()).isEqualTo(id);
        assertThat(asesorFicha.getIdentificador()).isEqualTo(identificador);
        assertThat(asesorFicha.getNombre()).isEqualTo(nombre);
        assertThat(asesorFicha.getEmail()).isEqualTo(email);
        assertThat(asesorFicha.getOcurridoEn()).isEqualTo(ocurridoEn);
    }

    @Test
    void debeReconstruirSinValidar_cuandoReconstruirEsInvocado() {
        // Arrange
        var id = UUID.randomUUID();
        String identificador = null;
        String nombre = null;
        String email = null;
        Instant ocurridoEn = null;

        // Act
        var asesorFicha = AsesorFichaDomain.reconstruir(id, identificador, nombre, email, ocurridoEn);

        // Assert
        assertThat(asesorFicha).isNotNull();
        assertThat(asesorFicha.getId()).isEqualTo(id);
        assertThat(asesorFicha.getIdentificador()).isNull();
        assertThat(asesorFicha.getNombre()).isNull();
        assertThat(asesorFicha.getEmail()).isNull();
        assertThat(asesorFicha.getOcurridoEn()).isNull();
    }

    @Test
    void debeReportarNoVacio_cuandoSeCreaUnaInstanciaNormal() {
        // Act
        var asesorFicha = AsesorFichaDomain.crear(
                UUID.randomUUID(), "20161020123", "Juan Pérez", "juan.perez@example.com", Instant.now());

        // Assert
        assertThat(asesorFicha.esVacio()).isFalse();
        assertThat(AsesorFichaDomain.VACIO.esVacio()).isTrue();
    }

    @Test
    void debeActualizarDatosYOcurridoEn_cuandoActualizarEsInvocado() {
        // Arrange
        var id = UUID.randomUUID();
        var asesorFicha = AsesorFichaDomain.reconstruir(
                id, "20161020123", "Juan Pérez", "juan.perez@example.com", Instant.now());
        var nuevoOcurridoEn = Instant.now().plusSeconds(60);

        // Act
        asesorFicha.actualizar("20161020999", "Juan Actualizado", "actualizado@example.com", nuevoOcurridoEn);

        // Assert
        assertThat(asesorFicha.getIdentificador()).isEqualTo("20161020999");
        assertThat(asesorFicha.getNombre()).isEqualTo("Juan Actualizado");
        assertThat(asesorFicha.getEmail()).isEqualTo("actualizado@example.com");
        assertThat(asesorFicha.getOcurridoEn()).isEqualTo(nuevoOcurridoEn);
        assertThat(asesorFicha.getId()).isEqualTo(id);
    }

    @Test
    void debeAcumularErrores_cuandoActualizarRecibeDatosInvalidos() {
        // Arrange
        var asesorFicha = AsesorFichaDomain.reconstruir(
                UUID.randomUUID(), "20161020123", "Juan Pérez", "juan.perez@example.com", Instant.now());

        // Act & Assert
        assertThatThrownBy(() -> asesorFicha.actualizar(" ", "Juan Pérez", " ", Instant.now()))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> {
                    var errores = ((DomainValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores)
                            .extracting(e -> e.campo())
                            .contains(FichasFields.AsesorFicha.IDENTIFICADOR, FichasFields.AsesorFicha.EMAIL);
                });
    }
}
