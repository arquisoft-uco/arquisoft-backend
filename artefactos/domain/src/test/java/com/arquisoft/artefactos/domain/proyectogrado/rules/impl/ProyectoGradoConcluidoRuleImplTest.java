package com.arquisoft.artefactos.domain.proyectogrado.rules.impl;

import com.arquisoft.artefactos.domain.proyectogrado.exception.ProyectoGradoNoConcluidoException;
import com.arquisoft.artefactos.domain.proyectogrado.model.EstadoProyectoGrado;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProyectoGradoConcluidoRuleImplTest {

    private final ProyectoGradoConcluidoRuleImpl regla = new ProyectoGradoConcluidoRuleImpl();

    @Test
    void debePasar_cuandoFinalizadoOCancelado() {
        // Arrange
        var estado = new EstadoProyectoGrado(UtilUUID.generarNuevoUUID(), "Proyecto", true, true);

        // Act & Assert
        assertThatCode(() -> regla.validar(estado)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzar_cuandoNoConcluido() {
        // Arrange
        var estado = new EstadoProyectoGrado(UtilUUID.generarNuevoUUID(), "Proyecto", true, false);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(estado))
                .isInstanceOf(ProyectoGradoNoConcluidoException.class)
                .extracting("codigoError")
                .isEqualTo(ArtefactosCodes.ProyectoGrado.NO_CONCLUIDO);
    }
}
