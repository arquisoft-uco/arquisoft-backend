package com.arquisoft.fichas.domain.estudiante;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EstudianteDomainTest {

    @Test
    void debeCrearEstudiante_cuandoLosDatosSonValidos() {
        // Arrange
        var id = UUID.randomUUID();
        var ocurridoEn = Instant.now();

        // Act
        var estudiante = EstudianteDomain.crear(
                id, "20161020123", "Juan Pérez", "juan.perez@example.com", ocurridoEn);

        // Assert
        assertThat(estudiante.getId()).isEqualTo(id);
        assertThat(estudiante.getIdentificador()).isEqualTo("20161020123");
        assertThat(estudiante.getNombre()).isEqualTo("Juan Pérez");
        assertThat(estudiante.getEmail()).isEqualTo("juan.perez@example.com");
        assertThat(estudiante.getOcurridoEn()).isEqualTo(ocurridoEn);
    }

    @Test
    void debeAcumularErrores_cuandoIdentificadorYEmailFaltan() {
        // Act & Assert
        assertThatThrownBy(() -> EstudianteDomain.crear(
                UUID.randomUUID(), " ", "Juan Pérez", " ", Instant.now()))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> {
                    var errores = ((DomainValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores)
                            .extracting(e -> e.campo())
                            .contains(FichasFields.Estudiante.IDENTIFICADOR, FichasFields.Estudiante.EMAIL);
                    assertThat(errores)
                            .extracting(e -> e.codigoError())
                            .contains(FichasCodes.Estudiante.IDENTIFICADOR_REQUERIDO,
                                    FichasCodes.Estudiante.EMAIL_REQUERIDO);
                });
    }

    @Test
    void debeLanzarValidacion_cuandoOcurridoEnEsNulo() {
        // Act & Assert
        assertThatThrownBy(() -> EstudianteDomain.crear(
                UUID.randomUUID(), "20161020123", "Juan Pérez", "juan.perez@example.com", null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> {
                    var errores = ((DomainValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores)
                            .anySatisfy(e -> assertThat(e.campo())
                                    .isEqualTo(FichasFields.Estudiante.OCURRIDO_EN));
                });
    }

    @Test
    void debeReconstruir_cuandoDatosValidos() {
        // Arrange
        var id = UUID.randomUUID();
        var identificador = "1234567890";
        var nombre = "Juan Pérez";
        var email = "juan.perez@example.com";
        var ocurridoEn = Instant.now();

        // Act
        var estudiante = EstudianteDomain.reconstruir(id, identificador, nombre, email, ocurridoEn, UtilFecha.VACIO);

        // Assert
        assertThat(estudiante).isNotNull();
        assertThat(estudiante.getId()).isEqualTo(id);
        assertThat(estudiante.getIdentificador()).isEqualTo(identificador);
        assertThat(estudiante.getNombre()).isEqualTo(nombre);
        assertThat(estudiante.getEmail()).isEqualTo(email);
        assertThat(estudiante.getOcurridoEn()).isEqualTo(ocurridoEn);
    }

    @Test
    void debeReconstruirSinValidar_cuandoReconstruirEsInvocado() {
        // Arrange
        var id = UUID.randomUUID();
        String identificador = null;
        String nombre = null;
        String email = null;
        Instant ocurridoEn = null;

        // Act
        var estudiante = EstudianteDomain.reconstruir(id, identificador, nombre, email, ocurridoEn, UtilFecha.VACIO);

        // Assert
        assertThat(estudiante).isNotNull();
        assertThat(estudiante.getId()).isEqualTo(id);
        assertThat(estudiante.getIdentificador()).isNull();
        assertThat(estudiante.getNombre()).isNull();
        assertThat(estudiante.getEmail()).isNull();
        assertThat(estudiante.getOcurridoEn()).isNull();
    }

    @Test
    void debeNacerVigente_cuandoSeCreaYReconstruirNormalizaEliminadoNulo() {
        // Act
        var creado = EstudianteDomain.crear(
                UUID.randomUUID(), "20161020123", "Juan Pérez", "juan.perez@example.com", Instant.now());
        var reconstruido = EstudianteDomain.reconstruir(
                UUID.randomUUID(), "20161020123", "Juan Pérez", "juan.perez@example.com", Instant.now(), null);

        // Assert
        assertThat(creado.estaEliminado()).isFalse();
        assertThat(reconstruido.getEliminadoEn()).isEqualTo(UtilFecha.VACIO);
        assertThat(EstudianteDomain.VACIO.esVacio()).isTrue();
        assertThat(EstudianteDomain.VACIO.estaEliminado()).isFalse();
    }

    @Test
    void debeFijarEliminadoEnYOcurridoEn_cuandoSeRemueve() {
        // Arrange
        var estudiante = EstudianteDomain.crear(UUID.randomUUID(), "20161020123", "Juan Pérez",
                "juan.perez@example.com", Instant.parse("2026-09-01T10:00:00Z"));
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
        var estudiante = EstudianteDomain.reconstruir(UUID.randomUUID(), "20161020123", "Juan Pérez",
                "juan.perez@example.com", eliminadoEn, eliminadoEn);
        var ocurridoEn = Instant.parse("2026-09-16T10:00:00Z");

        // Act
        estudiante.reactivar("20161020999", "Juan P. Gómez", "juan.gomez@example.com", ocurridoEn);

        // Assert
        assertThat(estudiante.estaEliminado()).isFalse();
        assertThat(estudiante.getEliminadoEn()).isEqualTo(UtilFecha.VACIO);
        assertThat(estudiante.getIdentificador()).isEqualTo("20161020999");
        assertThat(estudiante.getNombre()).isEqualTo("Juan P. Gómez");
        assertThat(estudiante.getEmail()).isEqualTo("juan.gomez@example.com");
        assertThat(estudiante.getOcurridoEn()).isEqualTo(ocurridoEn);
    }

    @Test
    void debeAcumularErroresYSeguirEliminado_cuandoSeReactivaConDatosInvalidos() {
        // Arrange
        var eliminadoEn = Instant.parse("2026-09-10T10:00:00Z");
        var estudiante = EstudianteDomain.reconstruir(UUID.randomUUID(), "20161020123", "Juan Pérez",
                "juan.perez@example.com", eliminadoEn, eliminadoEn);

        // Act & Assert
        assertThatThrownBy(() -> estudiante.reactivar(" ", " ", "juan.perez@example.com", null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .extracting(e -> e.campo())
                        .contains(FichasFields.Estudiante.IDENTIFICADOR, FichasFields.Estudiante.NOMBRE,
                                FichasFields.Estudiante.OCURRIDO_EN));
        assertThat(estudiante.estaEliminado()).isTrue();
    }

    @Test
    void debeActualizarDatos_cuandoEstudianteVigente() {
        // Arrange
        var estudiante = EstudianteDomain.crear(UUID.randomUUID(), "20161020123", "Juan Pérez",
                "juan.perez@example.com", Instant.parse("2026-09-01T10:00:00Z"));
        var ocurridoEn = Instant.parse("2026-09-16T10:00:00Z");

        // Act
        estudiante.actualizar("20161020999", "Juan Actualizado", "actualizado@example.com", ocurridoEn);

        // Assert
        assertThat(estudiante.getIdentificador()).isEqualTo("20161020999");
        assertThat(estudiante.getNombre()).isEqualTo("Juan Actualizado");
        assertThat(estudiante.getEmail()).isEqualTo("actualizado@example.com");
        assertThat(estudiante.getOcurridoEn()).isEqualTo(ocurridoEn);
        assertThat(estudiante.estaEliminado()).isFalse();
    }

    @Test
    void debeConservarEliminadoEn_cuandoSeActualizaUnEstudianteEliminado() {
        // Arrange
        var eliminadoEn = Instant.parse("2026-09-10T10:00:00Z");
        var estudiante = EstudianteDomain.reconstruir(UUID.randomUUID(), "20161020123", "Juan Pérez",
                "juan.perez@example.com", eliminadoEn, eliminadoEn);
        var ocurridoEn = Instant.parse("2026-09-16T10:00:00Z");

        // Act
        estudiante.actualizar("20161020999", "Juan Actualizado", "actualizado@example.com", ocurridoEn);

        // Assert — actualizar no toca eliminadoEn, a diferencia de reactivar
        assertThat(estudiante.estaEliminado()).isTrue();
        assertThat(estudiante.getEliminadoEn()).isEqualTo(eliminadoEn);
        assertThat(estudiante.getIdentificador()).isEqualTo("20161020999");
        assertThat(estudiante.getOcurridoEn()).isEqualTo(ocurridoEn);
    }
}
