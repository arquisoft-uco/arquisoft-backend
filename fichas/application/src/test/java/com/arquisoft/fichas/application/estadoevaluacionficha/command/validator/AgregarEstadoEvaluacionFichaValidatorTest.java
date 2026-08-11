package com.arquisoft.fichas.application.estadoevaluacionficha.command.validator;

import com.arquisoft.fichas.application.estadoevaluacionficha.command.validator.impl.AgregarEstadoEvaluacionFichaValidatorImpl;
import com.arquisoft.fichas.domain.estadoevaluacionficha.AgregacionEstadoEvaluacionFichaDomain;
import com.arquisoft.fichas.domain.estadoevaluacionficha.rules.EstadoEvaluacionNoDuplicadoRule;
import com.arquisoft.fichas.domain.estadoevaluacionficha.rules.EvaluacionFichaExisteRule;
import com.arquisoft.fichas.domain.estadoevaluacionficha.rules.RepresentantePropietarioEvaluacionRule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

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
        var entrada = AgregacionEstadoEvaluacionFichaDomain.crear(evaluacion, "APROBADA", representante);

        // Act
        validator.validar(entrada);

        // Assert
        InOrder inOrder = inOrder(evaluacionFichaExisteRule, representantePropietarioEvaluacionRule,
                estadoEvaluacionNoDuplicadoRule);
        inOrder.verify(evaluacionFichaExisteRule).validar(evaluacion);
        inOrder.verify(representantePropietarioEvaluacionRule).validar(entrada);
        inOrder.verify(estadoEvaluacionNoDuplicadoRule).validar(entrada);
    }
}
