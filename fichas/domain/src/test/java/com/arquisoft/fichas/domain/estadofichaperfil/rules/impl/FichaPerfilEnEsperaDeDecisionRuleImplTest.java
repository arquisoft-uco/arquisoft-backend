package com.arquisoft.fichas.domain.estadofichaperfil.rules.impl;

import com.arquisoft.fichas.domain.estadoficha.EstadoFicha;
import com.arquisoft.fichas.domain.estadofichaperfil.exception.FichaPerfilEnEsperaDeDecisionException;
import com.arquisoft.fichas.domain.estadofichaperfil.model.FichaEnEsperaDeDecision;
import com.arquisoft.shared.message.constant.FichasCodes;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

class FichaPerfilEnEsperaDeDecisionRuleImplTest {

    private static final String TITULO = "Sistema de gestión de proyectos";

    private final FichaPerfilEnEsperaDeDecisionRuleImpl regla = new FichaPerfilEnEsperaDeDecisionRuleImpl();

    @Test
    void debeLanzarExcepcion_cuandoSaleDeDisponibleConEvaluacionesFinalizadas() {
        // Arrange
        var fichaPerfil = UUID.randomUUID();
        var ficha = new FichaEnEsperaDeDecision(fichaPerfil, TITULO, EstadoFicha.DISPONIBLE_PARA_EVALUACION, 2);

        // Act
        var excepcion = catchThrowableOfType(FichaPerfilEnEsperaDeDecisionException.class,
                () -> regla.validar(ficha));

        // Assert
        assertThat(excepcion.getCodigoError()).isEqualTo(FichasCodes.EstadoFichaPerfil.EN_ESPERA_DE_DECISION);
        assertThat(excepcion.getError().getMessage())
                .contains(TITULO)
                .doesNotContain(fichaPerfil.toString());
    }

    @Test
    void debePasar_cuandoEstaDisponibleSinFinalizadas() {
        // Arrange
        var ficha = new FichaEnEsperaDeDecision(UUID.randomUUID(), TITULO,
                EstadoFicha.DISPONIBLE_PARA_EVALUACION, 0);

        // Act & Assert
        assertThatCode(() -> regla.validar(ficha)).doesNotThrowAnyException();
    }

    @Test
    void debePasar_cuandoElEstadoActualEsEnConstruccionConFinalizadas() {
        // Arrange
        var ficha = new FichaEnEsperaDeDecision(UUID.randomUUID(), TITULO, EstadoFicha.EN_CONSTRUCCION, 2);

        // Act & Assert
        assertThatCode(() -> regla.validar(ficha)).doesNotThrowAnyException();
    }

    @Test
    void debePasar_cuandoElEstadoActualEsDescartadaConFinalizadas() {
        // Arrange
        var ficha = new FichaEnEsperaDeDecision(UUID.randomUUID(), TITULO, EstadoFicha.DESCARTADA, 1);

        // Act & Assert
        assertThatCode(() -> regla.validar(ficha)).doesNotThrowAnyException();
    }
}
