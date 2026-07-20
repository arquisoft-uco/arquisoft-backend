package com.arquisoft.fichas.application.evaluacionfichaperfil.command;

import com.arquisoft.fichas.application.evaluacionfichaperfil.command.model.RegistrarEvaluacionFichaPerfilCommand;
import com.arquisoft.fichas.application.evaluacionfichaperfil.exception.EvaluacionFichaPerfilDuplicadaException;
import com.arquisoft.fichas.application.evaluacionfichaperfil.exception.RepresentanteComiteNoEncontradoException;
import com.arquisoft.fichas.application.fichaperfil.exception.FichaPerfilNoEncontradaException;
import com.arquisoft.fichas.application.representantecomite.query.port.out.RepresentanteComiteQueryOutputPort;
import com.arquisoft.fichas.domain.estadoevaluacionficha.aggregate.EstadoEvaluacionFichaAggregate;
import com.arquisoft.fichas.domain.estadoevaluacionficha.port.out.EstadoEvaluacionFichaOutputPort;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.aggregate.EvaluacionFichaPerfilAggregate;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.port.out.EvaluacionFichaPerfilOutputPort;
import com.arquisoft.fichas.application.fichaperfil.query.port.out.FichaPerfilQueryOutputPort;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrarEvaluacionFichaPerfilUseCaseTest {

    @Mock
    private FichaPerfilQueryOutputPort fichaPerfilQueryOutputPort;

    @Mock
    private RepresentanteComiteQueryOutputPort representanteComiteQueryOutputPort;

    @Mock
    private EvaluacionFichaPerfilOutputPort evaluacionFichaPerfilOutputPort;

    @Mock
    private EstadoEvaluacionFichaOutputPort estadoEvaluacionFichaOutputPort;

    @InjectMocks
    private RegistrarEvaluacionFichaPerfilUseCase useCase;

    @Test
    void debeRegistrar_cuandoDatosValidos() {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        UUID representanteId = UUID.randomUUID();
        var command = new RegistrarEvaluacionFichaPerfilCommand(fichaId, representanteId);

        when(fichaPerfilQueryOutputPort.existsById(fichaId)).thenReturn(true);
        when(representanteComiteQueryOutputPort.existsById(representanteId)).thenReturn(true);
        when(evaluacionFichaPerfilOutputPort.existsByRepresentanteAndFicha(representanteId, fichaId))
                .thenReturn(false);

        // Act
        UUID resultado = useCase.ejecutar(command);

        // Assert
        assertThat(resultado).isNotNull();
        verify(fichaPerfilQueryOutputPort).existsById(fichaId);
        verify(representanteComiteQueryOutputPort).existsById(representanteId);
        verify(evaluacionFichaPerfilOutputPort).existsByRepresentanteAndFicha(representanteId, fichaId);
        verify(evaluacionFichaPerfilOutputPort).guardar(any(EvaluacionFichaPerfilAggregate.class));
        verify(estadoEvaluacionFichaOutputPort).guardar(any(EstadoEvaluacionFichaAggregate.class));
    }

    @Test
    void debeLanzarExcepcion_cuandoFichaNoExiste() {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        UUID representanteId = UUID.randomUUID();
        var command = new RegistrarEvaluacionFichaPerfilCommand(fichaId, representanteId);

        when(fichaPerfilQueryOutputPort.existsById(fichaId)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(command))
                .isInstanceOf(FichaPerfilNoEncontradaException.class);

        verify(fichaPerfilQueryOutputPort).existsById(fichaId);
        verify(representanteComiteQueryOutputPort, never()).existsById(any());
        verify(evaluacionFichaPerfilOutputPort, never()).existsByRepresentanteAndFicha(any(), any());
        verify(evaluacionFichaPerfilOutputPort, never()).guardar(any());
    }

    @Test
    void debeLanzarExcepcion_cuandoRepresentanteNoExiste() {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        UUID representanteId = UUID.randomUUID();
        var command = new RegistrarEvaluacionFichaPerfilCommand(fichaId, representanteId);

        when(fichaPerfilQueryOutputPort.existsById(fichaId)).thenReturn(true);
        when(representanteComiteQueryOutputPort.existsById(representanteId)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(command))
                .isInstanceOf(RepresentanteComiteNoEncontradoException.class);

        verify(fichaPerfilQueryOutputPort).existsById(fichaId);
        verify(representanteComiteQueryOutputPort).existsById(representanteId);
        verify(evaluacionFichaPerfilOutputPort, never()).existsByRepresentanteAndFicha(any(), any());
        verify(evaluacionFichaPerfilOutputPort, never()).guardar(any());
    }

    @Test
    void debeLanzarExcepcion_cuandoEvaluacionDuplicada() {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        UUID representanteId = UUID.randomUUID();
        var command = new RegistrarEvaluacionFichaPerfilCommand(fichaId, representanteId);

        when(fichaPerfilQueryOutputPort.existsById(fichaId)).thenReturn(true);
        when(representanteComiteQueryOutputPort.existsById(representanteId)).thenReturn(true);
        when(evaluacionFichaPerfilOutputPort.existsByRepresentanteAndFicha(representanteId, fichaId))
                .thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(command))
                .isInstanceOf(EvaluacionFichaPerfilDuplicadaException.class);

        verify(fichaPerfilQueryOutputPort).existsById(fichaId);
        verify(representanteComiteQueryOutputPort).existsById(representanteId);
        verify(evaluacionFichaPerfilOutputPort).existsByRepresentanteAndFicha(representanteId, fichaId);
        verify(evaluacionFichaPerfilOutputPort, never()).guardar(any());
    }

    @Test
    void debeLanzarExcepcion_cuandoRepositorioFalla() {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        UUID representanteId = UUID.randomUUID();
        var command = new RegistrarEvaluacionFichaPerfilCommand(fichaId, representanteId);

        when(fichaPerfilQueryOutputPort.existsById(fichaId)).thenReturn(true);
        when(representanteComiteQueryOutputPort.existsById(representanteId)).thenReturn(true);
        when(evaluacionFichaPerfilOutputPort.existsByRepresentanteAndFicha(representanteId, fichaId))
                .thenReturn(false);
        doThrow(new DataAccessException("Error de BD") {})
                .when(evaluacionFichaPerfilOutputPort).guardar(any(EvaluacionFichaPerfilAggregate.class));

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(command))
                .isInstanceOf(DataAccessException.class)
                .hasMessageContaining("Error de BD");

        verify(evaluacionFichaPerfilOutputPort).guardar(any(EvaluacionFichaPerfilAggregate.class));
    }

    @Test
    void debeCrearEstadoInicialAutomatico_cuandoRegistrarEvaluacion() {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        UUID representanteId = UUID.randomUUID();
        var command = new RegistrarEvaluacionFichaPerfilCommand(fichaId, representanteId);

        when(fichaPerfilQueryOutputPort.existsById(fichaId)).thenReturn(true);
        when(representanteComiteQueryOutputPort.existsById(representanteId)).thenReturn(true);
        when(evaluacionFichaPerfilOutputPort.existsByRepresentanteAndFicha(representanteId, fichaId))
                .thenReturn(false);

        // Act
        useCase.ejecutar(command);

        // Assert
        verify(estadoEvaluacionFichaOutputPort).guardar(any(EstadoEvaluacionFichaAggregate.class));
    }
}
