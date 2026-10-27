package com.arquisoft.artefactos.domain.asesor;

import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AsesorDomainTest {

    @Test
    void debeCrearAsesor_cuandoDatosValidos() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var ocurridoEn = Instant.now();

        // Act
        var asesor = AsesorDomain.crear(id, "20161020123", "Ana Perez", "ana@uco.edu.co", ocurridoEn);

        // Assert
        assertThat(asesor.getId()).isEqualTo(id);
        assertThat(asesor.getIdentificador()).isEqualTo("20161020123");
        assertThat(asesor.getNombre()).isEqualTo("Ana Perez");
        assertThat(asesor.getEmail()).isEqualTo("ana@uco.edu.co");
        assertThat(asesor.getOcurridoEn()).isEqualTo(ocurridoEn);
        assertThat(asesor.esVacio()).isFalse();
    }

    @Test
    void debeAcumularTodosLosErrores_cuandoVariosCamposSonInvalidos() {
        // Act & Assert
        assertThatThrownBy(() -> AsesorDomain.crear(null, " ", " ", " ", null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> {
                    var validationEx = (DomainValidationException) ex;
                    var errores = validationEx.getValidationResult().getErrores();
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo()).isEqualTo(ArtefactosFields.Asesor.ID);
                        assertThat(error.codigoError()).isEqualTo(ArtefactosCodes.Asesor.ID_REQUERIDO);
                    });
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo()).isEqualTo(ArtefactosFields.Asesor.IDENTIFICADOR);
                        assertThat(error.codigoError())
                                .isEqualTo(ArtefactosCodes.Asesor.IDENTIFICADOR_REQUERIDO);
                    });
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo()).isEqualTo(ArtefactosFields.Asesor.NOMBRE);
                        assertThat(error.codigoError()).isEqualTo(ArtefactosCodes.Asesor.NOMBRE_REQUERIDO);
                    });
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo()).isEqualTo(ArtefactosFields.Asesor.EMAIL);
                        assertThat(error.codigoError()).isEqualTo(ArtefactosCodes.Asesor.EMAIL_REQUERIDO);
                    });
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo()).isEqualTo(ArtefactosFields.Asesor.OCURRIDO_EN);
                        assertThat(error.codigoError())
                                .isEqualTo(ArtefactosCodes.Asesor.OCURRIDO_EN_REQUERIDO);
                    });
                });
    }

    @Test
    void debeReconstruir_sinValidar() {
        // Act
        var asesor = AsesorDomain.reconstruir(null, null, null, null, null, null);

        // Assert
        assertThat(asesor.getId()).isNull();
        assertThat(asesor.esVacio()).isFalse();
    }

    @Test
    void debeExponerElCentinelaVacio() {
        // Assert
        assertThat(AsesorDomain.VACIO.esVacio()).isTrue();
    }

    @Test
    void debeActualizarDatosYOcurridoEn_cuandoActualizarEsInvocado() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var asesor = AsesorDomain.crear(id, "20161020123", "Ana Perez", "ana@uco.edu.co", Instant.now());
        var nuevoOcurridoEn = Instant.now().plusSeconds(60);

        // Act
        asesor.actualizar("20161020999", "Ana Actualizada", "actualizada@uco.edu.co", nuevoOcurridoEn);

        // Assert
        assertThat(asesor.getIdentificador()).isEqualTo("20161020999");
        assertThat(asesor.getNombre()).isEqualTo("Ana Actualizada");
        assertThat(asesor.getEmail()).isEqualTo("actualizada@uco.edu.co");
        assertThat(asesor.getOcurridoEn()).isEqualTo(nuevoOcurridoEn);
        assertThat(asesor.getId()).isEqualTo(id);
    }

    @Test
    void debeAcumularErrores_cuandoActualizarRecibeDatosInvalidos() {
        // Arrange
        var asesor = AsesorDomain.crear(
                UtilUUID.generarNuevoUUID(), "20161020123", "Ana Perez", "ana@uco.edu.co", Instant.now());

        // Act & Assert
        assertThatThrownBy(() -> asesor.actualizar(" ", "Ana Perez", " ", Instant.now()))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .extracting(e -> e.campo())
                        .contains(ArtefactosFields.Asesor.IDENTIFICADOR, ArtefactosFields.Asesor.EMAIL));
    }

    @Test
    void debeNacerVigenteYReconstruirSinBaja_cuandoEliminadoEnEsNulo() {
        // Act
        var creado = AsesorDomain.crear(
                UtilUUID.generarNuevoUUID(), "20161020123", "Ana Perez", "ana@uco.edu.co", Instant.now());
        var reconstruido = AsesorDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), "20161020123", "Ana Perez", "ana@uco.edu.co", Instant.now(), null);

        // Assert
        assertThat(creado.getEliminadoEn()).isEqualTo(UtilFecha.VACIO);
        assertThat(creado.estaEliminado()).isFalse();
        assertThat(reconstruido.getEliminadoEn()).isEqualTo(UtilFecha.VACIO);
        assertThat(reconstruido.estaEliminado()).isFalse();
    }

    @Test
    void debeFijarEliminadoEnYOcurridoEn_cuandoSeRemueve() {
        // Arrange
        var asesor = AsesorDomain.crear(
                UtilUUID.generarNuevoUUID(), "20161020123", "Ana Perez", "ana@uco.edu.co",
                Instant.parse("2026-09-24T10:00:00Z"));
        var instante = Instant.parse("2026-09-24T11:00:00Z");

        // Act
        asesor.remover(instante);

        // Assert
        assertThat(asesor.getEliminadoEn()).isEqualTo(instante);
        assertThat(asesor.getOcurridoEn()).isEqualTo(instante);
        assertThat(asesor.estaEliminado()).isTrue();
    }

    @Test
    void debeActualizarDatosYLimpiarLaBaja_cuandoSeReactiva() {
        // Arrange
        var eliminadoEn = Instant.parse("2026-09-24T10:00:00Z");
        var asesor = AsesorDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), "20161020123", "Ana Perez", "ana@uco.edu.co", eliminadoEn, eliminadoEn);
        var nuevoOcurridoEn = Instant.parse("2026-09-24T12:00:00Z");

        // Act
        asesor.reactivar("20161020999", "Ana Reactivada", "reactivada@uco.edu.co", nuevoOcurridoEn);

        // Assert
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
        var eliminadoEn = Instant.parse("2026-09-24T10:00:00Z");
        var asesor = AsesorDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), "20161020123", "Ana Perez", "ana@uco.edu.co", eliminadoEn, eliminadoEn);

        // Act & Assert
        assertThatThrownBy(() -> asesor.reactivar(" ", " ", "ana@uco.edu.co", null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .extracting(e -> e.campo())
                        .contains(ArtefactosFields.Asesor.IDENTIFICADOR, ArtefactosFields.Asesor.NOMBRE,
                                ArtefactosFields.Asesor.OCURRIDO_EN));
        assertThat(asesor.estaEliminado()).isTrue();
    }
}
