package com.arquisoft.fichas.domain.coordinador;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CoordinadorDomainTest {

    @Test
    void debeCrearCoordinador_cuandoLosDatosSonValidos() {
        // Arrange
        var id = UUID.randomUUID();
        var ocurridoEn = Instant.now();

        // Act
        var coordinador = CoordinadorDomain.crear(
                id, "20161020123", "Juan Perez", "juan.perez@example.com", ocurridoEn);

        // Assert
        assertThat(coordinador.getId()).isEqualTo(id);
        assertThat(coordinador.getIdentificador()).isEqualTo("20161020123");
        assertThat(coordinador.getNombre()).isEqualTo("Juan Perez");
        assertThat(coordinador.getEmail()).isEqualTo("juan.perez@example.com");
        assertThat(coordinador.getOcurridoEn()).isEqualTo(ocurridoEn);
    }

    @Test
    void debeAcumularErrores_cuandoIdentificadorYEmailFaltan() {
        // Act & Assert
        assertThatThrownBy(() -> CoordinadorDomain.crear(
                UUID.randomUUID(), " ", "Juan Perez", " ", Instant.now()))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> {
                    var errores = ((DomainValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores)
                            .extracting(e -> e.campo())
                            .contains(FichasFields.Coordinador.IDENTIFICADOR, FichasFields.Coordinador.EMAIL);
                    assertThat(errores)
                            .extracting(e -> e.codigoError())
                            .contains(FichasCodes.Coordinador.IDENTIFICADOR_REQUERIDO,
                                    FichasCodes.Coordinador.EMAIL_REQUERIDO);
                });
    }

    @Test
    void debeLanzarValidacion_cuandoOcurridoEnEsNulo() {
        // Act & Assert
        assertThatThrownBy(() -> CoordinadorDomain.crear(
                UUID.randomUUID(), "20161020123", "Juan Perez", "juan.perez@example.com", null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> {
                    var errores = ((DomainValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores)
                            .anySatisfy(e -> assertThat(e.campo())
                                    .isEqualTo(FichasFields.Coordinador.OCURRIDO_EN));
                });
    }

    @Test
    void debeReconstruirSinValidar_cuandoReconstruirEsInvocado() {
        // Arrange
        var id = UUID.randomUUID();

        // Act
        var coordinador = CoordinadorDomain.reconstruir(id, null, null, null, null);

        // Assert
        assertThat(coordinador.getId()).isEqualTo(id);
        assertThat(coordinador.getIdentificador()).isNull();
        assertThat(coordinador.getNombre()).isNull();
        assertThat(coordinador.getEmail()).isNull();
        assertThat(coordinador.getOcurridoEn()).isNull();
    }

    @Test
    void debeReportarNoVacio_cuandoSeCreaUnaInstanciaNormal() {
        // Act
        var coordinador = CoordinadorDomain.crear(
                UUID.randomUUID(), "20161020123", "Juan Perez", "juan.perez@example.com", Instant.now());

        // Assert
        assertThat(coordinador.esVacio()).isFalse();
        assertThat(CoordinadorDomain.VACIO.esVacio()).isTrue();
    }
}
