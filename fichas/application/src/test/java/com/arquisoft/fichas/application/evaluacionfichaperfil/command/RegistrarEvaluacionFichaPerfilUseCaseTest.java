package com.arquisoft.fichas.application.evaluacionfichaperfil.command;

import com.arquisoft.fichas.application.evaluacionfichaperfil.command.model.RegistrarEvaluacionFichaPerfilCommand;
import com.arquisoft.fichas.application.evaluacionfichaperfil.command.validator.RegistrarEvaluacionFichaPerfilValidator;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.exception.EvaluacionFichaPerfilDuplicadaException;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.exception.RepresentanteComiteNoEncontradoException;
import com.arquisoft.fichas.domain.fichaperfil.exception.FichaPerfilNoEncontradaException;
import com.arquisoft.fichas.domain.estadoevaluacionficha.aggregate.EstadoEvaluacionFichaAggregate;
import com.arquisoft.fichas.domain.estadoevaluacionficha.port.out.EstadoEvaluacionFichaOutputPort;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.aggregate.EvaluacionFichaPerfilAggregate;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.port.out.EvaluacionFichaPerfilOutputPort;
import com.arquisoft.shared.exception.DomainValidationException;
import com.arquisoft.shared.logger.AppLogger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RegistrarEvaluacionFichaPerfilUseCaseTest {

    @Mock
    private EvaluacionFichaPerfilOutputPort evaluacionFichaPerfilOutputPort;

    @Mock
    private EstadoEvaluacionFichaOutputPort estadoEvaluacionFichaOutputPort;

    @Mock
    private RegistrarEvaluacionFichaPerfilValidator registrarEvaluacionFichaPerfilValidator;


    @Mock
    private AppLogger logger;

    @InjectMocks
    private RegistrarEvaluacionFichaPerfilUseCaseImpl useCase;

    @Test
    void debeRegistrar_cuandoDatosValidos() {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        UUID representanteId = UUID.randomUUID();
        var command = new RegistrarEvaluacionFichaPerfilCommand(fichaId, representanteId);

        // Act
        UUID resultado = useCase.ejecutar(command);

        // Assert
        assertThat(resultado).isNotNull();
        verify(registrarEvaluacionFichaPerfilValidator).validar(any());
        verify(registrarEvaluacionFichaPerfilValidator).validar(any());
        verify(registrarEvaluacionFichaPerfilValidator).validar(any());
        verify(evaluacionFichaPerfilOutputPort).guardar(any(EvaluacionFichaPerfilAggregate.class));
        verify(estadoEvaluacionFichaOutputPort).guardar(any(EstadoEvaluacionFichaAggregate.class));
    }

    @Test
    void debeLanzarExcepcion_cuandoFichaNoExiste() {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        UUID representanteId = UUID.randomUUID();
        var command = new RegistrarEvaluacionFichaPerfilCommand(fichaId, representanteId);

        doThrow(new FichaPerfilNoEncontradaException(fichaId))
                .when(registrarEvaluacionFichaPerfilValidator).validar(any());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(command))
                .isInstanceOf(FichaPerfilNoEncontradaException.class);

        verify(evaluacionFichaPerfilOutputPort, never()).guardar(any());
    }

    @Test
    void debeLanzarExcepcion_cuandoRepresentanteNoExiste() {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        UUID representanteId = UUID.randomUUID();
        var command = new RegistrarEvaluacionFichaPerfilCommand(fichaId, representanteId);

        doThrow(new RepresentanteComiteNoEncontradoException(representanteId))
                .when(registrarEvaluacionFichaPerfilValidator).validar(any());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(command))
                .isInstanceOf(RepresentanteComiteNoEncontradoException.class);

        verify(evaluacionFichaPerfilOutputPort, never()).guardar(any());
    }

    @Test
    void debeLanzarExcepcion_cuandoEvaluacionDuplicada() {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        UUID representanteId = UUID.randomUUID();
        var command = new RegistrarEvaluacionFichaPerfilCommand(fichaId, representanteId);

        doThrow(new EvaluacionFichaPerfilDuplicadaException(representanteId, fichaId))
                .when(registrarEvaluacionFichaPerfilValidator)
                .validar(any());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(command))
                .isInstanceOf(EvaluacionFichaPerfilDuplicadaException.class);

        verify(evaluacionFichaPerfilOutputPort, never()).guardar(any());
    }

    @Test
    void debeLanzarExcepcion_cuandoRepositorioFalla() {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        UUID representanteId = UUID.randomUUID();
        var command = new RegistrarEvaluacionFichaPerfilCommand(fichaId, representanteId);

        doThrow(new DataAccessException("Error de BD") {})
                .when(evaluacionFichaPerfilOutputPort).guardar(any(EvaluacionFichaPerfilAggregate.class));

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(command))
                .isInstanceOf(DataAccessException.class)
                .hasMessageContaining("Error de BD");

        verify(evaluacionFichaPerfilOutputPort).guardar(any(EvaluacionFichaPerfilAggregate.class));
    }

    @Test
    void debeConstruirAgregadoAntesDeConsultarLaBaseDeDatos_cuandoRepresentanteEsNull() {
        // Arrange
        var command = new RegistrarEvaluacionFichaPerfilCommand(null, UUID.randomUUID());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(command))
                .isInstanceOf(DomainValidationException.class);

        verify(evaluacionFichaPerfilOutputPort, never()).guardar(any());
    }

    @Test
    void debeCrearEstadoInicialAutomatico_cuandoRegistrarEvaluacion() {
        // Arrange
        var command = new RegistrarEvaluacionFichaPerfilCommand(UUID.randomUUID(), UUID.randomUUID());

        // Act
        useCase.ejecutar(command);

        // Assert
        verify(estadoEvaluacionFichaOutputPort).guardar(any(EstadoEvaluacionFichaAggregate.class));
    }
}
