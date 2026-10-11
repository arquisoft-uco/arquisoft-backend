package com.arquisoft.proyectos.domain.proyectogrado.rules.impl;

import com.arquisoft.proyectos.domain.estadoproyectogrado.EstadoProyectoGrado;
import com.arquisoft.proyectos.domain.proyectogrado.exception.ProyectoGradoFinalizadoException;
import com.arquisoft.proyectos.domain.proyectogrado.model.EstadoActualProyectoGrado;
import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProyectoGradoNoFinalizadoRuleImplTest {

    private final ProyectoGradoNoFinalizadoRuleImpl regla = new ProyectoGradoNoFinalizadoRuleImpl();

    @Test
    void debePasar_cuandoEstaEnCualquierEstadoDistintoDeFinalizado() {
        // Arrange
        var estados = List.of(EstadoProyectoGrado.EN_PROCESO, EstadoProyectoGrado.LISTO_PARA_REVISION,
                EstadoProyectoGrado.ATRASADO);

        // Act & Assert
        for (var estado : estados) {
            var actual = new EstadoActualProyectoGrado(UtilUUID.generarNuevoUUID(), estado);
            assertThatCode(() -> regla.validar(actual)).doesNotThrowAnyException();
        }
    }

    @Test
    void debeLanzarFinalizado_cuandoEstaFinalizado() {
        // Arrange
        var actual = new EstadoActualProyectoGrado(UtilUUID.generarNuevoUUID(), EstadoProyectoGrado.FINALIZADO);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(actual))
                .isInstanceOf(ProyectoGradoFinalizadoException.class)
                .extracting("codigoError")
                .isEqualTo(ProyectosCodes.ProyectoGrado.FINALIZADO);
    }
}
