package com.arquisoft.usuarios.domain.asesor;

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

class AsesorDomainTest {

    @Test
    void debeCrearAsesor_cuandoUsuarioEsValido() {
        // Arrange
        var usuario = UUID.randomUUID();

        // Act
        var asesor = AsesorDomain.crear(usuario);

        // Assert
        assertThat(asesor.getUsuario()).isEqualTo(usuario);
    }

    @Test
    void debeLanzarValidacion_cuandoUsuarioEsNulo() {
        // Act & Assert
        assertThatThrownBy(() -> AsesorDomain.crear(null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> {
                    var validationEx = (DomainValidationException) ex;
                    assertThat(validationEx.getValidationResult().getErrores())
                            .anySatisfy(error -> {
                                assertThat(error.campo()).isEqualTo(UsuariosFields.Asesor.USUARIO);
                                assertThat(error.codigoError())
                                        .isEqualTo(UsuariosCodes.Asesor.USUARIO_REQUERIDO);
                            });
                });
    }

    @Test
    void debeReconstruirAsesor_sinValidar() {
        // Act
        var asesor = AsesorDomain.reconstruir(null, null);

        // Assert
        assertThat(asesor.getUsuario()).isNull();
        assertThat(asesor.getEliminadoEn()).isEqualTo(UtilFecha.VACIO);
        assertThat(asesor.estaEliminado()).isFalse();
    }

    @Test
    void debeNacerVigente_cuandoSeCrea() {
        // Act
        var asesor = AsesorDomain.crear(UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(asesor.getEliminadoEn()).isEqualTo(UtilFecha.VACIO);
        assertThat(asesor.estaEliminado()).isFalse();
        assertThat(asesor.esVacio()).isFalse();
    }

    @Test
    void debeQuedarEliminado_cuandoSeRemueve() {
        // Arrange
        var asesor = AsesorDomain.crear(UtilUUID.generarNuevoUUID());
        var instante = Instant.parse("2026-09-23T10:00:00Z");

        // Act
        asesor.remover(instante);

        // Assert
        assertThat(asesor.getEliminadoEn()).isEqualTo(instante);
        assertThat(asesor.estaEliminado()).isTrue();
    }

    @Test
    void debeQuedarVigente_cuandoSeReactivaUnEliminado() {
        // Arrange
        var asesor = AsesorDomain.reconstruir(UtilUUID.generarNuevoUUID(), Instant.parse("2026-09-23T10:00:00Z"));

        // Act
        asesor.reactivar();

        // Assert
        assertThat(asesor.getEliminadoEn()).isEqualTo(UtilFecha.VACIO);
        assertThat(asesor.estaEliminado()).isFalse();
    }

    @Test
    void debeExponerCentinelaVacioNoEliminado_cuandoSeConsultaVacio() {
        // Act
        var vacio = AsesorDomain.VACIO;

        // Assert
        assertThat(vacio.esVacio()).isTrue();
        assertThat(vacio.estaEliminado()).isFalse();
    }
}
