package com.arquisoft.fichas.application.estadoevaluacionficha.command.validator;

import com.arquisoft.fichas.application.estadoevaluacionficha.command.validator.impl.AgregarEstadoEvaluacionFichaValidatorImpl;
import com.arquisoft.fichas.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.fichas.domain.estadoevaluacionficha.AgregacionEstadoEvaluacionFichaDomain;
import com.arquisoft.fichas.domain.estadoevaluacionficha.EstadoEvaluacionFichaDomain;
import com.arquisoft.fichas.domain.estadoevaluacionficha.model.DisponibilidadEstadoEvaluacion;
import com.arquisoft.fichas.domain.estadoevaluacionficha.model.ExistenciaEvaluacionFicha;
import com.arquisoft.fichas.domain.estadoevaluacionficha.model.PropiedadEvaluacionFicha;
import com.arquisoft.fichas.domain.estadoevaluacionficha.model.SolicitudEstadoEvaluacion;
import com.arquisoft.fichas.domain.estadoevaluacionficha.model.UltimoEstadoEvaluacion;
import com.arquisoft.fichas.domain.estadoevaluacionficha.rules.EstadoEnEvaluacionNoManualRule;
import com.arquisoft.fichas.domain.estadoevaluacionficha.rules.EstadoEvaluacionEnTerminalRule;
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

    @Mock
    private EstadoEnEvaluacionNoManualRule estadoEnEvaluacionNoManualRule;

    @Mock
    private EstadoEvaluacionEnTerminalRule estadoEvaluacionEnTerminalRule;

    @InjectMocks
    private AgregarEstadoEvaluacionFichaValidatorImpl validator;

    @Test
    void debeAplicarLasReglasEnOrden_cuandoValida() {
        // Arrange
        UUID evaluacion = UUID.randomUUID();
        UUID representante = UUID.randomUUID();
        var entrada = AgregacionEstadoEvaluacionFichaDomain.crear(
                EstadoEvaluacionFichaDomain.crearConEstado(evaluacion, EstadoEvaluacion.APROBADA.getId()),
                representante);

        // Act
        validator.validar(entrada, true, true, false, EstadoEvaluacionFichaDomain.VACIO);

        // Assert
        InOrder inOrder = inOrder(evaluacionFichaExisteRule, representantePropietarioEvaluacionRule,
                estadoEvaluacionNoDuplicadoRule, estadoEnEvaluacionNoManualRule, estadoEvaluacionEnTerminalRule);
        inOrder.verify(evaluacionFichaExisteRule)
                .validar(new ExistenciaEvaluacionFicha(evaluacion, true));
        inOrder.verify(representantePropietarioEvaluacionRule)
                .validar(new PropiedadEvaluacionFicha(evaluacion, representante, true));
        inOrder.verify(estadoEvaluacionNoDuplicadoRule)
                .validar(new DisponibilidadEstadoEvaluacion(evaluacion, EstadoEvaluacion.APROBADA, false));
        inOrder.verify(estadoEnEvaluacionNoManualRule)
                .validar(new SolicitudEstadoEvaluacion(evaluacion, EstadoEvaluacion.APROBADA));
        inOrder.verify(estadoEvaluacionEnTerminalRule)
                .validar(new UltimoEstadoEvaluacion(evaluacion, EstadoEvaluacion.VACIO));
    }

    @Test
    void debeTrasladarLosDatosConsultados_cuandoNoEsPropietarioYElEstadoYaExiste() {
        // Arrange
        UUID evaluacion = UUID.randomUUID();
        UUID representante = UUID.randomUUID();
        var entrada = AgregacionEstadoEvaluacionFichaDomain.crear(
                EstadoEvaluacionFichaDomain.crearConEstado(evaluacion, EstadoEvaluacion.APROBADA.getId()),
                representante);

        // Act
        validator.validar(entrada, true, false, true, EstadoEvaluacionFichaDomain.VACIO);

        // Assert
        InOrder inOrder = inOrder(representantePropietarioEvaluacionRule, estadoEvaluacionNoDuplicadoRule);
        inOrder.verify(representantePropietarioEvaluacionRule)
                .validar(new PropiedadEvaluacionFicha(evaluacion, representante, false));
        inOrder.verify(estadoEvaluacionNoDuplicadoRule)
                .validar(new DisponibilidadEstadoEvaluacion(evaluacion, EstadoEvaluacion.APROBADA, true));
    }
}
