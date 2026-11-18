package com.arquisoft.biblioteca.domain.autor;

import com.arquisoft.shared.message.constant.BibliotecaCodes;
import com.arquisoft.shared.message.constant.BibliotecaFields;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AutorDomainTest {

    private static final Instant OCURRIDO_EN = Instant.parse("2026-09-24T10:00:00Z");

    @Test
    void debeCrearAutorVigente_cuandoDatosValidos() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();

        // Act
        var autor = AutorDomain.crear(id, "20161020123", "Ana Perez", "ana@uco.edu.co", OCURRIDO_EN);

        // Assert
        assertThat(autor.getId()).isEqualTo(id);
        assertThat(autor.getIdentificador()).isEqualTo("20161020123");
        assertThat(autor.getNombre()).isEqualTo("Ana Perez");
        assertThat(autor.getEmail()).isEqualTo("ana@uco.edu.co");
        assertThat(autor.getOcurridoEn()).isEqualTo(OCURRIDO_EN);
        assertThat(autor.getEliminadoEn()).isEqualTo(UtilFecha.VACIO);
        assertThat(autor.estaEliminado()).isFalse();
        assertThat(autor.esVacio()).isFalse();
    }

    @Test
    void debeAcumularTodosLosErrores_cuandoVariosCamposSonInvalidos() {
        // Act & Assert
        assertThatThrownBy(() -> AutorDomain.crear(null, " ", " ", " ", null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> {
                    var errores = ((DomainValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores).hasSize(5);
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo()).isEqualTo(BibliotecaFields.Autor.ID);
                        assertThat(error.codigoError()).isEqualTo(BibliotecaCodes.Autor.ID_REQUERIDO);
                    });
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo()).isEqualTo(BibliotecaFields.Autor.IDENTIFICADOR);
                        assertThat(error.codigoError())
                                .isEqualTo(BibliotecaCodes.Autor.IDENTIFICADOR_REQUERIDO);
                    });
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo()).isEqualTo(BibliotecaFields.Autor.NOMBRE);
                        assertThat(error.codigoError()).isEqualTo(BibliotecaCodes.Autor.NOMBRE_REQUERIDO);
                    });
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo()).isEqualTo(BibliotecaFields.Autor.EMAIL);
                        assertThat(error.codigoError()).isEqualTo(BibliotecaCodes.Autor.EMAIL_REQUERIDO);
                    });
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo()).isEqualTo(BibliotecaFields.Autor.OCURRIDO_EN);
                        assertThat(error.codigoError())
                                .isEqualTo(BibliotecaCodes.Autor.OCURRIDO_EN_REQUERIDO);
                    });
                });
    }

    @Test
    void debeReconstruirSinValidarYReflejarLaBaja_cuandoEliminadoEnEsNuloONo() {
        // Arrange
        var eliminadoEn = Instant.parse("2026-09-25T10:00:00Z");

        // Act
        var sinBaja = AutorDomain.reconstruir(null, null, null, null, null, null);
        var conBaja = AutorDomain.reconstruir(
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
    void debeAsignarEliminadoEnYOcurridoEn_cuandoSeRemueve() {
        // Arrange
        var autor = AutorDomain.crear(
                UtilUUID.generarNuevoUUID(), "20161020123", "Ana Perez", "ana@uco.edu.co", OCURRIDO_EN);
        var nuevoOcurridoEn = Instant.parse("2026-09-24T12:00:00Z");

        // Act
        autor.remover(nuevoOcurridoEn);

        // Assert
        assertThat(autor.getEliminadoEn()).isEqualTo(nuevoOcurridoEn);
        assertThat(autor.getOcurridoEn()).isEqualTo(nuevoOcurridoEn);
        assertThat(autor.estaEliminado()).isTrue();
    }

    @Test
    void debeActualizarDatosYLimpiarLaBaja_cuandoSeReactiva() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var autor = AutorDomain.reconstruir(
                id, "20161020123", "Ana Perez", "ana@uco.edu.co", OCURRIDO_EN, OCURRIDO_EN);
        var nuevoOcurridoEn = Instant.parse("2026-09-24T12:00:00Z");

        // Act
        autor.reactivar("20161020999", "Ana Reactivada", "reactivada@uco.edu.co", nuevoOcurridoEn);

        // Assert
        assertThat(autor.getId()).isEqualTo(id);
        assertThat(autor.getIdentificador()).isEqualTo("20161020999");
        assertThat(autor.getNombre()).isEqualTo("Ana Reactivada");
        assertThat(autor.getEmail()).isEqualTo("reactivada@uco.edu.co");
        assertThat(autor.getOcurridoEn()).isEqualTo(nuevoOcurridoEn);
        assertThat(autor.getEliminadoEn()).isEqualTo(UtilFecha.VACIO);
        assertThat(autor.estaEliminado()).isFalse();
    }

    @Test
    void debeAcumularErroresYConservarLaBaja_cuandoReactivarRecibeDatosInvalidos() {
        // Arrange
        var autor = AutorDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), "20161020123", "Ana Perez", "ana@uco.edu.co", OCURRIDO_EN, OCURRIDO_EN);

        // Act & Assert
        assertThatThrownBy(() -> autor.reactivar(" ", " ", " ", null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .extracting(e -> e.campo())
                        .containsExactlyInAnyOrder(BibliotecaFields.Autor.IDENTIFICADOR,
                                BibliotecaFields.Autor.NOMBRE, BibliotecaFields.Autor.EMAIL,
                                BibliotecaFields.Autor.OCURRIDO_EN));
        assertThat(autor.estaEliminado()).isTrue();
    }

    @Test
    void debeExponerCentinelaVacioNoEliminado_cuandoSeConsultaVacio() {
        // Act
        var vacio = AutorDomain.VACIO;

        // Assert
        assertThat(vacio.esVacio()).isTrue();
        assertThat(vacio.estaEliminado()).isFalse();
    }
}
