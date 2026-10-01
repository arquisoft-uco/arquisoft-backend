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
        UUID asesor = UUID.randomUUID();
        var propiedad = new PropiedadAsesorFicha(UUID.randomUUID(), asesor, asesor);

        // Act & Assert
        assertThatCode(() -> regla.validar(propiedad)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarExcepcion_cuandoElAsesorNoEsPropietario() {
        // Arrange
        UUID fichaPerfil = UUID.randomUUID();
        UUID asesorEsperado = UUID.randomUUID();
        UUID asesorSolicitante = UUID.randomUUID();
        var propiedad = new PropiedadAsesorFicha(fichaPerfil, asesorEsperado, asesorSolicitante);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(propiedad))
                .isInstanceOf(FichaNoPerteneceAsesorException.class)
                .hasMessageContaining(asesorSolicitante.toString())
                .hasMessageContaining(fichaPerfil.toString());
    }

    @Test
    void debeLanzarExcepcion_cuandoElAsesorEsperadoEsNulo() {
        // Arrange — ficha sin asesor asociado (p.ej. FichaPerfilDomain.VACIO)
        UUID fichaPerfil = UUID.randomUUID();
        UUID asesorSolicitante = UUID.randomUUID();
        var propiedad = new PropiedadAsesorFicha(fichaPerfil, null, asesorSolicitante);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(propiedad))
                .isInstanceOf(FichaNoPerteneceAsesorException.class);
    }
}
