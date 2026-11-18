package com.arquisoft.proyectos.domain.estadoproyectogrado.rules.impl;

import com.arquisoft.proyectos.domain.estadoproyectogrado.exception.UsuarioSinAccesoEstadosProyectoGradoException;
import com.arquisoft.proyectos.domain.estadoproyectogrado.model.AccesoEstadosProyectoGrado;
import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UsuarioConAccesoEstadosProyectoGradoRuleImplTest {

    private final UsuarioConAccesoEstadosProyectoGradoRuleImpl regla = new UsuarioConAccesoEstadosProyectoGradoRuleImpl();

    @Test
    void debePasar_cuandoEsCoordinador() {
        // Arrange
        var coordinador = UtilUUID.generarNuevoUUID();
        var acceso = new AccesoEstadosProyectoGrado(UtilUUID.generarNuevoUUID(), coordinador, coordinador, false);

        // Act & Assert
        assertThatCode(() -> regla.validar(acceso)).doesNotThrowAnyException();
    }

    @Test
    void debePasar_cuandoEsAsesorAsignado() {
        // Arrange
        var acceso = new AccesoEstadosProyectoGrado(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                UtilUUID.generarNuevoUUID(), true);

        // Act & Assert
        assertThatCode(() -> regla.validar(acceso)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzar_cuandoNoEsNingunoDeLosDos() {
        // Arrange
        var acceso = new AccesoEstadosProyectoGrado(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                UtilUUID.generarNuevoUUID(), false);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(acceso))
                .isInstanceOf(UsuarioSinAccesoEstadosProyectoGradoException.class)
                .extracting("codigoError")
                .isEqualTo(ProyectosCodes.EstadoProyectoGrado.USUARIO_SIN_ACCESO);
    }

    @Test
    void debeLanzar_cuandoElProyectoNoTieneCoordinadorYNoHayVinculo() {
        // Arrange
        var acceso = new AccesoEstadosProyectoGrado(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                UtilUUID.obtenerUUIDPorDefecto(), false);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(acceso))
                .isInstanceOf(UsuarioSinAccesoEstadosProyectoGradoException.class);
    }
}
