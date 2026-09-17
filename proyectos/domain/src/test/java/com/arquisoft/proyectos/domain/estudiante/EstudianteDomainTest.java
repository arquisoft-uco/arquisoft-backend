package com.arquisoft.proyectos.domain.estudiante;

import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.message.constant.ProyectosFields;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EstudianteDomainTest {

    @Test
    void debeCrearEstudiante_cuandoDatosValidos() {
        // Arrange
        var id = UUID.randomUUID();
        var ocurridoEn = Instant.now();

        // Act
        var estudiante = EstudianteDomain.crear(id, "20161020123", "Ana Perez", "ana@uco.edu.co", ocurridoEn);

        // Assert
        assertThat(estudiante.getId()).isEqualTo(id);
        assertThat(estudiante.getIdentificador()).isEqualTo("20161020123");
        assertThat(estudiante.getNombre()).isEqualTo("Ana Perez");
        assertThat(estudiante.getEmail()).isEqualTo("ana@uco.edu.co");
        assertThat(estudiante.getOcurridoEn()).isEqualTo(ocurridoEn);
        assertThat(estudiante.esVacio()).isFalse();
    }

    @Test
    void debeAcumularTodosLosErrores_cuandoVariosCamposSonInvalidos() {
        // Act & Assert
        assertThatThrownBy(() -> EstudianteDomain.crear(null, " ", " ", " ", null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> {
                    var validationEx = (DomainValidationException) ex;
                    var errores = validationEx.getValidationResult().getErrores();
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo()).isEqualTo(ProyectosFields.Estudiante.ID);
                        assertThat(error.codigoError()).isEqualTo(ProyectosCodes.Estudiante.ID_REQUERIDO);
                    });
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo()).isEqualTo(ProyectosFields.Estudiante.IDENTIFICADOR);
                        assertThat(error.codigoError())
                                .isEqualTo(ProyectosCodes.Estudiante.IDENTIFICADOR_REQUERIDO);
                    });
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo()).isEqualTo(ProyectosFields.Estudiante.NOMBRE);
                        assertThat(error.codigoError()).isEqualTo(ProyectosCodes.Estudiante.NOMBRE_REQUERIDO);
                    });
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo()).isEqualTo(ProyectosFields.Estudiante.EMAIL);
                        assertThat(error.codigoError()).isEqualTo(ProyectosCodes.Estudiante.EMAIL_REQUERIDO);
                    });
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo()).isEqualTo(ProyectosFields.Estudiante.OCURRIDO_EN);
                        assertThat(error.codigoError())
                                .isEqualTo(ProyectosCodes.Estudiante.OCURRIDO_EN_REQUERIDO);
                    });
                });
    }

    @Test
    void debeReconstruir_sinValidar() {
        // Act
        var estudiante = EstudianteDomain.reconstruir(null, null, null, null, null, null);

        // Assert
        assertThat(estudiante.getId()).isNull();
        assertThat(estudiante.esVacio()).isFalse();
    }

    @Test
    void debeExponerElCentinelaVacio() {
        // Assert
        assertThat(EstudianteDomain.VACIO.esVacio()).isTrue();
        assertThat(EstudianteDomain.VACIO.estaEliminado()).isFalse();
        assertThat(EstudianteDomain.reconstruir(null, null, null, null, null, null).getEliminadoEn())
                .isEqualTo(UtilFecha.VACIO);
    }

    @Test
    void debeFijarEliminadoEnYOcurridoEn_cuandoSeRemueve() {
        // Arrange
        var estudiante = EstudianteDomain.crear(UUID.randomUUID(), "20161020123", "Ana Perez",
                "ana@uco.edu.co", Instant.parse("2026-09-01T10:00:00Z"));
        var ocurridoEn = Instant.parse("2026-09-16T10:00:00Z");

        // Act
        estudiante.remover(ocurridoEn);

        // Assert
        assertThat(estudiante.estaEliminado()).isTrue();
        assertThat(estudiante.getEliminadoEn()).isEqualTo(ocurridoEn);
        assertThat(estudiante.getOcurridoEn()).isEqualTo(ocurridoEn);
    }

    @Test
    void debeLimpiarEliminadoYRefrescarDatos_cuandoSeReactiva() {
        // Arrange
        var eliminadoEn = Instant.parse("2026-09-10T10:00:00Z");
        var estudiante = EstudianteDomain.reconstruir(UUID.randomUUID(), "20161020123", "Ana Perez",
                "ana@uco.edu.co", eliminadoEn, eliminadoEn);
        var ocurridoEn = Instant.parse("2026-09-16T10:00:00Z");

        // Act
        estudiante.reactivar("20161020999", "Ana Gomez", "ana.gomez@uco.edu.co", ocurridoEn);

        // Assert
        assertThat(estudiante.estaEliminado()).isFalse();
        assertThat(estudiante.getIdentificador()).isEqualTo("20161020999");
        assertThat(estudiante.getNombre()).isEqualTo("Ana Gomez");
        assertThat(estudiante.getEmail()).isEqualTo("ana.gomez@uco.edu.co");
        assertThat(estudiante.getOcurridoEn()).isEqualTo(ocurridoEn);
    }

    @Test
    void debeAcumularErroresYSeguirEliminado_cuandoSeReactivaConDatosInvalidos() {
        // Arrange
        var eliminadoEn = Instant.parse("2026-09-10T10:00:00Z");
        var estudiante = EstudianteDomain.reconstruir(UUID.randomUUID(), "20161020123", "Ana Perez",
                "ana@uco.edu.co", eliminadoEn, eliminadoEn);

        // Act & Assert
        assertThatThrownBy(() -> estudiante.reactivar(" ", " ", " ", null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .extracting(e -> e.campo())
                        .contains(ProyectosFields.Estudiante.IDENTIFICADOR, ProyectosFields.Estudiante.NOMBRE,
                                ProyectosFields.Estudiante.EMAIL, ProyectosFields.Estudiante.OCURRIDO_EN));
        assertThat(estudiante.estaEliminado()).isTrue();
    }
}
