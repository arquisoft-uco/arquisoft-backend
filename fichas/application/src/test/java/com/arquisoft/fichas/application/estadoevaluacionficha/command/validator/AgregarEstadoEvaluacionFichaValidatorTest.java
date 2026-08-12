package com.arquisoft.fichas.application.estadoevaluacionficha.command.validator;

import com.arquisoft.fichas.application.estadoevaluacionficha.command.validator.impl.AgregarEstadoEvaluacionFichaValidatorImpl;
import com.arquisoft.fichas.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.fichas.domain.estadoevaluacionficha.AgregacionEstadoEvaluacionFichaDomain;
import com.arquisoft.fichas.domain.estadoevaluacionficha.model.DisponibilidadEstadoEvaluacion;
import com.arquisoft.fichas.domain.estadoevaluacionficha.model.ExistenciaEvaluacionFicha;
import com.arquisoft.fichas.domain.estadoevaluacionficha.model.PropiedadEvaluacionFicha;
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
        var entrada = AgregacionEstadoEvaluacionFichaDomain.crear(
                evaluacion, EstadoEvaluacion.APROBADA.getId(), representante);

        // Act
        validator.validar(entrada, true, true, false);

        // Assert
        InOrder inOrder = inOrder(evaluacionFichaExisteRule, representantePropietarioEvaluacionRule,
                estadoEvaluacionNoDuplicadoRule);
        inOrder.verify(evaluacionFichaExisteRule)
                .validar(new ExistenciaEvaluacionFicha(evaluacion, true));
        inOrder.verify(representantePropietarioEvaluacionRule)
                .validar(new PropiedadEvaluacionFicha(evaluacion, representante, true));
        inOrder.verify(estadoEvaluacionNoDuplicadoRule)
                .validar(new DisponibilidadEstadoEvaluacion(evaluacion, EstadoEvaluacion.APROBADA, false));
    }

    @Test
    void debeTrasladarLosDatosConsultados_cuandoNoEsPropietarioYElEstadoYaExiste() {
        // Arrange
        UUID evaluacion = UUID.randomUUID();
        UUID representante = UUID.randomUUID();
        var entrada = AgregacionEstadoEvaluacionFichaDomain.crear(
                evaluacion, EstadoEvaluacion.APROBADA.getId(), representante);

        // Act
        validator.validar(entrada, true, false, true);

        // Assert
        InOrder inOrder = inOrder(representantePropietarioEvaluacionRule, estadoEvaluacionNoDuplicadoRule);
        inOrder.verify(representantePropietarioEvaluacionRule)
                .validar(new PropiedadEvaluacionFicha(evaluacion, representante, false));
        inOrder.verify(estadoEvaluacionNoDuplicadoRule)
                .validar(new DisponibilidadEstadoEvaluacion(evaluacion, EstadoEvaluacion.APROBADA, true));
    }
}
