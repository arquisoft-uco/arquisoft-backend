package com.arquisoft.fichas.domain.representantecomite;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RepresentanteComiteDomainTest {

    private static final Instant OCURRIDO_EN = Instant.parse("2026-09-20T10:00:00Z");
    private static final Instant OCURRIDO_EN_NUEVO = Instant.parse("2026-09-24T10:00:00Z");

    @Test
    void debeCrearRepresentanteComiteVigenteRecortado_cuandoLosDatosSonValidos() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();

        // Act
        var representanteComite = RepresentanteComiteDomain.crear(
                id, " 20161020123 ", " Ana Pérez ", " ana.perez@uco.edu.co ", OCURRIDO_EN);

        // Assert
        assertThat(representanteComite.getId()).isEqualTo(id);
        assertThat(representanteComite.getIdentificador()).isEqualTo("20161020123");
        assertThat(representanteComite.getNombre()).isEqualTo("Ana Pérez");
        assertThat(representanteComite.getEmail()).isEqualTo("ana.perez@uco.edu.co");
        assertThat(representanteComite.getOcurridoEn()).isEqualTo(OCURRIDO_EN);
        assertThat(representanteComite.getEliminadoEn()).isEqualTo(UtilFecha.VACIO);
        assertThat(representanteComite.estaEliminado()).isFalse();
        assertThat(representanteComite.esVacio()).isFalse();
    }

    @Test
    void debeAcumularLosCincoErrores_cuandoTodosLosCamposSonInvalidos() {
        // Act & Assert
        assertThatThrownBy(() -> RepresentanteComiteDomain.crear(null, " ", " ", " ", null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> {
                    var errores = ((DomainValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores)
                            .extracting(e -> e.campo())
                            .containsExactlyInAnyOrder(
                                    FichasFields.RepresentanteComite.ID,
                                    FichasFields.RepresentanteComite.IDENTIFICADOR,
                                    FichasFields.RepresentanteComite.NOMBRE,
                                    FichasFields.RepresentanteComite.EMAIL,
                                    FichasFields.RepresentanteComite.OCURRIDO_EN);
                    assertThat(errores)
                            .extracting(e -> e.codigoError())
                            .containsExactlyInAnyOrder(
                                    FichasCodes.RepresentanteComite.ID_REQUERIDO,
                                    FichasCodes.RepresentanteComite.IDENTIFICADOR_REQUERIDO,
                                    FichasCodes.RepresentanteComite.NOMBRE_REQUERIDO,
                                    FichasCodes.RepresentanteComite.EMAIL_REQUERIDO,
                                    FichasCodes.RepresentanteComite.OCURRIDO_EN_REQUERIDO);
                });
    }

    @Test
    void debeReconstruirSinValidar_cuandoReconstruirRecibeNulos() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();

        // Act
        var sinBaja = RepresentanteComiteDomain.reconstruir(id, null, null, null, null, null);
        var eliminado = RepresentanteComiteDomain.reconstruir(
                id, "20161020123", "Ana Pérez", "ana.perez@uco.edu.co", OCURRIDO_EN, OCURRIDO_EN);

        // Assert
        assertThat(sinBaja.getId()).isEqualTo(id);
        assertThat(sinBaja.getIdentificador()).isNull();
        assertThat(sinBaja.getOcurridoEn()).isNull();
        assertThat(sinBaja.getEliminadoEn()).isEqualTo(UtilFecha.VACIO);
        assertThat(sinBaja.estaEliminado()).isFalse();
        assertThat(eliminado.getEliminadoEn()).isEqualTo(OCURRIDO_EN);
        assertThat(eliminado.estaEliminado()).isTrue();
    }

    @Test
    void debeReactivarConLosDatosNuevos_cuandoEstabaEliminado() {
        // Arrange
        var representanteComite = RepresentanteComiteDomain.reconstruir(UtilUUID.generarNuevoUUID(),
                "20161020123", "Ana Pérez", "ana.perez@uco.edu.co", OCURRIDO_EN, OCURRIDO_EN);

        // Act
        representanteComite.reactivar(" 20161020999 ", "Ana Reactivada", "reactivada@uco.edu.co",
                OCURRIDO_EN_NUEVO);

        // Assert
        assertThat(representanteComite.estaEliminado()).isFalse();
        assertThat(representanteComite.getEliminadoEn()).isEqualTo(UtilFecha.VACIO);
        assertThat(representanteComite.getIdentificador()).isEqualTo("20161020999");
        assertThat(representanteComite.getNombre()).isEqualTo("Ana Reactivada");
        assertThat(representanteComite.getEmail()).isEqualTo("reactivada@uco.edu.co");
        assertThat(representanteComite.getOcurridoEn()).isEqualTo(OCURRIDO_EN_NUEVO);
    }

    @Test
    void debeAcumularErroresYSeguirEliminado_cuandoReactivarRecibeDatosInvalidos() {
        // Arrange
        var representanteComite = RepresentanteComiteDomain.reconstruir(UtilUUID.generarNuevoUUID(),
                "20161020123", "Ana Pérez", "ana.perez@uco.edu.co", OCURRIDO_EN, OCURRIDO_EN);

        // Act & Assert
        assertThatThrownBy(() -> representanteComite.reactivar(" ", " ", "ana.perez@uco.edu.co", null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> {
                    var errores = ((DomainValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores)
                            .extracting(e -> e.campo())
                            .containsExactlyInAnyOrder(
                                    FichasFields.RepresentanteComite.IDENTIFICADOR,
                                    FichasFields.RepresentanteComite.NOMBRE,
                                    FichasFields.RepresentanteComite.OCURRIDO_EN);
                });
        assertThat(representanteComite.getEliminadoEn()).isEqualTo(OCURRIDO_EN);
        assertThat(representanteComite.estaEliminado()).isTrue();
    }

    @Test
    void debeActualizarDatosYConservarBaja_cuandoActualizarRecibeDatosValidos() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var representanteComite = RepresentanteComiteDomain.reconstruir(
                id, "20161020123", "Ana Pérez", "ana.perez@uco.edu.co", OCURRIDO_EN, OCURRIDO_EN);

        // Act
        representanteComite.actualizar("20161020999", "Ana Actualizada", " actualizada@uco.edu.co ",
                OCURRIDO_EN_NUEVO);

        // Assert
        assertThat(representanteComite.getId()).isEqualTo(id);
        assertThat(representanteComite.getIdentificador()).isEqualTo("20161020999");
        assertThat(representanteComite.getNombre()).isEqualTo("Ana Actualizada");
        assertThat(representanteComite.getEmail()).isEqualTo("actualizada@uco.edu.co");
        assertThat(representanteComite.getOcurridoEn()).isEqualTo(OCURRIDO_EN_NUEVO);
        assertThat(representanteComite.getEliminadoEn()).isEqualTo(OCURRIDO_EN);
    }

    @Test
    void debeAcumularErrores_cuandoActualizarRecibeDatosInvalidos() {
        // Arrange
        var representanteComite = RepresentanteComiteDomain.reconstruir(UtilUUID.generarNuevoUUID(),
                "20161020123", "Ana Pérez", "ana.perez@uco.edu.co", OCURRIDO_EN, null);

        // Act & Assert
        assertThatThrownBy(() -> representanteComite.actualizar("20161020123", "Ana Pérez", " ", null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> {
                    var errores = ((DomainValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores)
                            .extracting(e -> e.campo())
                            .containsExactlyInAnyOrder(
                                    FichasFields.RepresentanteComite.EMAIL,
                                    FichasFields.RepresentanteComite.OCURRIDO_EN);
                });
    }

    @Test
    void debeExponerCentinelaVacioNoEliminado_cuandoSeConsultaVacio() {
        // Act
        var vacio = RepresentanteComiteDomain.VACIO;

        // Assert
        assertThat(vacio.esVacio()).isTrue();
        assertThat(vacio.estaEliminado()).isFalse();
    }
}
