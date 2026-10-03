package com.arquisoft.usuarios.domain.estudiante;

import com.arquisoft.shared.validation.DomainValidationException;
import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.message.constant.UsuariosFields;
import com.arquisoft.shared.util.UtilFecha;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EstudianteDomainTest {

    @Test
    void debeCrearEstudiante_cuandoUsuarioEsValido() {
        // Arrange
        var usuario = UUID.randomUUID();

        // Act
        var estudiante = EstudianteDomain.crear(usuario);

        // Assert
        assertThat(estudiante.getUsuario()).isEqualTo(usuario);
    }

    @Test
    void debeLanzarValidacion_cuandoUsuarioEsNulo() {
        // Act & Assert
        assertThatThrownBy(() -> EstudianteDomain.crear(null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> {
                    var validationEx = (DomainValidationException) ex;
                    assertThat(validationEx.getValidationResult().getErrores())
                            .anySatisfy(error -> {
                                assertThat(error.campo()).isEqualTo(UsuariosFields.Estudiante.USUARIO);
                                assertThat(error.codigoError())
                                        .isEqualTo(UsuariosCodes.Estudiante.USUARIO_REQUERIDO);
                            });
                });
    }

    @Test
    void debeReconstruirEstudiante_sinValidar() {
        // Act
        var estudiante = EstudianteDomain.reconstruir(null, null);

        // Assert
        assertThat(estudiante.getUsuario()).isNull();
        assertThat(estudiante.getEliminadoEn()).isEqualTo(UtilFecha.VACIO);
    }

    @Test
    void debeNacerVigente_cuandoSeCrea() {
        // Act
        var estudiante = EstudianteDomain.crear(UUID.randomUUID());

        // Assert
        assertThat(estudiante.getEliminadoEn()).isEqualTo(UtilFecha.VACIO);
        assertThat(estudiante.estaEliminado()).isFalse();
        assertThat(estudiante.esVacio()).isFalse();
    }

    @Test
    void debeQuedarEliminado_cuandoSeRemueve() {
        // Arrange
        var estudiante = EstudianteDomain.crear(UUID.randomUUID());
        var instante = Instant.parse("2026-09-16T10:00:00Z");

        // Act
        estudiante.remover(instante);

        // Assert
        assertThat(estudiante.getEliminadoEn()).isEqualTo(instante);
        assertThat(estudiante.estaEliminado()).isTrue();
    }

    @Test
    void debeQuedarVigente_cuandoSeReactivaUnEliminado() {
        // Arrange
        var estudiante = EstudianteDomain.reconstruir(UUID.randomUUID(), Instant.parse("2026-09-16T10:00:00Z"));

        // Act
        estudiante.reactivar();

        // Assert
        assertThat(estudiante.getEliminadoEn()).isEqualTo(UtilFecha.VACIO);
        assertThat(estudiante.estaEliminado()).isFalse();
    }

    @Test
    void debeExponerCentinelaVacioNoEliminado_cuandoSeConsultaVacio() {
        // Act
        var vacio = EstudianteDomain.VACIO;

        // Assert
        assertThat(vacio.esVacio()).isTrue();
        assertThat(vacio.estaEliminado()).isFalse();
    }
}
