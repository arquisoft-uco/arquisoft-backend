package com.arquisoft.evaluaciones.domain.asesor;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AsesorDomainTest {

    private static final Instant OCURRIDO_EN = Instant.parse("2026-09-24T10:00:00Z");

    @Test
    void debeCrearAsesorVigente_cuandoDatosValidos() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();

        // Act
        var asesor = AsesorDomain.crear(id, "20161020123", "Ana Perez", "ana@uco.edu.co", OCURRIDO_EN);

        // Assert
        assertThat(asesor.getId()).isEqualTo(id);
        assertThat(asesor.getIdentificador()).isEqualTo("20161020123");
        assertThat(asesor.getNombre()).isEqualTo("Ana Perez");
        assertThat(asesor.getEmail()).isEqualTo("ana@uco.edu.co");
        assertThat(asesor.getOcurridoEn()).isEqualTo(OCURRIDO_EN);
        assertThat(asesor.getEliminadoEn()).isEqualTo(UtilFecha.VACIO);
        assertThat(asesor.estaEliminado()).isFalse();
        assertThat(asesor.esVacio()).isFalse();
    }

    @Test
    void debeAcumularTodosLosErrores_cuandoVariosCamposSonInvalidos() {
        // Act & Assert
        assertThatThrownBy(() -> AsesorDomain.crear(null, " ", " ", " ", null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> {
                    var errores = ((DomainValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores).hasSize(5);
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo()).isEqualTo(EvaluacionesFields.Asesor.ID);
                        assertThat(error.codigoError()).isEqualTo(EvaluacionesCodes.Asesor.ID_REQUERIDO);
                    });
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo()).isEqualTo(EvaluacionesFields.Asesor.IDENTIFICADOR);
                        assertThat(error.codigoError())
                                .isEqualTo(EvaluacionesCodes.Asesor.IDENTIFICADOR_REQUERIDO);
                    });
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo()).isEqualTo(EvaluacionesFields.Asesor.NOMBRE);
                        assertThat(error.codigoError()).isEqualTo(EvaluacionesCodes.Asesor.NOMBRE_REQUERIDO);
                    });
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo()).isEqualTo(EvaluacionesFields.Asesor.EMAIL);
                        assertThat(error.codigoError()).isEqualTo(EvaluacionesCodes.Asesor.EMAIL_REQUERIDO);
                    });
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo()).isEqualTo(EvaluacionesFields.Asesor.OCURRIDO_EN);
                        assertThat(error.codigoError())
                                .isEqualTo(EvaluacionesCodes.Asesor.OCURRIDO_EN_REQUERIDO);
                    });
                });
    }

    @Test
    void debeReconstruirSinValidarYReflejarLaBaja_cuandoEliminadoEnEsNuloONo() {
        // Arrange
        var eliminadoEn = Instant.parse("2026-09-25T10:00:00Z");

        // Act
        var sinBaja = AsesorDomain.reconstruir(null, null, null, null, null, null);
        var conBaja = AsesorDomain.reconstruir(
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
        var asesor = AsesorDomain.reconstruir(
                id, "20161020123", "Ana Perez", "ana@uco.edu.co", OCURRIDO_EN, OCURRIDO_EN);
        var nuevoOcurridoEn = Instant.parse("2026-09-24T12:00:00Z");

        // Act
        asesor.reactivar("20161020999", "Ana Reactivada", "reactivada@uco.edu.co", nuevoOcurridoEn);

        // Assert
        assertThat(asesor.getId()).isEqualTo(id);
        assertThat(asesor.getIdentificador()).isEqualTo("20161020999");
        assertThat(asesor.getNombre()).isEqualTo("Ana Reactivada");
        assertThat(asesor.getEmail()).isEqualTo("reactivada@uco.edu.co");
        assertThat(asesor.getOcurridoEn()).isEqualTo(nuevoOcurridoEn);
        assertThat(asesor.getEliminadoEn()).isEqualTo(UtilFecha.VACIO);
        assertThat(asesor.estaEliminado()).isFalse();
    }

    @Test
    void debeAcumularErroresYConservarLaBaja_cuandoReactivarRecibeDatosInvalidos() {
        // Arrange
        var asesor = AsesorDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), "20161020123", "Ana Perez", "ana@uco.edu.co", OCURRIDO_EN, OCURRIDO_EN);

        // Act & Assert
        assertThatThrownBy(() -> asesor.reactivar(" ", " ", " ", null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .extracting(e -> e.campo())
                        .containsExactlyInAnyOrder(EvaluacionesFields.Asesor.IDENTIFICADOR,
                                EvaluacionesFields.Asesor.NOMBRE, EvaluacionesFields.Asesor.EMAIL,
                                EvaluacionesFields.Asesor.OCURRIDO_EN));
        assertThat(asesor.estaEliminado()).isTrue();
    }

    @Test
    void debeAsignarEliminadoEnYOcurridoEn_cuandoSeRemueve() {
        // Arrange
        var asesor = AsesorDomain.crear(
                UtilUUID.generarNuevoUUID(), "20161020123", "Ana Perez", "ana@uco.edu.co", OCURRIDO_EN);
        var nuevoOcurridoEn = Instant.parse("2026-09-24T12:00:00Z");

        // Act
        asesor.remover(nuevoOcurridoEn);

        // Assert
        assertThat(asesor.getEliminadoEn()).isEqualTo(nuevoOcurridoEn);
        assertThat(asesor.getOcurridoEn()).isEqualTo(nuevoOcurridoEn);
        assertThat(asesor.estaEliminado()).isTrue();
    }

    @Test
    void debeLanzarValidacionYConservarVigente_cuandoRemoverRecibeOcurridoEnNulo() {
        // Arrange
        var asesor = AsesorDomain.crear(
                UtilUUID.generarNuevoUUID(), "20161020123", "Ana Perez", "ana@uco.edu.co", OCURRIDO_EN);

        // Act & Assert
        assertThatThrownBy(() -> asesor.remover(null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .extracting(e -> e.campo())
                        .containsExactly(EvaluacionesFields.Asesor.OCURRIDO_EN));
        assertThat(asesor.estaEliminado()).isFalse();
        assertThat(asesor.getOcurridoEn()).isEqualTo(OCURRIDO_EN);
    }

    @Test
    void debeExponerCentinelaVacioNoEliminado_cuandoSeConsultaVacio() {
        // Act
        var vacio = AsesorDomain.VACIO;

        // Assert
        assertThat(vacio.esVacio()).isTrue();
        assertThat(vacio.estaEliminado()).isFalse();
    }
}
