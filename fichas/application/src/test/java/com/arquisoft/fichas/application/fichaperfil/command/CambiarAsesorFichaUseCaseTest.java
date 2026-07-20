package com.arquisoft.fichas.application.fichaperfil.command;

import com.arquisoft.fichas.application.asesorficha.query.port.out.AsesorFichaQueryOutputPort;
import com.arquisoft.fichas.application.estadofichaperfil.query.port.out.EstadoFichaPerfilQueryOutputPort;
import com.arquisoft.fichas.application.fichaperfil.command.model.CambiarAsesorFichaCommand;
import com.arquisoft.fichas.application.fichaperfil.exception.AsesorFichaNoEncontradoException;
import com.arquisoft.fichas.application.fichaperfil.exception.FichaPerfilNoEncontradaException;
import com.arquisoft.fichas.domain.estadoficha.EstadoFicha;
import com.arquisoft.fichas.domain.fichaperfil.aggregate.FichaPerfilAggregate;
import com.arquisoft.fichas.domain.fichaperfil.port.out.FichaPerfilOutputPort;
import com.arquisoft.shared.exception.DomainValidationException;
import com.arquisoft.shared.message.FichasMessages;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CambiarAsesorFichaUseCaseTest {

    @Mock
    private FichaPerfilOutputPort fichaPerfilOutputPort;

    @Mock
    private AsesorFichaQueryOutputPort asesorFichaQueryOutputPort;

    @Mock
    private EstadoFichaPerfilQueryOutputPort estadoFichaPerfilQueryOutputPort;

    @InjectMocks
    private CambiarAsesorFichaUseCase cambiarAsesorFichaUseCase;

    @Test
    void debeCambiarAsesor_cuandoDatosValidos() {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        UUID asesorActualId = UUID.randomUUID();
        UUID nuevoAsesorId = UUID.randomUUID();

        FichaPerfilAggregate ficha = FichaPerfilAggregate.reconstruir(
                fichaId,
                "Título de prueba",
                asesorActualId
        );

        CambiarAsesorFichaCommand command = new CambiarAsesorFichaCommand(fichaId, nuevoAsesorId);

        when(fichaPerfilOutputPort.buscarPorId(fichaId)).thenReturn(Optional.of(ficha));
        when(asesorFichaQueryOutputPort.existsById(nuevoAsesorId)).thenReturn(true);
        when(estadoFichaPerfilQueryOutputPort.obtenerEstadoActual(fichaId))
                .thenReturn(Optional.of(EstadoFicha.EN_CONSTRUCCION));

        // Act
        cambiarAsesorFichaUseCase.ejecutar(command);

        // Assert
        ArgumentCaptor<FichaPerfilAggregate> fichaCaptor = ArgumentCaptor.forClass(FichaPerfilAggregate.class);
        verify(fichaPerfilOutputPort).guardar(fichaCaptor.capture());

        FichaPerfilAggregate fichaGuardada = fichaCaptor.getValue();
        assertThat(fichaGuardada.getAsesorFichaId()).isEqualTo(nuevoAsesorId);
    }

    @Test
    void debeLanzarFichaPerfilNoEncontradaException_cuandoFichaNoExiste() {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        UUID nuevoAsesorId = UUID.randomUUID();
        CambiarAsesorFichaCommand command = new CambiarAsesorFichaCommand(fichaId, nuevoAsesorId);

        when(fichaPerfilOutputPort.buscarPorId(fichaId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> cambiarAsesorFichaUseCase.ejecutar(command))
                .isInstanceOf(FichaPerfilNoEncontradaException.class)
                .hasMessageContaining(fichaId.toString());

        verify(fichaPerfilOutputPort, never()).guardar(any());
    }

    @Test
    void debeLanzarAsesorFichaNoEncontradoException_cuandoAsesorNoExiste() {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        UUID asesorActualId = UUID.randomUUID();
        UUID nuevoAsesorId = UUID.randomUUID();

        FichaPerfilAggregate ficha = FichaPerfilAggregate.reconstruir(
                fichaId,
                "Título de prueba",
                asesorActualId
        );

        CambiarAsesorFichaCommand command = new CambiarAsesorFichaCommand(fichaId, nuevoAsesorId);

        when(fichaPerfilOutputPort.buscarPorId(fichaId)).thenReturn(Optional.of(ficha));
        when(asesorFichaQueryOutputPort.existsById(nuevoAsesorId)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> cambiarAsesorFichaUseCase.ejecutar(command))
                .isInstanceOf(AsesorFichaNoEncontradoException.class)
                .hasMessageContaining(nuevoAsesorId.toString());

        verify(fichaPerfilOutputPort, never()).guardar(any());
    }

    @Test
    void debePropagarDomainValidationException_cuandoMismoAsesor() {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        UUID asesorActualId = UUID.randomUUID();

        FichaPerfilAggregate ficha = FichaPerfilAggregate.reconstruir(
                fichaId,
                "Título de prueba",
                asesorActualId
        );

        CambiarAsesorFichaCommand command = new CambiarAsesorFichaCommand(fichaId, asesorActualId);

        when(fichaPerfilOutputPort.buscarPorId(fichaId)).thenReturn(Optional.of(ficha));
        when(asesorFichaQueryOutputPort.existsById(asesorActualId)).thenReturn(true);
        when(estadoFichaPerfilQueryOutputPort.obtenerEstadoActual(fichaId))
                .thenReturn(Optional.of(EstadoFicha.EN_REVISION));

        // Act & Assert
        assertThatThrownBy(() -> cambiarAsesorFichaUseCase.ejecutar(command))
                .isInstanceOf(DomainValidationException.class)
                .extracting(ex -> ((DomainValidationException) ex).getValidationResult().getErrors().get(0).errorCode())
                .isEqualTo(FichasMessages.FichaPerfil.MISMO_ASESOR);

        verify(fichaPerfilOutputPort, never()).guardar(any());
    }

    @Test
    void debePropagarDomainValidationException_cuandoEstadoEsTerminal() {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        UUID asesorActualId = UUID.randomUUID();
        UUID nuevoAsesorId = UUID.randomUUID();

        FichaPerfilAggregate ficha = FichaPerfilAggregate.reconstruir(
                fichaId,
                "Título de prueba",
                asesorActualId
        );

        CambiarAsesorFichaCommand command = new CambiarAsesorFichaCommand(fichaId, nuevoAsesorId);

        when(fichaPerfilOutputPort.buscarPorId(fichaId)).thenReturn(Optional.of(ficha));
        when(asesorFichaQueryOutputPort.existsById(nuevoAsesorId)).thenReturn(true);
        when(estadoFichaPerfilQueryOutputPort.obtenerEstadoActual(fichaId))
                .thenReturn(Optional.of(EstadoFicha.APROBADA));

        // Act & Assert
        assertThatThrownBy(() -> cambiarAsesorFichaUseCase.ejecutar(command))
                .isInstanceOf(DomainValidationException.class)
                .extracting(ex -> ((DomainValidationException) ex).getValidationResult().getErrors().get(0).errorCode())
                .isEqualTo(FichasMessages.FichaPerfil.ESTADO_TERMINAL);

        verify(fichaPerfilOutputPort, never()).guardar(any());
    }
}
