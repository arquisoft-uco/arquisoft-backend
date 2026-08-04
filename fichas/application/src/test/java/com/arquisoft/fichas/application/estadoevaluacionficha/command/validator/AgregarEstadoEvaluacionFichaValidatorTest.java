package com.arquisoft.fichas.application.estadoevaluacionficha.command.validator;

import com.arquisoft.fichas.application.estadoevaluacionficha.command.validator.impl.AgregarEstadoEvaluacionFichaValidatorImpl;
import com.arquisoft.fichas.application.estadoevaluacionficha.exception.EstadoEvaluacionNoEncontradoException;
import com.arquisoft.fichas.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.fichas.domain.estadoevaluacionficha.model.EstadoEvaluacionCriteria;
import com.arquisoft.fichas.domain.estadoevaluacionficha.rules.EstadoEvaluacionNoDuplicadoRule;
import com.arquisoft.fichas.domain.estadoevaluacionficha.rules.EvaluacionFichaExisteRule;
import com.arquisoft.fichas.domain.estadoevaluacionficha.rules.RepresentantePropietarioEvaluacionRule;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.model.PropietarioEvaluacionCriteria;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.inOrder;

@ExtendWith(MockitoExtension.class)
class AgregarEstadoEvaluacionFichaValidatorTest {

    @Mock
    private EvaluacionFichaExisteRule evaluacionFichaExisteRule;

    @Mock
    private RepresentantePropietarioEvaluacionRule representantePropietarioEvaluacionRule;

    @Mock
    private EstadoEvaluacionNoDuplicadoRule estadoEvaluacionNoDuplicadoRule;

    @InjectMocks
    private AgregarEstadoEvaluacionFichaValidatorImpl validator;

    @Test
    void debeAplicarLasReglasEnOrden_cuandoValida() {
        // Arrange
        UUID evaluacion = UUID.randomUUID();
        UUID representante = UUID.randomUUID();
        String estado = EstadoEvaluacion.APROBADA.name();

        // Act
        validator.validar(evaluacion, representante, estado);

        // Assert
        InOrder inOrder = inOrder(evaluacionFichaExisteRule, representantePropietarioEvaluacionRule,
                estadoEvaluacionNoDuplicadoRule);
        inOrder.verify(evaluacionFichaExisteRule).validar(evaluacion);
        inOrder.verify(representantePropietarioEvaluacionRule)
                .validar(new PropietarioEvaluacionCriteria(evaluacion, representante));
        inOrder.verify(estadoEvaluacionNoDuplicadoRule)
                .validar(new EstadoEvaluacionCriteria(evaluacion, estado));
    }

    @Test
    void debeResolverElEstado_cuandoElNombreEsValido() {
        // Act & Assert
        assertThat(validator.resolverEstado(EstadoEvaluacion.APROBADA.name()))
                .isEqualTo(EstadoEvaluacion.APROBADA);
    }

    @Test
    void debeLanzarExcepcion_cuandoElEstadoNoExiste() {
        // Act & Assert
        assertThatThrownBy(() -> validator.resolverEstado("ESTADO_INEXISTENTE"))
                .isInstanceOf(EstadoEvaluacionNoEncontradoException.class);
    }
}
