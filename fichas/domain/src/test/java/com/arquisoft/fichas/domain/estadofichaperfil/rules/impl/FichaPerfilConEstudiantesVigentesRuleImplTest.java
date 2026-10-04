package com.arquisoft.fichas.domain.estadofichaperfil.rules.impl;

import com.arquisoft.fichas.domain.estadofichaperfil.exception.FichaPerfilSinEstudiantesVigentesException;
import com.arquisoft.fichas.domain.estadofichaperfil.model.IntegrantesVigentesFicha;
import com.arquisoft.shared.message.constant.FichasCodes;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FichaPerfilConEstudiantesVigentesRuleImplTest {

    private final FichaPerfilConEstudiantesVigentesRuleImpl regla = new FichaPerfilConEstudiantesVigentesRuleImpl();

    @Test
    void debeLanzarExcepcion_cuandoLaFichaNoTieneEstudiantesVigentes() {
        // Arrange
        var integrantes = new IntegrantesVigentesFicha(UUID.randomUUID(), 0);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(integrantes))
                .isInstanceOf(FichaPerfilSinEstudiantesVigentesException.class)
                .extracting("codigoError")
                .isEqualTo(FichasCodes.EstadoFichaPerfil.SIN_ESTUDIANTES_VIGENTES);
    }

    @Test
    void debePasar_cuandoLaFichaTieneUnEstudianteVigente() {
        // Arrange
        var integrantes = new IntegrantesVigentesFicha(UUID.randomUUID(), 1);

        // Act & Assert
        assertThatCode(() -> regla.validar(integrantes)).doesNotThrowAnyException();
    }
}
