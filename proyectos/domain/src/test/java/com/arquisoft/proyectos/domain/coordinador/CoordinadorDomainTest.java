package com.arquisoft.proyectos.domain.coordinador;

import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.message.constant.ProyectosFields;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CoordinadorDomainTest {

    @Test
    void debeCrearCoordinador_cuandoDatosValidos() {
        // Arrange
        var id = UUID.randomUUID();
        var ocurridoEn = Instant.now();

        // Act
        var coordinador = CoordinadorDomain.crear(id, "20161020123", "Ana Perez", "ana@uco.edu.co", ocurridoEn);

        // Assert
        assertThat(coordinador.getId()).isEqualTo(id);
        assertThat(coordinador.getIdentificador()).isEqualTo("20161020123");
        assertThat(coordinador.getNombre()).isEqualTo("Ana Perez");
        assertThat(coordinador.getEmail()).isEqualTo("ana@uco.edu.co");
        assertThat(coordinador.getOcurridoEn()).isEqualTo(ocurridoEn);
        assertThat(coordinador.esVacio()).isFalse();
    }

    @Test
    void debeAcumularTodosLosErrores_cuandoVariosCamposSonInvalidos() {
        // Act & Assert
        assertThatThrownBy(() -> CoordinadorDomain.crear(null, " ", " ", " ", null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> {
                    var validationEx = (DomainValidationException) ex;
                    var errores = validationEx.getValidationResult().getErrores();
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo()).isEqualTo(ProyectosFields.Coordinador.ID);
                        assertThat(error.codigoError()).isEqualTo(ProyectosCodes.Coordinador.ID_REQUERIDO);
                    });
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo()).isEqualTo(ProyectosFields.Coordinador.IDENTIFICADOR);
                        assertThat(error.codigoError())
                                .isEqualTo(ProyectosCodes.Coordinador.IDENTIFICADOR_REQUERIDO);
                    });
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo()).isEqualTo(ProyectosFields.Coordinador.NOMBRE);
                        assertThat(error.codigoError()).isEqualTo(ProyectosCodes.Coordinador.NOMBRE_REQUERIDO);
                    });
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo()).isEqualTo(ProyectosFields.Coordinador.EMAIL);
                        assertThat(error.codigoError()).isEqualTo(ProyectosCodes.Coordinador.EMAIL_REQUERIDO);
                    });
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo()).isEqualTo(ProyectosFields.Coordinador.OCURRIDO_EN);
                        assertThat(error.codigoError())
                                .isEqualTo(ProyectosCodes.Coordinador.OCURRIDO_EN_REQUERIDO);
                    });
                });
    }

    @Test
    void debeReconstruir_sinValidar() {
        // Act
        var coordinador = CoordinadorDomain.reconstruir(null, null, null, null, null, null);

        // Assert
        assertThat(coordinador.getId()).isNull();
        assertThat(coordinador.esVacio()).isFalse();
    }

    @Test
    void debeExponerElCentinelaVacio() {
        // Assert
        assertThat(CoordinadorDomain.VACIO.esVacio()).isTrue();
    }

    @Test
    void debeActualizarDatosYOcurridoEn_cuandoActualizarEsInvocado() {
        // Arrange
        var id = UUID.randomUUID();
        var coordinador = CoordinadorDomain.crear(id, "20161020123", "Ana Perez", "ana@uco.edu.co", Instant.now());
        var nuevoOcurridoEn = Instant.now().plusSeconds(60);

        // Act
        coordinador.actualizar("20161020999", "Ana Actualizada", "actualizada@uco.edu.co", nuevoOcurridoEn);

        // Assert
        assertThat(coordinador.getIdentificador()).isEqualTo("20161020999");
        assertThat(coordinador.getNombre()).isEqualTo("Ana Actualizada");
        assertThat(coordinador.getEmail()).isEqualTo("actualizada@uco.edu.co");
        assertThat(coordinador.getOcurridoEn()).isEqualTo(nuevoOcurridoEn);
        assertThat(coordinador.getId()).isEqualTo(id);
    }

    @Test
    void debeAcumularErrores_cuandoActualizarRecibeDatosInvalidos() {
        // Arrange
        var coordinador = CoordinadorDomain.crear(
                UUID.randomUUID(), "20161020123", "Ana Perez", "ana@uco.edu.co", Instant.now());

        // Act & Assert
        assertThatThrownBy(() -> coordinador.actualizar(" ", "Ana Perez", " ", Instant.now()))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .extracting(e -> e.campo())
                        .contains(ProyectosFields.Coordinador.IDENTIFICADOR, ProyectosFields.Coordinador.EMAIL));
    }

    @Test
    void debeNacerVigenteYReconstruirSinBaja_cuandoEliminadoEnEsNulo() {
        // Act
        var creado = CoordinadorDomain.crear(
                UUID.randomUUID(), "20161020123", "Ana Perez", "ana@uco.edu.co", Instant.now());
        var reconstruido = CoordinadorDomain.reconstruir(
                UUID.randomUUID(), "20161020123", "Ana Perez", "ana@uco.edu.co", Instant.now(), null);

        // Assert
        assertThat(creado.getEliminadoEn()).isEqualTo(UtilFecha.VACIO);
        assertThat(creado.estaEliminado()).isFalse();
        assertThat(reconstruido.getEliminadoEn()).isEqualTo(UtilFecha.VACIO);
        assertThat(reconstruido.estaEliminado()).isFalse();
    }

    @Test
    void debeFijarEliminadoEnYOcurridoEn_cuandoSeRemueve() {
        // Arrange
        var coordinador = CoordinadorDomain.crear(
                UUID.randomUUID(), "20161020123", "Ana Perez", "ana@uco.edu.co",
                Instant.parse("2026-09-24T10:00:00Z"));
        var instante = Instant.parse("2026-09-24T11:00:00Z");

        // Act
        coordinador.remover(instante);

        // Assert
        assertThat(coordinador.getEliminadoEn()).isEqualTo(instante);
        assertThat(coordinador.getOcurridoEn()).isEqualTo(instante);
        assertThat(coordinador.estaEliminado()).isTrue();
    }

    @Test
    void debeActualizarDatosYLimpiarLaBaja_cuandoSeReactiva() {
        // Arrange
        var eliminadoEn = Instant.parse("2026-09-24T10:00:00Z");
        var coordinador = CoordinadorDomain.reconstruir(
                UUID.randomUUID(), "20161020123", "Ana Perez", "ana@uco.edu.co", eliminadoEn, eliminadoEn);
        var nuevoOcurridoEn = Instant.parse("2026-09-24T12:00:00Z");

        // Act
        coordinador.reactivar("20161020999", "Ana Reactivada", "reactivada@uco.edu.co", nuevoOcurridoEn);

        // Assert
        assertThat(coordinador.getIdentificador()).isEqualTo("20161020999");
        assertThat(coordinador.getNombre()).isEqualTo("Ana Reactivada");
        assertThat(coordinador.getEmail()).isEqualTo("reactivada@uco.edu.co");
        assertThat(coordinador.getOcurridoEn()).isEqualTo(nuevoOcurridoEn);
        assertThat(coordinador.getEliminadoEn()).isEqualTo(UtilFecha.VACIO);
        assertThat(coordinador.estaEliminado()).isFalse();
    }

    @Test
    void debeAcumularErroresYConservarLaBaja_cuandoReactivarRecibeDatosInvalidos() {
        // Arrange
        var eliminadoEn = Instant.parse("2026-09-24T10:00:00Z");
        var coordinador = CoordinadorDomain.reconstruir(
                UUID.randomUUID(), "20161020123", "Ana Perez", "ana@uco.edu.co", eliminadoEn, eliminadoEn);

        // Act & Assert
        assertThatThrownBy(() -> coordinador.reactivar(" ", " ", "ana@uco.edu.co", null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .extracting(e -> e.campo())
                        .contains(ProyectosFields.Coordinador.IDENTIFICADOR, ProyectosFields.Coordinador.NOMBRE,
                                ProyectosFields.Coordinador.OCURRIDO_EN));
        assertThat(coordinador.estaEliminado()).isTrue();
    }
}
