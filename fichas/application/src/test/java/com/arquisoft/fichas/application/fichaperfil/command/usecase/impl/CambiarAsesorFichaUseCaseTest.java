package com.arquisoft.fichas.application.fichaperfil.command.usecase.impl;

import com.arquisoft.shared.message.MessageCatalog;
import com.arquisoft.shared.message.ResourceBundleMessageCatalog;
import com.arquisoft.shared.message.FichasCodes;
import com.arquisoft.fichas.application.estadofichaperfil.query.port.out.EstadoFichaPerfilQueryOutputPort;
import com.arquisoft.fichas.application.fichaperfil.command.model.CambiarAsesorFichaCommand;
import com.arquisoft.fichas.application.fichaperfil.command.validator.CambiarAsesorFichaValidator;
import com.arquisoft.fichas.domain.fichaperfil.exception.AsesorFichaNoEncontradoException;
import com.arquisoft.fichas.domain.fichaperfil.exception.FichaPerfilNoEncontradaException;
import com.arquisoft.fichas.domain.estadoficha.EstadoFicha;
import com.arquisoft.fichas.domain.fichaperfil.aggregate.FichaPerfilAggregate;
import com.arquisoft.fichas.domain.fichaperfil.port.out.FichaPerfilOutputPort;
import com.arquisoft.shared.exception.DomainValidationException;
import com.arquisoft.shared.logger.AppLogger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

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
class CambiarAsesorFichaUseCaseTest {

    @Mock
    private FichaPerfilOutputPort fichaPerfilOutputPort;

    @Mock
    private EstadoFichaPerfilQueryOutputPort estadoFichaPerfilQueryOutputPort;

    @Mock
    private CambiarAsesorFichaValidator cambiarAsesorFichaValidator;

    @Mock
    private AppLogger logger;

        // Catalogo real, no mock: varios mensajes acaban en la excepcion o en el
    // resultado, y un mock los dejaria en null.
    @Spy
    private MessageCatalog catalog = ResourceBundleMessageCatalog.porDefecto();

@InjectMocks
    private CambiarAsesorFichaUseCaseImpl cambiarAsesorFichaUseCase;

    @Test
    void debeCambiarAsesor_cuandoDatosValidos() {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        UUID nuevoAsesorId = UUID.randomUUID();
        FichaPerfilAggregate ficha = fichaReconstruida(fichaId, UUID.randomUUID());
        var command = new CambiarAsesorFichaCommand(fichaId, nuevoAsesorId);

        when(fichaPerfilOutputPort.buscarPorId(fichaId)).thenReturn(Optional.of(ficha));
        when(estadoFichaPerfilQueryOutputPort.obtenerEstadoActual(fichaId))
                .thenReturn(Optional.of(EstadoFicha.EN_CONSTRUCCION));

        // Act
        cambiarAsesorFichaUseCase.ejecutar(command);

        // Assert
        ArgumentCaptor<FichaPerfilAggregate> fichaCaptor = ArgumentCaptor.forClass(FichaPerfilAggregate.class);
        verify(fichaPerfilOutputPort).guardar(fichaCaptor.capture());
        assertThat(fichaCaptor.getValue().getAsesorFichaId()).isEqualTo(nuevoAsesorId);
    }

    @Test
    void debeLanzarFichaPerfilNoEncontradaException_cuandoFichaNoExiste() {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        var command = new CambiarAsesorFichaCommand(fichaId, UUID.randomUUID());

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
        UUID nuevoAsesorId = UUID.randomUUID();
        FichaPerfilAggregate ficha = fichaReconstruida(fichaId, UUID.randomUUID());
        var command = new CambiarAsesorFichaCommand(fichaId, nuevoAsesorId);

        when(fichaPerfilOutputPort.buscarPorId(fichaId)).thenReturn(Optional.of(ficha));
        doThrow(new AsesorFichaNoEncontradoException(nuevoAsesorId))
                .when(cambiarAsesorFichaValidator).validar(any());

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
        FichaPerfilAggregate ficha = fichaReconstruida(fichaId, asesorActualId);
        var command = new CambiarAsesorFichaCommand(fichaId, asesorActualId);

        when(fichaPerfilOutputPort.buscarPorId(fichaId)).thenReturn(Optional.of(ficha));
        when(estadoFichaPerfilQueryOutputPort.obtenerEstadoActual(fichaId))
                .thenReturn(Optional.of(EstadoFicha.EN_CONSTRUCCION));

        // Act & Assert
        assertThatThrownBy(() -> cambiarAsesorFichaUseCase.ejecutar(command))
                .isInstanceOf(DomainValidationException.class)
                .extracting(ex -> ((DomainValidationException) ex)
                        .getValidationResult().getErrores().get(0).codigoError())
                .isEqualTo(FichasCodes.FichaPerfil.MISMO_ASESOR);

        verify(fichaPerfilOutputPort, never()).guardar(any());
    }

    @Test
    void debePropagarDomainValidationException_cuandoEstadoEsTerminal() {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        FichaPerfilAggregate ficha = fichaReconstruida(fichaId, UUID.randomUUID());
        var command = new CambiarAsesorFichaCommand(fichaId, UUID.randomUUID());

        when(fichaPerfilOutputPort.buscarPorId(fichaId)).thenReturn(Optional.of(ficha));
        when(estadoFichaPerfilQueryOutputPort.obtenerEstadoActual(fichaId))
                .thenReturn(Optional.of(EstadoFicha.APROBADA));

        // Act & Assert
        assertThatThrownBy(() -> cambiarAsesorFichaUseCase.ejecutar(command))
                .isInstanceOf(DomainValidationException.class)
                .extracting(ex -> ((DomainValidationException) ex)
                        .getValidationResult().getErrores().get(0).codigoError())
                .isEqualTo(FichasCodes.FichaPerfil.ESTADO_TERMINAL);

        verify(fichaPerfilOutputPort, never()).guardar(any());
    }

    private FichaPerfilAggregate fichaReconstruida(UUID fichaId, UUID asesorId) {
        return FichaPerfilAggregate.reconstruir(fichaId, "Título de prueba", asesorId);
    }
}
