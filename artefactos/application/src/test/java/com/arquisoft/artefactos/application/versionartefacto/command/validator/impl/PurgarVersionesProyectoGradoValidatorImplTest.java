package com.arquisoft.artefactos.application.versionartefacto.command.validator.impl;

import com.arquisoft.artefactos.domain.proyectogrado.exception.ProyectoGradoNoEncontradoException;
import com.arquisoft.artefactos.domain.proyectogrado.exception.ProyectoGradoNoConcluidoException;
import com.arquisoft.artefactos.domain.proyectogrado.model.EstadoProyectoGrado;
import com.arquisoft.artefactos.domain.proyectogrado.model.ExistenciaProyectoGrado;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PurgarVersionesProyectoGradoValidatorImplTest {

    private final PurgarVersionesProyectoGradoValidatorImpl validator = new PurgarVersionesProyectoGradoValidatorImpl();

    @Test
    void debeLanzar_cuandoProyectoNoExiste() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var existencia = new ExistenciaProyectoGrado(id, false);
        var estado = new EstadoProyectoGrado(id, "", false, true);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(existencia, estado))
                .isInstanceOf(ProyectoGradoNoEncontradoException.class);
    }

    @Test
    void debeLanzar_cuandoNoFinalizado() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var existencia = new ExistenciaProyectoGrado(id, true);
        var estado = new EstadoProyectoGrado(id, "Proyecto", true, false);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(existencia, estado))
                .isInstanceOf(ProyectoGradoNoConcluidoException.class);
    }

    @Test
    void debeValidarExistenciaAntesQueFinalizacion_cuandoAmbasFallan() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var existencia = new ExistenciaProyectoGrado(id, false);
        var estado = new EstadoProyectoGrado(id, "", false, false);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(existencia, estado))
                .isInstanceOf(ProyectoGradoNoEncontradoException.class);
    }

    @Test
    void debePasar_cuandoFinalizado() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var existencia = new ExistenciaProyectoGrado(id, true);
        var estado = new EstadoProyectoGrado(id, "Proyecto", true, true);

        // Act & Assert
        assertThatCode(() -> validator.validar(existencia, estado)).doesNotThrowAnyException();
    }
}
