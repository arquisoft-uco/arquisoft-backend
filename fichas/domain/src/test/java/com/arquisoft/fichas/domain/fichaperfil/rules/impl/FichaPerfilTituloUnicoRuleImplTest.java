package com.arquisoft.fichas.domain.fichaperfil.rules.impl;

import com.arquisoft.fichas.domain.fichaperfil.exception.FichaTituloDuplicadoException;
import com.arquisoft.fichas.domain.fichaperfil.model.DisponibilidadTituloFicha;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FichaPerfilTituloUnicoRuleImplTest {

    private final FichaPerfilTituloUnicoRuleImpl regla = new FichaPerfilTituloUnicoRuleImpl();

    @Test
    void debeLanzarExcepcion_cuandoElTituloYaEstaTomado() {
        // Arrange
        var disponibilidad = new DisponibilidadTituloFicha("Titulo duplicado", true);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(disponibilidad))
                .isInstanceOf(FichaTituloDuplicadoException.class)
                .hasMessageContaining("Titulo duplicado");
    }

    @Test
    void debePasar_cuandoElTituloEstaLibre() {
        // Arrange
        var disponibilidad = new DisponibilidadTituloFicha("Titulo libre", false);

        // Act & Assert
        assertThatCode(() -> regla.validar(disponibilidad)).doesNotThrowAnyException();
    }
}
