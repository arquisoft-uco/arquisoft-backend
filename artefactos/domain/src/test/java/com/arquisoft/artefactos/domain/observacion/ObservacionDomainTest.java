package com.arquisoft.artefactos.domain.observacion;

import com.arquisoft.artefactos.domain.observacion.model.EstadoObservacion;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosLimits;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ObservacionDomainTest {

    @Test
    void debeCrear_cuandoDatosValidos() {
        // Arrange
        var revision = UtilUUID.generarNuevoUUID();

        // Act
        var observacion = ObservacionDomain.crear(revision, "  Falta la bibliografia  ");

        // Assert
        assertThat(observacion.getId()).isNotNull();
        assertThat(observacion.getRevisionAsesor()).isEqualTo(revision);
        assertThat(observacion.getDescripcion()).isEqualTo("Falta la bibliografia");
        assertThat(observacion.getEstado()).isEqualTo(EstadoObservacion.NUEVA);
    }

    @Test
    void debeReconstruir_conTodosLosCampos() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var revision = UtilUUID.generarNuevoUUID();

        // Act
        var observacion = ObservacionDomain.reconstruir(
                id, revision, "Texto", EstadoObservacion.SOLUCIONADA, UtilUUID.obtenerUUIDPorDefecto());

        // Assert
        assertThat(observacion.getId()).isEqualTo(id);
        assertThat(observacion.getRevisionAsesor()).isEqualTo(revision);
        assertThat(observacion.getDescripcion()).isEqualTo("Texto");
        assertThat(observacion.getEstado()).isEqualTo(EstadoObservacion.SOLUCIONADA);
        assertThat(observacion.esVacio()).isFalse();
    }

    @Test
    void debeCrearContinuacion_copiandoDescripcionYOrigen() {
        // Arrange
        var origen = ObservacionDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), "Falta el marco teorico",
                EstadoObservacion.SOLUCIONADA, UtilUUID.obtenerUUIDPorDefecto());
        var revisionSiguiente = UtilUUID.generarNuevoUUID();

        // Act
        var continuacion = ObservacionDomain.crearContinuacion(origen, revisionSiguiente);

        // Assert
        assertThat(continuacion.getId()).isNotNull().isNotEqualTo(origen.getId());
        assertThat(continuacion.getRevisionAsesor()).isEqualTo(revisionSiguiente);
        assertThat(continuacion.getDescripcion()).isEqualTo("Falta el marco teorico");
        assertThat(continuacion.getEstado()).isEqualTo(EstadoObservacion.NUEVA);
        assertThat(continuacion.getObservacionOrigen()).isEqualTo(origen.getId());
        assertThat(continuacion.esContinuacion()).isTrue();
    }

    @Test
    void debeRechazarContinuacion_cuandoLaRevisionEsNula() {
        // Arrange
        var origen = ObservacionDomain.crear(UtilUUID.generarNuevoUUID(), "Texto");

        // Act & Assert
        assertThatThrownBy(() -> ObservacionDomain.crearContinuacion(origen, null))
                .isInstanceOf(DomainValidationException.class);
    }

    @Test
    void debeDejarOrigenPorDefecto_cuandoSeCreaYNoSerContinuacion() {
        // Act
        var observacion = ObservacionDomain.crear(UtilUUID.generarNuevoUUID(), "Texto");

        // Assert
        assertThat(UtilUUID.esPorDefecto(observacion.getObservacionOrigen())).isTrue();
        assertThat(observacion.esContinuacion()).isFalse();
    }

    @Test
    void debeReconstruirConOrigen_yResponderEsContinuacion() {
        // Arrange
        var origen = UtilUUID.generarNuevoUUID();

        // Act
        var observacion = ObservacionDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), "Texto", EstadoObservacion.NUEVA, origen);

        // Assert
        assertThat(observacion.getObservacionOrigen()).isEqualTo(origen);
        assertThat(observacion.esContinuacion()).isTrue();
    }

    @Test
    void debeSerVacio_cuandoEsElCentinela() {
        // Act & Assert
        assertThat(ObservacionDomain.VACIO.esVacio()).isTrue();
        assertThat(UtilUUID.esPorDefecto(ObservacionDomain.VACIO.getId())).isTrue();
        assertThat(ObservacionDomain.VACIO.getEstado()).isEqualTo(EstadoObservacion.VACIO);
    }

    @Test
    void debeAcumularFieldErrors_cuandoRevisionNulaYDescripcionEnBlanco() {
        // Act & Assert
        assertThatThrownBy(() -> ObservacionDomain.crear(null, "   "))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> {
                    var errores = ((DomainValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores).extracting("codigoError")
                            .contains(ArtefactosCodes.Observacion.REVISION_ASESOR_ID_REQUERIDO,
                                    ArtefactosCodes.Observacion.DESCRIPCION_REQUERIDA);
                });
    }

    @Test
    void debeRechazar_cuandoDescripcionSuperaMaximoTrasRecortar() {
        // Arrange
        var revision = UtilUUID.generarNuevoUUID();
        var excedida = "a".repeat(ArtefactosLimits.Observacion.DESCRIPCION_MAX + 1);
        var limite = " " + "a".repeat(ArtefactosLimits.Observacion.DESCRIPCION_MAX) + " ";

        // Act & Assert
        assertThatThrownBy(() -> ObservacionDomain.crear(revision, excedida))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> {
                    var errores = ((DomainValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores).extracting("codigoError")
                            .contains(ArtefactosCodes.Observacion.DESCRIPCION_LONGITUD_EXCEDIDA);
                });
        assertThat(ObservacionDomain.crear(revision, limite).getDescripcion()).hasSize(1000);
    }
}
