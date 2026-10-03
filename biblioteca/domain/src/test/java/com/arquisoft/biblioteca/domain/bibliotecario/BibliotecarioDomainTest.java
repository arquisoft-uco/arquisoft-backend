package com.arquisoft.biblioteca.domain.bibliotecario;

import com.arquisoft.shared.message.constant.BibliotecaCodes;
import com.arquisoft.shared.message.constant.BibliotecaFields;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BibliotecarioDomainTest {

    private static final Instant OCURRIDO_EN = Instant.parse("2026-09-24T10:00:00Z");

    @Test
    void debeCrearBibliotecarioVigente_cuandoDatosValidos() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();

        // Act
        var bibliotecario = BibliotecarioDomain.crear(id, "20161020123", "Ana Perez", "ana@uco.edu.co", OCURRIDO_EN);

        // Assert
        assertThat(bibliotecario.getId()).isEqualTo(id);
        assertThat(bibliotecario.getIdentificador()).isEqualTo("20161020123");
        assertThat(bibliotecario.getNombre()).isEqualTo("Ana Perez");
        assertThat(bibliotecario.getEmail()).isEqualTo("ana@uco.edu.co");
        assertThat(bibliotecario.getOcurridoEn()).isEqualTo(OCURRIDO_EN);
        assertThat(bibliotecario.getEliminadoEn()).isEqualTo(UtilFecha.VACIO);
        assertThat(bibliotecario.estaEliminado()).isFalse();
        assertThat(bibliotecario.esVacio()).isFalse();
    }

    @Test
    void debeAcumularTodosLosErrores_cuandoVariosCamposSonInvalidos() {
        // Act & Assert
        assertThatThrownBy(() -> BibliotecarioDomain.crear(null, " ", " ", " ", null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> {
                    var errores = ((DomainValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores).hasSize(5);
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo()).isEqualTo(BibliotecaFields.Bibliotecario.ID);
                        assertThat(error.codigoError()).isEqualTo(BibliotecaCodes.Bibliotecario.ID_REQUERIDO);
                    });
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo()).isEqualTo(BibliotecaFields.Bibliotecario.IDENTIFICADOR);
                        assertThat(error.codigoError())
                                .isEqualTo(BibliotecaCodes.Bibliotecario.IDENTIFICADOR_REQUERIDO);
                    });
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo()).isEqualTo(BibliotecaFields.Bibliotecario.NOMBRE);
                        assertThat(error.codigoError()).isEqualTo(BibliotecaCodes.Bibliotecario.NOMBRE_REQUERIDO);
                    });
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo()).isEqualTo(BibliotecaFields.Bibliotecario.EMAIL);
                        assertThat(error.codigoError()).isEqualTo(BibliotecaCodes.Bibliotecario.EMAIL_REQUERIDO);
                    });
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo()).isEqualTo(BibliotecaFields.Bibliotecario.OCURRIDO_EN);
                        assertThat(error.codigoError())
                                .isEqualTo(BibliotecaCodes.Bibliotecario.OCURRIDO_EN_REQUERIDO);
                    });
                });
    }

    @Test
    void debeReconstruirSinValidarYReflejarLaBaja_cuandoEliminadoEnEsNuloONo() {
        // Arrange
        var eliminadoEn = Instant.parse("2026-09-25T10:00:00Z");

        // Act
        var sinBaja = BibliotecarioDomain.reconstruir(null, null, null, null, null, null);
        var conBaja = BibliotecarioDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), "20161020123", "Ana Perez", "ana@uco.edu.co", OCURRIDO_EN, eliminadoEn);

        // Assert
        assertThat(sinBaja.getId()).isNull();
        assertThat(sinBaja.getEliminadoEn()).isEqualTo(UtilFecha.VACIO);
        assertThat(sinBaja.estaEliminado()).isFalse();
        assertThat(sinBaja.esVacio()).isFalse();
        assertThat(conBaja.getEliminadoEn()).isEqualTo(eliminadoEn);
        assertThat(conBaja.estaEliminado()).isTrue();
    }

    @Test
    void debeActualizarDatosYLimpiarLaBaja_cuandoSeReactiva() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var bibliotecario = BibliotecarioDomain.reconstruir(
                id, "20161020123", "Ana Perez", "ana@uco.edu.co", OCURRIDO_EN, OCURRIDO_EN);
        var nuevoOcurridoEn = Instant.parse("2026-09-24T12:00:00Z");

        // Act
        bibliotecario.reactivar("20161020999", "Ana Reactivada", "reactivada@uco.edu.co", nuevoOcurridoEn);

        // Assert
        assertThat(bibliotecario.getId()).isEqualTo(id);
        assertThat(bibliotecario.getIdentificador()).isEqualTo("20161020999");
        assertThat(bibliotecario.getNombre()).isEqualTo("Ana Reactivada");
        assertThat(bibliotecario.getEmail()).isEqualTo("reactivada@uco.edu.co");
        assertThat(bibliotecario.getOcurridoEn()).isEqualTo(nuevoOcurridoEn);
        assertThat(bibliotecario.getEliminadoEn()).isEqualTo(UtilFecha.VACIO);
        assertThat(bibliotecario.estaEliminado()).isFalse();
    }

    @Test
    void debeAcumularErroresYConservarLaBaja_cuandoReactivarRecibeDatosInvalidos() {
        // Arrange
        var bibliotecario = BibliotecarioDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), "20161020123", "Ana Perez", "ana@uco.edu.co", OCURRIDO_EN, OCURRIDO_EN);

        // Act & Assert
        assertThatThrownBy(() -> bibliotecario.reactivar(" ", " ", " ", null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .extracting(e -> e.campo())
                        .containsExactlyInAnyOrder(BibliotecaFields.Bibliotecario.IDENTIFICADOR,
                                BibliotecaFields.Bibliotecario.NOMBRE, BibliotecaFields.Bibliotecario.EMAIL,
                                BibliotecaFields.Bibliotecario.OCURRIDO_EN));
        assertThat(bibliotecario.estaEliminado()).isTrue();
    }

    @Test
    void debeExponerCentinelaVacioNoEliminado_cuandoSeConsultaVacio() {
        // Act
        var vacio = BibliotecarioDomain.VACIO;

        // Assert
        assertThat(vacio.esVacio()).isTrue();
        assertThat(vacio.estaEliminado()).isFalse();
    }
}
