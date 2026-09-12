package com.arquisoft.fichas.domain.estudiante;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EstudianteDomainTest {

    @Test
    void debeCrearEstudiante_cuandoLosDatosSonValidos() {
        // Arrange
        var id = UUID.randomUUID();
        var ocurridoEn = Instant.now();

        // Act
        var estudiante = EstudianteDomain.crear(
                id, "20161020123", "Juan Pérez", "juan.perez@example.com", ocurridoEn);

        // Assert
        assertThat(estudiante.getId()).isEqualTo(id);
        assertThat(estudiante.getIdentificador()).isEqualTo("20161020123");
        assertThat(estudiante.getNombre()).isEqualTo("Juan Pérez");
        assertThat(estudiante.getEmail()).isEqualTo("juan.perez@example.com");
        assertThat(estudiante.getOcurridoEn()).isEqualTo(ocurridoEn);
    }

    @Test
    void debeAcumularErrores_cuandoIdentificadorYEmailFaltan() {
        // Act & Assert
        assertThatThrownBy(() -> EstudianteDomain.crear(
                UUID.randomUUID(), " ", "Juan Pérez", " ", Instant.now()))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> {
                    var errores = ((DomainValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores)
                            .extracting(e -> e.campo())
                            .contains(FichasFields.Estudiante.IDENTIFICADOR, FichasFields.Estudiante.EMAIL);
                    assertThat(errores)
                            .extracting(e -> e.codigoError())
                            .contains(FichasCodes.Estudiante.IDENTIFICADOR_REQUERIDO,
                                    FichasCodes.Estudiante.EMAIL_REQUERIDO);
                });
    }

    @Test
    void debeLanzarValidacion_cuandoOcurridoEnEsNulo() {
        // Act & Assert
        assertThatThrownBy(() -> EstudianteDomain.crear(
                UUID.randomUUID(), "20161020123", "Juan Pérez", "juan.perez@example.com", null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> {
                    var errores = ((DomainValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores)
                            .anySatisfy(e -> assertThat(e.campo())
                                    .isEqualTo(FichasFields.Estudiante.OCURRIDO_EN));
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
        var estudiante = EstudianteDomain.reconstruir(id, identificador, nombre, email, ocurridoEn);

        // Assert
        assertThat(estudiante).isNotNull();
        assertThat(estudiante.getId()).isEqualTo(id);
        assertThat(estudiante.getIdentificador()).isEqualTo(identificador);
        assertThat(estudiante.getNombre()).isEqualTo(nombre);
        assertThat(estudiante.getEmail()).isEqualTo(email);
        assertThat(estudiante.getOcurridoEn()).isEqualTo(ocurridoEn);
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
        var estudiante = EstudianteDomain.reconstruir(id, identificador, nombre, email, ocurridoEn);

        // Assert
        assertThat(estudiante).isNotNull();
        assertThat(estudiante.getId()).isEqualTo(id);
        assertThat(estudiante.getIdentificador()).isNull();
        assertThat(estudiante.getNombre()).isNull();
        assertThat(estudiante.getEmail()).isNull();
        assertThat(estudiante.getOcurridoEn()).isNull();
    }
}
