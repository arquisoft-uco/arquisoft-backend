package com.arquisoft.usuarios.domain.asesorficha;

import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.message.constant.UsuariosFields;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AsesorFichaDomainTest {

    @Test
    void debeCrearAsesorFichaVigente_cuandoUsuarioEsValido() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();

        // Act
        var asesorFicha = AsesorFichaDomain.crear(usuario);

        // Assert
        assertThat(asesorFicha.getUsuario()).isEqualTo(usuario);
        assertThat(asesorFicha.getEliminadoEn()).isEqualTo(UtilFecha.VACIO);
        assertThat(asesorFicha.estaEliminado()).isFalse();
        assertThat(asesorFicha.esVacio()).isFalse();
    }

    @Test
    void debeLanzarValidacion_cuandoUsuarioEsNulo() {
        // Act & Assert
        assertThatThrownBy(() -> AsesorFichaDomain.crear(null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> {
                    var validationEx = (DomainValidationException) ex;
                    assertThat(validationEx.getValidationResult().getErrores())
                            .anySatisfy(error -> {
                                assertThat(error.campo()).isEqualTo(UsuariosFields.AsesorFicha.USUARIO);
                                assertThat(error.codigoError())
                                        .isEqualTo(UsuariosCodes.AsesorFicha.USUARIO_REQUERIDO);
                            });
                });
    }

    @Test
    void debeReconstruirAsesorFicha_sinValidar() {
        // Act
        var asesorFicha = AsesorFichaDomain.reconstruir(null, null);

        // Assert
        assertThat(asesorFicha.getUsuario()).isNull();
        assertThat(asesorFicha.getEliminadoEn()).isEqualTo(UtilFecha.VACIO);
        assertThat(asesorFicha.estaEliminado()).isFalse();
    }

    @Test
    void debeQuedarEliminado_cuandoSeRemueve() {
        // Arrange
        var asesorFicha = AsesorFichaDomain.crear(UtilUUID.generarNuevoUUID());
        var instante = Instant.parse("2026-09-24T10:00:00Z");

        // Act
        asesorFicha.remover(instante);

        // Assert
        assertThat(asesorFicha.getEliminadoEn()).isEqualTo(instante);
        assertThat(asesorFicha.estaEliminado()).isTrue();
    }

    @Test
    void debeQuedarVigente_cuandoSeReactivaUnEliminado() {
        // Arrange
        var asesorFicha = AsesorFichaDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), Instant.parse("2026-09-24T10:00:00Z"));

        // Act
        asesorFicha.reactivar();

        // Assert
        assertThat(asesorFicha.getEliminadoEn()).isEqualTo(UtilFecha.VACIO);
        assertThat(asesorFicha.estaEliminado()).isFalse();
    }

    @Test
    void debeExponerCentinelaVacioNoEliminado_cuandoSeConsultaVacio() {
        // Act
        var vacio = AsesorFichaDomain.VACIO;

        // Assert
        assertThat(vacio.esVacio()).isTrue();
        assertThat(vacio.estaEliminado()).isFalse();
    }
}
