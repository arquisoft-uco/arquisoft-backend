package com.arquisoft.artefactos.domain.estudiante;

import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EstudianteDomainTest {

    private static final Instant ANTES = Instant.parse("2026-10-01T10:00:00Z");
    private static final Instant DESPUES = Instant.parse("2026-10-02T10:00:00Z");

    @Test
    void debeEstarEliminado_soloCuandoTieneFechaDeBaja() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();

        // Act
        var vigente = EstudianteDomain.reconstruir(id, "1", "Laura", "l@uco.edu.co", Instant.EPOCH, null);
        var eliminado = EstudianteDomain.reconstruir(id, "1", "Laura", "l@uco.edu.co", Instant.EPOCH, Instant.now());

        // Assert
        assertThat(vigente.estaEliminado()).isFalse();
        assertThat(eliminado.estaEliminado()).isTrue();
        assertThat(vigente.getNombre()).isEqualTo("Laura");
    }

    @Test
    void debeCrearVigente_cuandoDatosValidos() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();

        // Act
        var estudiante = EstudianteDomain.crear(id, "2016", "Laura Gomez", "laura@uco.edu.co", ANTES);

        // Assert
        assertThat(estudiante.getId()).isEqualTo(id);
        assertThat(estudiante.getIdentificador()).isEqualTo("2016");
        assertThat(estudiante.getNombre()).isEqualTo("Laura Gomez");
        assertThat(estudiante.getEmail()).isEqualTo("laura@uco.edu.co");
        assertThat(estudiante.getOcurridoEn()).isEqualTo(ANTES);
        assertThat(estudiante.getEliminadoEn()).isEqualTo(UtilFecha.VACIO);
        assertThat(estudiante.esVacio()).isFalse();
    }

    @Test
    void debeAcumularTodosLosErrores_cuandoVariosCamposSonInvalidos() {
        // Act & Assert
        assertThatThrownBy(() -> EstudianteDomain.crear(null, " ", " ", " ", null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .extracting(e -> e.campo())
                        .containsExactlyInAnyOrder(
                                ArtefactosFields.Estudiante.ID,
                                ArtefactosFields.Estudiante.IDENTIFICADOR,
                                ArtefactosFields.Estudiante.NOMBRE,
                                ArtefactosFields.Estudiante.EMAIL,
                                ArtefactosFields.Estudiante.OCURRIDO_EN));
    }

    @Test
    void debeFijarEliminadoEnYOcurridoEn_cuandoSeRemueve() {
        // Arrange
        var estudiante = EstudianteDomain.crear(UtilUUID.generarNuevoUUID(), "2016", "Laura", "l@uco.edu.co", ANTES);

        // Act
        estudiante.remover(DESPUES);

        // Assert
        assertThat(estudiante.getEliminadoEn()).isEqualTo(DESPUES);
        assertThat(estudiante.getOcurridoEn()).isEqualTo(DESPUES);
        assertThat(estudiante.estaEliminado()).isTrue();
    }

    @Test
    void debeActualizarDatosYLimpiarLaBaja_cuandoSeReactiva() {
        // Arrange
        var estudiante = EstudianteDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), "2016", "Laura", "l@uco.edu.co", ANTES, ANTES);

        // Act
        estudiante.reactivar("2099", "Laura Nueva", "nueva@uco.edu.co", DESPUES);

        // Assert
        assertThat(estudiante.getIdentificador()).isEqualTo("2099");
        assertThat(estudiante.getNombre()).isEqualTo("Laura Nueva");
        assertThat(estudiante.getEmail()).isEqualTo("nueva@uco.edu.co");
        assertThat(estudiante.getOcurridoEn()).isEqualTo(DESPUES);
        assertThat(estudiante.estaEliminado()).isFalse();
    }

    @Test
    void debeConservarLaBaja_cuandoReactivarRecibeDatosInvalidos() {
        // Arrange
        var estudiante = EstudianteDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), "2016", "Laura", "l@uco.edu.co", ANTES, ANTES);

        // Act & Assert
        assertThatThrownBy(() -> estudiante.reactivar(" ", " ", "l@uco.edu.co", null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .extracting(e -> e.campo())
                        .contains(ArtefactosFields.Estudiante.IDENTIFICADOR, ArtefactosFields.Estudiante.NOMBRE,
                                ArtefactosFields.Estudiante.OCURRIDO_EN));
        assertThat(estudiante.estaEliminado()).isTrue();
    }

    @Test
    void debeActualizarDatosYOcurridoEn_cuandoActualizarEsInvocado() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var estudiante = EstudianteDomain.crear(id, "2016", "Laura", "l@uco.edu.co", ANTES);

        // Act
        estudiante.actualizar("2099", "Laura Nueva", "nueva@uco.edu.co", DESPUES);

        // Assert
        assertThat(estudiante.getId()).isEqualTo(id);
        assertThat(estudiante.getIdentificador()).isEqualTo("2099");
        assertThat(estudiante.getNombre()).isEqualTo("Laura Nueva");
        assertThat(estudiante.getEmail()).isEqualTo("nueva@uco.edu.co");
        assertThat(estudiante.getOcurridoEn()).isEqualTo(DESPUES);
    }

    @Test
    void debeAcumularErrores_cuandoActualizarRecibeDatosInvalidos() {
        // Arrange
        var estudiante = EstudianteDomain.crear(UtilUUID.generarNuevoUUID(), "2016", "Laura", "l@uco.edu.co", ANTES);

        // Act & Assert
        assertThatThrownBy(() -> estudiante.actualizar(" ", "Laura", " ", DESPUES))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .extracting(e -> e.campo())
                        .contains(ArtefactosFields.Estudiante.IDENTIFICADOR, ArtefactosFields.Estudiante.EMAIL));
    }

    @Test
    void debeExponerElCentinelaVacio() {
        // Assert
        assertThat(EstudianteDomain.VACIO.esVacio()).isTrue();
    }
}
