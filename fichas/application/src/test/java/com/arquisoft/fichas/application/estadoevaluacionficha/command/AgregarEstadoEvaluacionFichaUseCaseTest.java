package com.arquisoft.fichas.application.estadoevaluacionficha.command;

import com.arquisoft.fichas.application.estadoevaluacionficha.command.model.AgregarEstadoEvaluacionFichaCommand;
import com.arquisoft.fichas.application.estadoevaluacionficha.exception.EstadoEvaluacionDuplicadoException;
import com.arquisoft.fichas.application.estadoevaluacionficha.exception.EstadoEvaluacionNoEncontradoException;
import com.arquisoft.fichas.application.estadoevaluacionficha.exception.EvaluacionFichaPerfilNoEncontradaException;
import com.arquisoft.fichas.application.evaluacionfichaperfil.query.port.out.EvaluacionFichaPerfilQueryOutputPort;
import com.arquisoft.fichas.domain.estadoevaluacionficha.aggregate.EstadoEvaluacionFichaAggregate;
import com.arquisoft.fichas.domain.estadoevaluacionficha.port.out.EstadoEvaluacionFichaOutputPort;
import com.arquisoft.shared.exception.DomainValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AgregarEstadoEvaluacionFichaUseCaseTest {

    @Mock
    private EstadoEvaluacionFichaOutputPort estadoEvaluacionFichaOutputPort;

    @Mock
    private EvaluacionFichaPerfilQueryOutputPort evaluacionFichaPerfilQueryOutputPort;

    @InjectMocks
    private AgregarEstadoEvaluacionFichaUseCase useCase;

    @Test
    void debeAgregar_cuandoDatosValidos() {
        // Arrange
        UUID evaluacionId = UUID.randomUUID();
        var command = new AgregarEstadoEvaluacionFichaCommand(evaluacionId, "APROBADA");

        when(evaluacionFichaPerfilQueryOutputPort.existsById(evaluacionId)).thenReturn(true);
        when(estadoEvaluacionFichaOutputPort.existsByEvaluacionAndEstado(evaluacionId, "APROBADA"))
                .thenReturn(false);
        when(estadoEvaluacionFichaOutputPort.obtenerUltimoEstado(evaluacionId))
                .thenReturn(Optional.of("EN_EVALUACION"));

        // Act
        UUID resultado = useCase.ejecutar(command);

        // Assert
        assertThat(resultado).isNotNull();
        verify(evaluacionFichaPerfilQueryOutputPort).existsById(evaluacionId);
        verify(estadoEvaluacionFichaOutputPort).existsByEvaluacionAndEstado(evaluacionId, "APROBADA");
        verify(estadoEvaluacionFichaOutputPort).obtenerUltimoEstado(evaluacionId);
        verify(estadoEvaluacionFichaOutputPort).guardar(any(EstadoEvaluacionFichaAggregate.class));
    }

    @Test
    void debeLanzarEvaluacionNoEncontrada_cuandoEvaluacionNoExiste() {
        // Arrange
        UUID evaluacionId = UUID.randomUUID();
        var command = new AgregarEstadoEvaluacionFichaCommand(evaluacionId, "APROBADA");

        when(evaluacionFichaPerfilQueryOutputPort.existsById(evaluacionId)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(command))
                .isInstanceOf(EvaluacionFichaPerfilNoEncontradaException.class);

        verify(evaluacionFichaPerfilQueryOutputPort).existsById(evaluacionId);
    }

    @Test
    void debeLanzarEstadoNoEncontrado_cuandoCatalogoNoExiste() {
        // Arrange
        UUID evaluacionId = UUID.randomUUID();
        var command = new AgregarEstadoEvaluacionFichaCommand(evaluacionId, "ESTADO_INVALIDO");

        when(evaluacionFichaPerfilQueryOutputPort.existsById(evaluacionId)).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(command))
                .isInstanceOf(EstadoEvaluacionNoEncontradoException.class);

        verify(evaluacionFichaPerfilQueryOutputPort).existsById(evaluacionId);
    }

    @Test
    void debeLanzarEstadoDuplicado_cuandoYaExiste() {
        // Arrange
        UUID evaluacionId = UUID.randomUUID();
        var command = new AgregarEstadoEvaluacionFichaCommand(evaluacionId, "APROBADA");

        when(evaluacionFichaPerfilQueryOutputPort.existsById(evaluacionId)).thenReturn(true);
        when(estadoEvaluacionFichaOutputPort.existsByEvaluacionAndEstado(evaluacionId, "APROBADA"))
                .thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(command))
                .isInstanceOf(EstadoEvaluacionDuplicadoException.class);

        verify(evaluacionFichaPerfilQueryOutputPort).existsById(evaluacionId);
        verify(estadoEvaluacionFichaOutputPort).existsByEvaluacionAndEstado(evaluacionId, "APROBADA");
    }

    @Test
    void debeLanzarDomainValidation_cuandoEstadoTerminal() {
        // Arrange
        UUID evaluacionId = UUID.randomUUID();
        var command = new AgregarEstadoEvaluacionFichaCommand(evaluacionId, "NO_APROBADA");

        when(evaluacionFichaPerfilQueryOutputPort.existsById(evaluacionId)).thenReturn(true);
        when(estadoEvaluacionFichaOutputPort.existsByEvaluacionAndEstado(evaluacionId, "NO_APROBADA"))
                .thenReturn(false);
        when(estadoEvaluacionFichaOutputPort.obtenerUltimoEstado(evaluacionId))
                .thenReturn(Optional.of("APROBADA"));

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(command))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("estado terminal");

        verify(evaluacionFichaPerfilQueryOutputPort).existsById(evaluacionId);
        verify(estadoEvaluacionFichaOutputPort).existsByEvaluacionAndEstado(evaluacionId, "NO_APROBADA");
        verify(estadoEvaluacionFichaOutputPort).obtenerUltimoEstado(evaluacionId);
    }

    @Test
    void debeLanzarDomainValidation_cuandoIntentaEnEvaluacionManual() {
        // Arrange
        UUID evaluacionId = UUID.randomUUID();
        var command = new AgregarEstadoEvaluacionFichaCommand(evaluacionId, "EN_EVALUACION");

        when(evaluacionFichaPerfilQueryOutputPort.existsById(evaluacionId)).thenReturn(true);
        when(estadoEvaluacionFichaOutputPort.existsByEvaluacionAndEstado(evaluacionId, "EN_EVALUACION"))
                .thenReturn(false);
        when(estadoEvaluacionFichaOutputPort.obtenerUltimoEstado(evaluacionId))
                .thenReturn(Optional.of("EN_EVALUACION"));

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(command))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("EN_EVALUACION se asigna al momento de registrar la evaluación");

        verify(evaluacionFichaPerfilQueryOutputPort).existsById(evaluacionId);
        verify(estadoEvaluacionFichaOutputPort).existsByEvaluacionAndEstado(evaluacionId, "EN_EVALUACION");
        verify(estadoEvaluacionFichaOutputPort).obtenerUltimoEstado(evaluacionId);
    }

    @Test
    void debePermitirSegundoEstado_cuandoYaExisteEnEvaluacion() {
        // Arrange
        UUID evaluacionId = UUID.randomUUID();
        var command = new AgregarEstadoEvaluacionFichaCommand(evaluacionId, "APROBADA_CON_OBSERVACIONES");

        when(evaluacionFichaPerfilQueryOutputPort.existsById(evaluacionId)).thenReturn(true);
        when(estadoEvaluacionFichaOutputPort.existsByEvaluacionAndEstado(
                evaluacionId, "APROBADA_CON_OBSERVACIONES"))
                .thenReturn(false);
        when(estadoEvaluacionFichaOutputPort.obtenerUltimoEstado(evaluacionId))
                .thenReturn(Optional.of("EN_EVALUACION"));

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
        var command = new AgregarEstadoEvaluacionFichaCommand(evaluacionId, "APROBADA");

        when(evaluacionFichaPerfilQueryOutputPort.existsById(evaluacionId)).thenReturn(true);
        when(estadoEvaluacionFichaOutputPort.existsByEvaluacionAndEstado(evaluacionId, "APROBADA"))
                .thenReturn(false);
        when(estadoEvaluacionFichaOutputPort.obtenerUltimoEstado(evaluacionId))
                .thenReturn(Optional.of("EN_EVALUACION"));
        doThrow(new DataAccessException("Error de BD") {})
                .when(estadoEvaluacionFichaOutputPort).guardar(any(EstadoEvaluacionFichaAggregate.class));

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(command))
                .isInstanceOf(DataAccessException.class)
                .hasMessageContaining("Error de BD");

        verify(estadoEvaluacionFichaOutputPort).guardar(any(EstadoEvaluacionFichaAggregate.class));
    }
}
