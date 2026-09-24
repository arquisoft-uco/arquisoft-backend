package com.arquisoft.proyectos.domain.asesor;

import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.message.constant.ProyectosFields;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AsesorDomainTest {

    @Test
    void debeCrearAsesor_cuandoDatosValidos() {
        // Arrange
        var id = UUID.randomUUID();
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
                        assertThat(error.campo()).isEqualTo(ProyectosFields.Asesor.ID);
                        assertThat(error.codigoError()).isEqualTo(ProyectosCodes.Asesor.ID_REQUERIDO);
                    });
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo()).isEqualTo(ProyectosFields.Asesor.IDENTIFICADOR);
                        assertThat(error.codigoError())
                                .isEqualTo(ProyectosCodes.Asesor.IDENTIFICADOR_REQUERIDO);
                    });
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo()).isEqualTo(ProyectosFields.Asesor.NOMBRE);
                        assertThat(error.codigoError()).isEqualTo(ProyectosCodes.Asesor.NOMBRE_REQUERIDO);
                    });
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo()).isEqualTo(ProyectosFields.Asesor.EMAIL);
                        assertThat(error.codigoError()).isEqualTo(ProyectosCodes.Asesor.EMAIL_REQUERIDO);
                    });
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo()).isEqualTo(ProyectosFields.Asesor.OCURRIDO_EN);
                        assertThat(error.codigoError())
                                .isEqualTo(ProyectosCodes.Asesor.OCURRIDO_EN_REQUERIDO);
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
        var id = UUID.randomUUID();
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
                UUID.randomUUID(), "20161020123", "Ana Perez", "ana@uco.edu.co", Instant.now());

        // Act & Assert
        assertThatThrownBy(() -> asesor.actualizar(" ", "Ana Perez", " ", Instant.now()))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .extracting(e -> e.campo())
                        .contains(ProyectosFields.Asesor.IDENTIFICADOR, ProyectosFields.Asesor.EMAIL));
    }

    @Test
    void debeNacerVigente_cuandoSeCreaOReconstruyeSinEliminadoEn() {
        // Act
        var creado = AsesorDomain.crear(UUID.randomUUID(), "20161020123", "Ana Perez", "ana@uco.edu.co",
                Instant.parse("2026-09-01T10:00:00Z"));
        var reconstruido = AsesorDomain.reconstruir(UUID.randomUUID(), "20161020123", "Ana Perez",
                "ana@uco.edu.co", Instant.parse("2026-09-01T10:00:00Z"), null);

        // Assert
        assertThat(creado.getEliminadoEn()).isEqualTo(UtilFecha.VACIO);
        assertThat(creado.estaEliminado()).isFalse();
        assertThat(reconstruido.getEliminadoEn()).isEqualTo(UtilFecha.VACIO);
        assertThat(reconstruido.estaEliminado()).isFalse();
    }

    @Test
    void debeFijarEliminadoEnYOcurridoEn_cuandoSeRemueve() {
        // Arrange
        var asesor = AsesorDomain.crear(UUID.randomUUID(), "20161020123", "Ana Perez",
                "ana@uco.edu.co", Instant.parse("2026-09-01T10:00:00Z"));
        var ocurridoEn = Instant.parse("2026-09-23T10:00:00Z");

        // Act
        asesor.remover(ocurridoEn);

        // Assert
        assertThat(asesor.estaEliminado()).isTrue();
        assertThat(asesor.getEliminadoEn()).isEqualTo(ocurridoEn);
        assertThat(asesor.getOcurridoEn()).isEqualTo(ocurridoEn);
    }

    @Test
    void debeLimpiarEliminadoYRefrescarDatos_cuandoSeReactiva() {
        // Arrange
        var eliminadoEn = Instant.parse("2026-09-10T10:00:00Z");
        var asesor = AsesorDomain.reconstruir(UUID.randomUUID(), "20161020123", "Ana Perez",
                "ana@uco.edu.co", eliminadoEn, eliminadoEn);
        var ocurridoEn = Instant.parse("2026-09-23T10:00:00Z");

        // Act
        asesor.reactivar("20161020999", "Ana Gomez", "ana.gomez@uco.edu.co", ocurridoEn);

        // Assert
        assertThat(asesor.estaEliminado()).isFalse();
        assertThat(asesor.getEliminadoEn()).isEqualTo(UtilFecha.VACIO);
        assertThat(asesor.getIdentificador()).isEqualTo("20161020999");
        assertThat(asesor.getNombre()).isEqualTo("Ana Gomez");
        assertThat(asesor.getEmail()).isEqualTo("ana.gomez@uco.edu.co");
        assertThat(asesor.getOcurridoEn()).isEqualTo(ocurridoEn);
    }

    @Test
    void debeAcumularErroresYSeguirEliminado_cuandoSeReactivaConDatosInvalidos() {
        // Arrange
        var eliminadoEn = Instant.parse("2026-09-10T10:00:00Z");
        var asesor = AsesorDomain.reconstruir(UUID.randomUUID(), "20161020123", "Ana Perez",
                "ana@uco.edu.co", eliminadoEn, eliminadoEn);

        // Act & Assert
        assertThatThrownBy(() -> asesor.reactivar(" ", " ", " ", null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .extracting(e -> e.campo())
                        .contains(ProyectosFields.Asesor.IDENTIFICADOR, ProyectosFields.Asesor.NOMBRE,
                                ProyectosFields.Asesor.EMAIL, ProyectosFields.Asesor.OCURRIDO_EN));
        assertThat(asesor.estaEliminado()).isTrue();
    }

    @Test
    void debeConservarEliminadoEn_cuandoSeActualizaUnAsesorEliminado() {
        // Arrange
        var eliminadoEn = Instant.parse("2026-09-10T10:00:00Z");
        var asesor = AsesorDomain.reconstruir(UUID.randomUUID(), "20161020123", "Ana Perez",
                "ana@uco.edu.co", eliminadoEn, eliminadoEn);
        var ocurridoEn = Instant.parse("2026-09-23T10:00:00Z");

        // Act
        asesor.actualizar("20161020999", "Ana Actualizada", "actualizada@uco.edu.co", ocurridoEn);

        // Assert
        assertThat(asesor.estaEliminado()).isTrue();
        assertThat(asesor.getEliminadoEn()).isEqualTo(eliminadoEn);
        assertThat(asesor.getOcurridoEn()).isEqualTo(ocurridoEn);
    }
}
