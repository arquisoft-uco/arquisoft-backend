package com.arquisoft.fichas.application.estadoevaluacionficha.command.usecase.impl;

import com.arquisoft.shared.message.MessageCatalog;
import com.arquisoft.shared.message.ResourceBundleMessageCatalog;
import com.arquisoft.fichas.application.estadoevaluacionficha.command.model.AgregarEstadoEvaluacionFichaCommand;
import com.arquisoft.fichas.application.estadoevaluacionficha.command.validator.AgregarEstadoEvaluacionFichaValidator;
import com.arquisoft.fichas.domain.estadoevaluacionficha.exception.EstadoEvaluacionDuplicadoException;
import com.arquisoft.fichas.application.estadoevaluacionficha.exception.EstadoEvaluacionNoEncontradoException;
import com.arquisoft.fichas.domain.estadoevaluacionficha.exception.EvaluacionFichaNoPropiaException;
import com.arquisoft.fichas.domain.estadoevaluacionficha.exception.EvaluacionFichaPerfilNoEncontradaException;
import com.arquisoft.fichas.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.fichas.domain.estadoevaluacionficha.aggregate.EstadoEvaluacionFichaAggregate;
import com.arquisoft.fichas.domain.estadoevaluacionficha.port.out.EstadoEvaluacionFichaOutputPort;
import com.arquisoft.shared.exception.DomainValidationException;
import com.arquisoft.shared.logger.AppLogger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AgregarEstadoEvaluacionFichaUseCaseTest {

    @Mock
    private EstadoEvaluacionFichaOutputPort estadoEvaluacionFichaOutputPort;

    @Mock
    private AgregarEstadoEvaluacionFichaValidator agregarEstadoEvaluacionFichaValidator;

    @Mock
    private AppLogger logger;

        // Catalogo real, no mock: varios mensajes acaban en la excepcion o en el
    // resultado, y un mock los dejaria en null.
    @Spy
    private MessageCatalog catalog = ResourceBundleMessageCatalog.porDefecto();

@InjectMocks
    private AgregarEstadoEvaluacionFichaUseCaseImpl useCase;

    @Test
    void debeAgregar_cuandoDatosValidos() {
        // Arrange
        UUID evaluacionId = UUID.randomUUID();
        UUID representanteId = UUID.randomUUID();
        var command = new AgregarEstadoEvaluacionFichaCommand(evaluacionId, "APROBADA", representanteId);

        when(agregarEstadoEvaluacionFichaValidator.resolverEstado("APROBADA"))
                .thenReturn(EstadoEvaluacion.APROBADA);
        when(estadoEvaluacionFichaOutputPort.obtenerUltimoEstado(evaluacionId))
                .thenReturn(Optional.of("EN_EVALUACION"));
        when(agregarEstadoEvaluacionFichaValidator.resolverEstado("EN_EVALUACION"))
                .thenReturn(EstadoEvaluacion.EN_EVALUACION);

        // Act
        UUID resultado = useCase.ejecutar(command);

        // Assert
        assertThat(resultado).isNotNull();
        verify(agregarEstadoEvaluacionFichaValidator).validar(any(), any(), any());
        verify(agregarEstadoEvaluacionFichaValidator).validar(any(), any(), any());
        verify(agregarEstadoEvaluacionFichaValidator).validar(any(), any(), any());
        verify(estadoEvaluacionFichaOutputPort).obtenerUltimoEstado(evaluacionId);
        verify(estadoEvaluacionFichaOutputPort).guardar(any(EstadoEvaluacionFichaAggregate.class));
    }

    @Test
    void debeLanzarEvaluacionNoEncontrada_cuandoEvaluacionNoExiste() {
        // Arrange
        UUID evaluacionId = UUID.randomUUID();
        UUID representanteId = UUID.randomUUID();
        var command = new AgregarEstadoEvaluacionFichaCommand(evaluacionId, "APROBADA", representanteId);

        when(agregarEstadoEvaluacionFichaValidator.resolverEstado("APROBADA"))
                .thenReturn(EstadoEvaluacion.APROBADA);
        doThrow(new EvaluacionFichaPerfilNoEncontradaException(evaluacionId))
                .when(agregarEstadoEvaluacionFichaValidator).validar(any(), any(), any());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(command))
                .isInstanceOf(EvaluacionFichaPerfilNoEncontradaException.class);

        verify(estadoEvaluacionFichaOutputPort, never()).guardar(any());
    }

    @Test
    void debeLanzarEvaluacionNoPropia_cuandoRepresentanteNoEsPropietario() {
        // Arrange
        UUID evaluacionId = UUID.randomUUID();
        UUID representanteId = UUID.randomUUID();
        var command = new AgregarEstadoEvaluacionFichaCommand(evaluacionId, "APROBADA", representanteId);

        when(agregarEstadoEvaluacionFichaValidator.resolverEstado("APROBADA"))
                .thenReturn(EstadoEvaluacion.APROBADA);
        doThrow(new EvaluacionFichaNoPropiaException(evaluacionId))
                .when(agregarEstadoEvaluacionFichaValidator).validar(any(), any(), any());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(command))
                .isInstanceOf(EvaluacionFichaNoPropiaException.class);

        verify(estadoEvaluacionFichaOutputPort, never()).guardar(any());
    }

    @Test
    void debeLanzarEstadoNoEncontrado_cuandoCatalogoNoExiste() {
        // Arrange
        UUID evaluacionId = UUID.randomUUID();
        UUID representanteId = UUID.randomUUID();
        var command = new AgregarEstadoEvaluacionFichaCommand(
                evaluacionId, "ESTADO_INVALIDO", representanteId);

        when(agregarEstadoEvaluacionFichaValidator.resolverEstado("ESTADO_INVALIDO"))
                .thenThrow(new EstadoEvaluacionNoEncontradoException("ESTADO_INVALIDO"));

        assertThatThrownBy(() -> useCase.ejecutar(command))
                .isInstanceOf(EstadoEvaluacionNoEncontradoException.class);

        verify(estadoEvaluacionFichaOutputPort, never()).guardar(any());
    }

    @Test
    void debeLanzarEstadoDuplicado_cuandoYaExiste() {
        // Arrange
        UUID evaluacionId = UUID.randomUUID();
        UUID representanteId = UUID.randomUUID();
        var command = new AgregarEstadoEvaluacionFichaCommand(evaluacionId, "APROBADA", representanteId);

        when(agregarEstadoEvaluacionFichaValidator.resolverEstado("APROBADA"))
                .thenReturn(EstadoEvaluacion.APROBADA);
        doThrow(new EstadoEvaluacionDuplicadoException(evaluacionId, "APROBADA"))
                .when(agregarEstadoEvaluacionFichaValidator).validar(any(), any(), any());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(command))
                .isInstanceOf(EstadoEvaluacionDuplicadoException.class);

        verify(estadoEvaluacionFichaOutputPort, never()).guardar(any());
    }

    @Test
    void debeLanzarDomainValidation_cuandoEstadoTerminal() {
        // Arrange
        UUID evaluacionId = UUID.randomUUID();
        UUID representanteId = UUID.randomUUID();
        var command = new AgregarEstadoEvaluacionFichaCommand(evaluacionId, "NO_APROBADA", representanteId);

        when(agregarEstadoEvaluacionFichaValidator.resolverEstado("NO_APROBADA"))
                .thenReturn(EstadoEvaluacion.NO_APROBADA);
        when(estadoEvaluacionFichaOutputPort.obtenerUltimoEstado(evaluacionId))
                .thenReturn(Optional.of("APROBADA"));
        when(agregarEstadoEvaluacionFichaValidator.resolverEstado("APROBADA"))
                .thenReturn(EstadoEvaluacion.APROBADA);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(command))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("estado terminal");

        verify(estadoEvaluacionFichaOutputPort, never()).guardar(any());
    }

    @Test
    void debeLanzarDomainValidation_cuandoIntentaEnEvaluacionManual() {
        // Arrange
        UUID evaluacionId = UUID.randomUUID();
        UUID representanteId = UUID.randomUUID();
        var command = new AgregarEstadoEvaluacionFichaCommand(evaluacionId, "EN_EVALUACION", representanteId);

        when(agregarEstadoEvaluacionFichaValidator.resolverEstado("EN_EVALUACION"))
                .thenReturn(EstadoEvaluacion.EN_EVALUACION);
        when(estadoEvaluacionFichaOutputPort.obtenerUltimoEstado(evaluacionId))
                .thenReturn(Optional.of("EN_EVALUACION"));

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(command))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("EN_EVALUACION se asigna al momento de registrar la evaluación");

        verify(estadoEvaluacionFichaOutputPort, never()).guardar(any());
    }

    @Test
    void debePermitirSegundoEstado_cuandoYaExisteEnEvaluacion() {
        // Arrange
        UUID evaluacionId = UUID.randomUUID();
        UUID representanteId = UUID.randomUUID();
        var command = new AgregarEstadoEvaluacionFichaCommand(
                evaluacionId, "APROBADA_CON_OBSERVACIONES", representanteId);

        when(agregarEstadoEvaluacionFichaValidator.resolverEstado("APROBADA_CON_OBSERVACIONES"))
                .thenReturn(EstadoEvaluacion.APROBADA_CON_OBSERVACIONES);
        when(estadoEvaluacionFichaOutputPort.obtenerUltimoEstado(evaluacionId))
                .thenReturn(Optional.of("EN_EVALUACION"));
        when(agregarEstadoEvaluacionFichaValidator.resolverEstado("EN_EVALUACION"))
                .thenReturn(EstadoEvaluacion.EN_EVALUACION);

        // Act
        UUID resultado = useCase.ejecutar(command);

        // Assert
        assertThat(resultado).isNotNull();
        verify(estadoEvaluacionFichaOutputPort).guardar(any(EstadoEvaluacionFichaAggregate.class));
    }

    @Test
    void debeLanzarExcepcion_cuandoRepositorioFalla() {
        // Arrange
        UUID evaluacionId = UUID.randomUUID();
        UUID representanteId = UUID.randomUUID();
        var command = new AgregarEstadoEvaluacionFichaCommand(evaluacionId, "APROBADA", representanteId);

        when(agregarEstadoEvaluacionFichaValidator.resolverEstado("APROBADA"))
                .thenReturn(EstadoEvaluacion.APROBADA);
        when(estadoEvaluacionFichaOutputPort.obtenerUltimoEstado(evaluacionId))
                .thenReturn(Optional.of("EN_EVALUACION"));
        when(agregarEstadoEvaluacionFichaValidator.resolverEstado("EN_EVALUACION"))
                .thenReturn(EstadoEvaluacion.EN_EVALUACION);
        doThrow(new DataAccessException("Error de BD") {})
                .when(estadoEvaluacionFichaOutputPort).guardar(any(EstadoEvaluacionFichaAggregate.class));

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(command))
                .isInstanceOf(DataAccessException.class)
                .hasMessageContaining("Error de BD");

        verify(estadoEvaluacionFichaOutputPort).guardar(any(EstadoEvaluacionFichaAggregate.class));
    }
}
