package com.arquisoft.fichas.domain.estadofichaperfil.rules.impl;

import com.arquisoft.fichas.domain.estadoficha.EstadoFicha;
import com.arquisoft.fichas.domain.estadofichaperfil.exception.FichaPerfilConEvaluacionEnCursoException;
import com.arquisoft.fichas.domain.estadofichaperfil.model.EvaluacionesEnCursoFicha;
import com.arquisoft.shared.message.constant.FichasCodes;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

class FichaPerfilSinEvaluacionEnCursoRuleImplTest {

    private static final String TITULO = "Sistema de gestión de proyectos";

    private final FichaPerfilSinEvaluacionEnCursoRuleImpl regla = new FichaPerfilSinEvaluacionEnCursoRuleImpl();

    @Test
    void debeLanzarExcepcion_cuandoSaleDeDisponibleConEvaluacionesEnCurso() {
        // Arrange
        var fichaPerfil = UUID.randomUUID();
        var evaluaciones = new EvaluacionesEnCursoFicha(fichaPerfil, TITULO,
                EstadoFicha.DISPONIBLE_PARA_EVALUACION, 2);

        // Act
        var excepcion = catchThrowableOfType(FichaPerfilConEvaluacionEnCursoException.class,
                () -> regla.validar(evaluaciones));

        // Assert
        assertThat(excepcion.getCodigoError()).isEqualTo(FichasCodes.EstadoFichaPerfil.EVALUACION_EN_CURSO);
        assertThat(excepcion.getError().getMessage())
                .contains(TITULO)
                .doesNotContain(fichaPerfil.toString());
    }

    @Test
    void debePasar_cuandoElEstadoActualNoEsDisponibleAunqueHayaEvaluacionesEnCurso() {
        // Arrange
        var evaluaciones = new EvaluacionesEnCursoFicha(UUID.randomUUID(), TITULO, EstadoFicha.EN_CONSTRUCCION, 3);

        // Act & Assert
        assertThatCode(() -> regla.validar(evaluaciones)).doesNotThrowAnyException();
    }

    @Test
    void debePasar_cuandoEstaDisponibleSinEvaluacionesEnCurso() {
        // Arrange
        var evaluaciones = new EvaluacionesEnCursoFicha(UUID.randomUUID(), TITULO,
                EstadoFicha.DISPONIBLE_PARA_EVALUACION, 0);

        // Act & Assert
        assertThatCode(() -> regla.validar(evaluaciones)).doesNotThrowAnyException();
    }
}
