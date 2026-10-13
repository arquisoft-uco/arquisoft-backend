package com.arquisoft.repositorio_artefactos.domain.administrador;

import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.message.constant.RepositorioArtefactosCodes;
import com.arquisoft.shared.message.constant.RepositorioArtefactosFields;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AdministradorDomainTest {

    @Test
    void debeCrearAdministrador_cuandoDatosValidos() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var ocurridoEn = Instant.now();

        // Act
        var administrador = AdministradorDomain.crear(id, "20161020123", "Ana Perez", "ana@uco.edu.co", ocurridoEn);

        // Assert
        assertThat(administrador.getId()).isEqualTo(id);
        assertThat(administrador.getIdentificador()).isEqualTo("20161020123");
        assertThat(administrador.getNombre()).isEqualTo("Ana Perez");
        assertThat(administrador.getEmail()).isEqualTo("ana@uco.edu.co");
        assertThat(administrador.getOcurridoEn()).isEqualTo(ocurridoEn);
        assertThat(administrador.esVacio()).isFalse();
    }

    @Test
    void debeAcumularTodosLosErrores_cuandoVariosCamposSonInvalidos() {
        // Act & Assert
        assertThatThrownBy(() -> AdministradorDomain.crear(null, " ", " ", " ", null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> {
                    var validationEx = (DomainValidationException) ex;
                    var errores = validationEx.getValidationResult().getErrores();
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo()).isEqualTo(RepositorioArtefactosFields.Administrador.ID);
                        assertThat(error.codigoError()).isEqualTo(RepositorioArtefactosCodes.Administrador.ID_REQUERIDO);
                    });
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo()).isEqualTo(RepositorioArtefactosFields.Administrador.IDENTIFICADOR);
                        assertThat(error.codigoError())
                                .isEqualTo(RepositorioArtefactosCodes.Administrador.IDENTIFICADOR_REQUERIDO);
                    });
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo()).isEqualTo(RepositorioArtefactosFields.Administrador.NOMBRE);
                        assertThat(error.codigoError()).isEqualTo(RepositorioArtefactosCodes.Administrador.NOMBRE_REQUERIDO);
                    });
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo()).isEqualTo(RepositorioArtefactosFields.Administrador.EMAIL);
                        assertThat(error.codigoError()).isEqualTo(RepositorioArtefactosCodes.Administrador.EMAIL_REQUERIDO);
                    });
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo()).isEqualTo(RepositorioArtefactosFields.Administrador.OCURRIDO_EN);
                        assertThat(error.codigoError())
                                .isEqualTo(RepositorioArtefactosCodes.Administrador.OCURRIDO_EN_REQUERIDO);
                    });
                });
    }

    @Test
    void debeReconstruir_sinValidar() {
        // Act
        var administrador = AdministradorDomain.reconstruir(null, null, null, null, null, null);

        // Assert
        assertThat(administrador.getId()).isNull();
        assertThat(administrador.esVacio()).isFalse();
    }

    @Test
    void debeExponerElCentinelaVacio() {
        // Assert
        assertThat(AdministradorDomain.VACIO.esVacio()).isTrue();
    }

    @Test
    void debeActualizarDatosYOcurridoEn_cuandoActualizarEsInvocado() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var administrador = AdministradorDomain.crear(id, "20161020123", "Ana Perez", "ana@uco.edu.co", Instant.now());
        var nuevoOcurridoEn = Instant.now().plusSeconds(60);

        // Act
        administrador.actualizar("20161020999", "Ana Actualizada", "actualizada@uco.edu.co", nuevoOcurridoEn);

        // Assert
        assertThat(administrador.getIdentificador()).isEqualTo("20161020999");
        assertThat(administrador.getNombre()).isEqualTo("Ana Actualizada");
        assertThat(administrador.getEmail()).isEqualTo("actualizada@uco.edu.co");
        assertThat(administrador.getOcurridoEn()).isEqualTo(nuevoOcurridoEn);
        assertThat(administrador.getId()).isEqualTo(id);
    }

    @Test
    void debeAcumularErrores_cuandoActualizarRecibeDatosInvalidos() {
        // Arrange
        var administrador = AdministradorDomain.crear(
                UtilUUID.generarNuevoUUID(), "20161020123", "Ana Perez", "ana@uco.edu.co", Instant.now());

        // Act & Assert
        assertThatThrownBy(() -> administrador.actualizar(" ", "Ana Perez", " ", Instant.now()))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .extracting(e -> e.campo())
                        .contains(RepositorioArtefactosFields.Administrador.IDENTIFICADOR, RepositorioArtefactosFields.Administrador.EMAIL));
    }

    @Test
    void debeNacerVigenteYReconstruirSinBaja_cuandoEliminadoEnEsNulo() {
        // Act
        var creado = AdministradorDomain.crear(
                UtilUUID.generarNuevoUUID(), "20161020123", "Ana Perez", "ana@uco.edu.co", Instant.now());
        var reconstruido = AdministradorDomain.reconstruir(
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
        var administrador = AdministradorDomain.crear(
                UtilUUID.generarNuevoUUID(), "20161020123", "Ana Perez", "ana@uco.edu.co",
                Instant.parse("2026-09-24T10:00:00Z"));
        var instante = Instant.parse("2026-09-24T11:00:00Z");

        // Act
        administrador.remover(instante);

        // Assert
        assertThat(administrador.getEliminadoEn()).isEqualTo(instante);
        assertThat(administrador.getOcurridoEn()).isEqualTo(instante);
        assertThat(administrador.estaEliminado()).isTrue();
    }

    @Test
    void debeActualizarDatosYLimpiarLaBaja_cuandoSeReactiva() {
        // Arrange
        var eliminadoEn = Instant.parse("2026-09-24T10:00:00Z");
        var administrador = AdministradorDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), "20161020123", "Ana Perez", "ana@uco.edu.co", eliminadoEn, eliminadoEn);
        var nuevoOcurridoEn = Instant.parse("2026-09-24T12:00:00Z");

        // Act
        administrador.reactivar("20161020999", "Ana Reactivada", "reactivada@uco.edu.co", nuevoOcurridoEn);

        // Assert
        assertThat(administrador.getIdentificador()).isEqualTo("20161020999");
        assertThat(administrador.getNombre()).isEqualTo("Ana Reactivada");
        assertThat(administrador.getEmail()).isEqualTo("reactivada@uco.edu.co");
        assertThat(administrador.getOcurridoEn()).isEqualTo(nuevoOcurridoEn);
        assertThat(administrador.getEliminadoEn()).isEqualTo(UtilFecha.VACIO);
        assertThat(administrador.estaEliminado()).isFalse();
    }

    @Test
    void debeAcumularErroresYConservarLaBaja_cuandoReactivarRecibeDatosInvalidos() {
        // Arrange
        var eliminadoEn = Instant.parse("2026-09-24T10:00:00Z");
        var administrador = AdministradorDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), "20161020123", "Ana Perez", "ana@uco.edu.co", eliminadoEn, eliminadoEn);

        // Act & Assert
        assertThatThrownBy(() -> administrador.reactivar(" ", " ", "ana@uco.edu.co", null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .extracting(e -> e.campo())
                        .contains(RepositorioArtefactosFields.Administrador.IDENTIFICADOR, RepositorioArtefactosFields.Administrador.NOMBRE,
                                RepositorioArtefactosFields.Administrador.OCURRIDO_EN));
        assertThat(administrador.estaEliminado()).isTrue();
    }
}
