package com.arquisoft.artefactos.domain.proyectogrado.rules.impl;

import com.arquisoft.artefactos.domain.proyectogrado.exception.ProyectoGradoCongeladoException;
import com.arquisoft.artefactos.domain.proyectogrado.model.EstadoProyectoGrado;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProyectoGradoNoCongeladoRuleImplTest {

    private final ProyectoGradoNoCongeladoRuleImpl regla = new ProyectoGradoNoCongeladoRuleImpl();

    @Test
    void debePasar_cuandoNoCongelado() {
        // Arrange
        var estado = new EstadoProyectoGrado(UtilUUID.generarNuevoUUID(), "Proyecto", false, false);

        // Act & Assert
        assertThatCode(() -> regla.validar(estado)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzar_cuandoCongelado() {
        // Arrange
        var estado = new EstadoProyectoGrado(UtilUUID.generarNuevoUUID(), "Proyecto", true, false);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(estado))
                .isInstanceOf(ProyectoGradoCongeladoException.class)
                .extracting("codigoError")
                .isEqualTo(ArtefactosCodes.ProyectoGrado.CONGELADO);
    }

    @Test
    void debeOcultarIdentificadores_enMensajeDeCliente() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var estado = new EstadoProyectoGrado(proyectoGrado, "Proyecto Alfa", true, true);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(estado))
                .isInstanceOf(ProyectoGradoCongeladoException.class)
                .satisfies(ex -> {
                    var excepcion = (ProyectoGradoCongeladoException) ex;
                    assertThat(excepcion.getError().getMessage()).contains("Proyecto Alfa")
                            .doesNotContain(proyectoGrado.toString());
                    assertThat(excepcion.getMessage()).contains(proyectoGrado.toString());
                });
    }
}
