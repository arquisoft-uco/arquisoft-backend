package com.arquisoft.evaluaciones.domain.jurado;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JuradoDomainTest {

    private static final Instant OCURRIDO_EN = Instant.parse("2026-09-24T10:00:00Z");

    @Test
    void debeCrearJuradoVigente_cuandoDatosValidos() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();

        // Act
        var jurado = JuradoDomain.crear(id, "20161020123", "Ana Perez", "ana@uco.edu.co", OCURRIDO_EN);

        // Assert
        assertThat(jurado.getId()).isEqualTo(id);
        assertThat(jurado.getIdentificador()).isEqualTo("20161020123");
        assertThat(jurado.getNombre()).isEqualTo("Ana Perez");
        assertThat(jurado.getEmail()).isEqualTo("ana@uco.edu.co");
        assertThat(jurado.getOcurridoEn()).isEqualTo(OCURRIDO_EN);
        assertThat(jurado.getEliminadoEn()).isEqualTo(UtilFecha.VACIO);
        assertThat(jurado.estaEliminado()).isFalse();
        assertThat(jurado.esVacio()).isFalse();
    }

    @Test
    void debeAcumularTodosLosErrores_cuandoVariosCamposSonInvalidos() {
        // Act & Assert
        assertThatThrownBy(() -> JuradoDomain.crear(null, " ", " ", " ", null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> {
                    var errores = ((DomainValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores).hasSize(5);
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo()).isEqualTo(EvaluacionesFields.Jurado.ID);
                        assertThat(error.codigoError()).isEqualTo(EvaluacionesCodes.Jurado.ID_REQUERIDO);
                    });
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo()).isEqualTo(EvaluacionesFields.Jurado.IDENTIFICADOR);
                        assertThat(error.codigoError())
                                .isEqualTo(EvaluacionesCodes.Jurado.IDENTIFICADOR_REQUERIDO);
                    });
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo()).isEqualTo(EvaluacionesFields.Jurado.NOMBRE);
                        assertThat(error.codigoError()).isEqualTo(EvaluacionesCodes.Jurado.NOMBRE_REQUERIDO);
                    });
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo()).isEqualTo(EvaluacionesFields.Jurado.EMAIL);
                        assertThat(error.codigoError()).isEqualTo(EvaluacionesCodes.Jurado.EMAIL_REQUERIDO);
                    });
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo()).isEqualTo(EvaluacionesFields.Jurado.OCURRIDO_EN);
                        assertThat(error.codigoError())
                                .isEqualTo(EvaluacionesCodes.Jurado.OCURRIDO_EN_REQUERIDO);
                    });
                });
    }

    @Test
    void debeReconstruirSinValidarYReflejarLaBaja_cuandoEliminadoEnEsNuloONo() {
        // Arrange
        var eliminadoEn = Instant.parse("2026-09-25T10:00:00Z");

        // Act
        var sinBaja = JuradoDomain.reconstruir(null, null, null, null, null, null);
        var conBaja = JuradoDomain.reconstruir(
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
        var jurado = JuradoDomain.reconstruir(
                id, "20161020123", "Ana Perez", "ana@uco.edu.co", OCURRIDO_EN, OCURRIDO_EN);
        var nuevoOcurridoEn = Instant.parse("2026-09-24T12:00:00Z");

        // Act
        jurado.reactivar("20161020999", "Ana Reactivada", "reactivada@uco.edu.co", nuevoOcurridoEn);

        // Assert
        assertThat(jurado.getId()).isEqualTo(id);
        assertThat(jurado.getIdentificador()).isEqualTo("20161020999");
        assertThat(jurado.getNombre()).isEqualTo("Ana Reactivada");
        assertThat(jurado.getEmail()).isEqualTo("reactivada@uco.edu.co");
        assertThat(jurado.getOcurridoEn()).isEqualTo(nuevoOcurridoEn);
        assertThat(jurado.getEliminadoEn()).isEqualTo(UtilFecha.VACIO);
        assertThat(jurado.estaEliminado()).isFalse();
    }

    @Test
    void debeAcumularErroresYConservarLaBaja_cuandoReactivarRecibeDatosInvalidos() {
        // Arrange
        var jurado = JuradoDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), "20161020123", "Ana Perez", "ana@uco.edu.co", OCURRIDO_EN, OCURRIDO_EN);

        // Act & Assert
        assertThatThrownBy(() -> jurado.reactivar(" ", " ", " ", null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .extracting(e -> e.campo())
                        .containsExactlyInAnyOrder(EvaluacionesFields.Jurado.IDENTIFICADOR,
                                EvaluacionesFields.Jurado.NOMBRE, EvaluacionesFields.Jurado.EMAIL,
                                EvaluacionesFields.Jurado.OCURRIDO_EN));
        assertThat(jurado.estaEliminado()).isTrue();
    }

    @Test
    void debeAsignarEliminadoEnYOcurridoEn_cuandoSeRemueve() {
        // Arrange
        var jurado = JuradoDomain.crear(
                UtilUUID.generarNuevoUUID(), "20161020123", "Ana Perez", "ana@uco.edu.co", OCURRIDO_EN);
        var nuevoOcurridoEn = Instant.parse("2026-09-24T12:00:00Z");

        // Act
        jurado.remover(nuevoOcurridoEn);

        // Assert
        assertThat(jurado.getEliminadoEn()).isEqualTo(nuevoOcurridoEn);
        assertThat(jurado.getOcurridoEn()).isEqualTo(nuevoOcurridoEn);
        assertThat(jurado.estaEliminado()).isTrue();
    }

    @Test
    void debeLanzarValidacionYConservarVigente_cuandoRemoverRecibeOcurridoEnNulo() {
        // Arrange
        var jurado = JuradoDomain.crear(
                UtilUUID.generarNuevoUUID(), "20161020123", "Ana Perez", "ana@uco.edu.co", OCURRIDO_EN);

        // Act & Assert
        assertThatThrownBy(() -> jurado.remover(null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .extracting(e -> e.campo())
                        .containsExactly(EvaluacionesFields.Jurado.OCURRIDO_EN));
        assertThat(jurado.estaEliminado()).isFalse();
        assertThat(jurado.getOcurridoEn()).isEqualTo(OCURRIDO_EN);
    }

    @Test
    void debeExponerCentinelaVacioNoEliminado_cuandoSeConsultaVacio() {
        // Act
        var vacio = JuradoDomain.VACIO;

        // Assert
        assertThat(vacio.esVacio()).isTrue();
        assertThat(vacio.estaEliminado()).isFalse();
    }
}
