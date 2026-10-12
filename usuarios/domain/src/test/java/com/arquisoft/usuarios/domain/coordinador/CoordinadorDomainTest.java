package com.arquisoft.usuarios.domain.coordinador;

import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.message.constant.UsuariosFields;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CoordinadorDomainTest {

    @Test
    void debeCrearCoordinador_cuandoUsuarioEsValido() {
        // Arrange
        var usuario = UUID.randomUUID();

        // Act
        var coordinador = CoordinadorDomain.crear(usuario);

        // Assert
        assertThat(coordinador.getUsuario()).isEqualTo(usuario);
    }

    @Test
    void debeLanzarValidacion_cuandoUsuarioEsNulo() {
        // Act & Assert
        assertThatThrownBy(() -> CoordinadorDomain.crear(null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> {
                    var validationEx = (DomainValidationException) ex;
                    assertThat(validationEx.getValidationResult().getErrores())
                            .anySatisfy(error -> {
                                assertThat(error.campo()).isEqualTo(UsuariosFields.Coordinador.USUARIO);
                                assertThat(error.codigoError())
                                        .isEqualTo(UsuariosCodes.Coordinador.USUARIO_REQUERIDO);
                            });
                });
    }

    @Test
    void debeNacerVigente_cuandoSeCrea() {
        // Act
        var coordinador = CoordinadorDomain.crear(UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(coordinador.getEliminadoEn()).isEqualTo(UtilFecha.VACIO);
        assertThat(coordinador.estaEliminado()).isFalse();
        assertThat(coordinador.esVacio()).isFalse();
    }

    @Test
    void debeReconstruirCoordinador_sinValidar() {
        // Act
        var coordinador = CoordinadorDomain.reconstruir(null, null);

        // Assert
        assertThat(coordinador.getUsuario()).isNull();
        assertThat(coordinador.getEliminadoEn()).isEqualTo(UtilFecha.VACIO);
        assertThat(coordinador.estaEliminado()).isFalse();
    }

    @Test
    void debeQuedarEliminado_cuandoSeRemueve() {
        // Arrange
        var coordinador = CoordinadorDomain.crear(UtilUUID.generarNuevoUUID());
        var instante = Instant.parse("2026-09-24T10:00:00Z");

        // Act
        coordinador.remover(instante);

        // Assert
        assertThat(coordinador.getEliminadoEn()).isEqualTo(instante);
        assertThat(coordinador.estaEliminado()).isTrue();
    }

    @Test
    void debeQuedarVigente_cuandoSeReactivaUnEliminado() {
        // Arrange
        var coordinador = CoordinadorDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), Instant.parse("2026-09-24T10:00:00Z"));

        // Act
        coordinador.reactivar();

        // Assert
        assertThat(coordinador.getEliminadoEn()).isEqualTo(UtilFecha.VACIO);
        assertThat(coordinador.estaEliminado()).isFalse();
    }

    @Test
    void debeExponerCentinelaVacioNoEliminado_cuandoSeConsultaVacio() {
        // Act
        var vacio = CoordinadorDomain.VACIO;

        // Assert
        assertThat(vacio.esVacio()).isTrue();
        assertThat(vacio.estaEliminado()).isFalse();
    }
}
