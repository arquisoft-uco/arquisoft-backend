package com.arquisoft.fichas.domain.estudiantefichaperfil.rules.impl;

import com.arquisoft.fichas.domain.estudiantefichaperfil.model.PropiedadFicha;
import com.arquisoft.fichas.domain.fichaperfil.exception.FichaNoPropietarioException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EstudiantePropietarioFichaRuleImplTest {

    private final EstudiantePropietarioFichaRuleImpl regla = new EstudiantePropietarioFichaRuleImpl();

    @Test
    void debeLanzarExcepcion_cuandoElEstudianteNoEsPropietario() {
        // Arrange
        var propiedad = new PropiedadFicha(UUID.randomUUID(), UUID.randomUUID(), false);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(propiedad))
                .isInstanceOf(FichaNoPropietarioException.class);
    }

    @Test
    void debePasar_cuandoElEstudianteEsPropietario() {
        // Arrange
        var propiedad = new PropiedadFicha(UUID.randomUUID(), UUID.randomUUID(), true);

        // Act & Assert
        assertThatCode(() -> regla.validar(propiedad)).doesNotThrowAnyException();
    }
}
