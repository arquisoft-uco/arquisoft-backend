package com.arquisoft.fichas.domain.fichaperfil.rules.impl;

import com.arquisoft.fichas.domain.fichaperfil.exception.FichaNoPerteneceAsesorException;
import com.arquisoft.fichas.domain.fichaperfil.model.PropiedadAsesorFicha;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AsesorFichaPropietarioRuleImplTest {

    private final AsesorFichaPropietarioRuleImpl regla = new AsesorFichaPropietarioRuleImpl();

    @Test
    void debePasar_cuandoElAsesorEsPropietario() {
        // Arrange
        var propiedad = new PropiedadAsesorFicha(UUID.randomUUID(), UUID.randomUUID(), true);

        // Act & Assert
        assertThatCode(() -> regla.validar(propiedad)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarExcepcion_cuandoElAsesorNoEsPropietario() {
        // Arrange
        UUID fichaPerfil = UUID.randomUUID();
        UUID asesorFicha = UUID.randomUUID();
        var propiedad = new PropiedadAsesorFicha(fichaPerfil, asesorFicha, false);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(propiedad))
                .isInstanceOf(FichaNoPerteneceAsesorException.class)
                .hasMessageContaining(asesorFicha.toString())
                .hasMessageContaining(fichaPerfil.toString());
    }
}
