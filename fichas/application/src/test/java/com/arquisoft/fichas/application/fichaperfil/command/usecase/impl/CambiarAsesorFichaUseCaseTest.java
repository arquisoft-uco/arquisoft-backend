package com.arquisoft.fichas.application.fichaperfil.command.usecase.impl;

import com.arquisoft.shared.message.MessageCatalog;
import com.arquisoft.shared.message.ResourceBundleMessageCatalog;
import com.arquisoft.fichas.application.asesorficha.query.port.out.AsesorFichaQueryOutputPort;
import com.arquisoft.fichas.application.asesorficha.query.readmodel.AsesorContactoReadModel;
import com.arquisoft.fichas.application.fichaperfil.command.model.CambiarAsesorFichaCommand;
import com.arquisoft.fichas.application.fichaperfil.command.validator.CambiarAsesorFichaValidator;
import com.arquisoft.fichas.domain.estadoficha.EstadoFicha;
import com.arquisoft.fichas.domain.estadofichaperfil.exception.EstadoFichaPerfilTerminalException;
import com.arquisoft.fichas.domain.fichaperfil.event.AsesorFichaCambiadoEvent;
import com.arquisoft.fichas.domain.fichaperfil.exception.AsesorFichaNoEncontradoException;
import com.arquisoft.fichas.domain.fichaperfil.exception.FichaPerfilNoEncontradaException;
import com.arquisoft.fichas.domain.fichaperfil.exception.MismoAsesorFichaException;
import com.arquisoft.fichas.domain.fichaperfil.aggregate.FichaPerfilAggregate;
import com.arquisoft.fichas.domain.fichaperfil.port.out.FichaPerfilOutputPort;
import com.arquisoft.shared.events.EventPublisher;
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
    private AsesorFichaQueryOutputPort asesorFichaQueryOutputPort;

    @Mock
    private CambiarAsesorFichaValidator cambiarAsesorFichaValidator;

    @Mock
    private EventPublisher eventPublisher;

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
        when(asesorFichaQueryOutputPort.buscarContactoPorId(nuevoAsesorId))
                .thenReturn(Optional.of(contactoDe(nuevoAsesorId)));

        // Act
        cambiarAsesorFichaUseCase.ejecutar(command);

        // Assert
        ArgumentCaptor<FichaPerfilAggregate> fichaCaptor = ArgumentCaptor.forClass(FichaPerfilAggregate.class);
        verify(fichaPerfilOutputPort).guardar(fichaCaptor.capture());
        assertThat(fichaCaptor.getValue().getAsesorFicha()).isEqualTo(nuevoAsesorId);
    }

    @Test
    void debeConservarElTitulo_cuandoGuardaLaFichaActualizada() {
        // Arrange — el comando no trae el título; debe salir de lo persistido, no perderse
        UUID fichaId = UUID.randomUUID();
        UUID nuevoAsesorId = UUID.randomUUID();
        FichaPerfilAggregate ficha = fichaReconstruida(fichaId, UUID.randomUUID());
        var command = new CambiarAsesorFichaCommand(fichaId, nuevoAsesorId);

        when(fichaPerfilOutputPort.buscarPorId(fichaId)).thenReturn(Optional.of(ficha));
        when(asesorFichaQueryOutputPort.buscarContactoPorId(nuevoAsesorId))
                .thenReturn(Optional.of(contactoDe(nuevoAsesorId)));

        // Act
        cambiarAsesorFichaUseCase.ejecutar(command);

        // Assert
        ArgumentCaptor<FichaPerfilAggregate> fichaCaptor = ArgumentCaptor.forClass(FichaPerfilAggregate.class);
        verify(fichaPerfilOutputPort).guardar(fichaCaptor.capture());
        assertThat(fichaCaptor.getValue().getTituloProyecto()).isEqualTo("Título de prueba");
    }

    @Test
    void debePublicarAsesorFichaCambiadoEvent_cuandoElCambioSePersiste() {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        UUID nuevoAsesorId = UUID.randomUUID();
        FichaPerfilAggregate ficha = fichaReconstruida(fichaId, UUID.randomUUID());
        var command = new CambiarAsesorFichaCommand(fichaId, nuevoAsesorId);

        when(fichaPerfilOutputPort.buscarPorId(fichaId)).thenReturn(Optional.of(ficha));
        when(asesorFichaQueryOutputPort.buscarContactoPorId(nuevoAsesorId))
                .thenReturn(Optional.of(contactoDe(nuevoAsesorId)));

        // Act
        cambiarAsesorFichaUseCase.ejecutar(command);

        // Assert — el evento sale con el correo dentro, listo para notificar sin volver a fichas
        ArgumentCaptor<AsesorFichaCambiadoEvent> eventoCaptor =
                ArgumentCaptor.forClass(AsesorFichaCambiadoEvent.class);
        verify(eventPublisher).publish(eventoCaptor.capture());
        assertThat(eventoCaptor.getValue().getFichaPerfilId()).isEqualTo(fichaId);
        assertThat(eventoCaptor.getValue().getTituloProyecto()).isEqualTo("Título de prueba");
        assertThat(eventoCaptor.getValue().getAsesorFichaId()).isEqualTo(nuevoAsesorId);
        assertThat(eventoCaptor.getValue().getAsesorNombre()).isEqualTo("Ana Gomez");
        assertThat(eventoCaptor.getValue().getAsesorEmail()).isEqualTo("ana.gomez@soyuco.edu.co");
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
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    void debeLanzarAsesorFichaNoEncontradoException_cuandoElContactoNoSePuedeLeer() {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        UUID nuevoAsesorId = UUID.randomUUID();
        FichaPerfilAggregate ficha = fichaReconstruida(fichaId, UUID.randomUUID());
        var command = new CambiarAsesorFichaCommand(fichaId, nuevoAsesorId);

        when(fichaPerfilOutputPort.buscarPorId(fichaId)).thenReturn(Optional.of(ficha));
        when(asesorFichaQueryOutputPort.buscarContactoPorId(nuevoAsesorId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> cambiarAsesorFichaUseCase.ejecutar(command))
                .isInstanceOf(AsesorFichaNoEncontradoException.class);

        verify(fichaPerfilOutputPort, never()).guardar(any());
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    void debePropagarExcepcionDelValidator_cuandoAsesorNoExiste() {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        UUID nuevoAsesorId = UUID.randomUUID();
        FichaPerfilAggregate ficha = fichaReconstruida(fichaId, UUID.randomUUID());
        var command = new CambiarAsesorFichaCommand(fichaId, nuevoAsesorId);

        when(fichaPerfilOutputPort.buscarPorId(fichaId)).thenReturn(Optional.of(ficha));
        doThrow(new AsesorFichaNoEncontradoException(nuevoAsesorId))
                .when(cambiarAsesorFichaValidator).validar(any(), any());

        // Act & Assert
        assertThatThrownBy(() -> cambiarAsesorFichaUseCase.ejecutar(command))
                .isInstanceOf(AsesorFichaNoEncontradoException.class)
                .hasMessageContaining(nuevoAsesorId.toString());

        verify(fichaPerfilOutputPort, never()).guardar(any());
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    void debePropagarExcepcionDelValidator_cuandoEstadoEsTerminal() {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        UUID nuevoAsesorId = UUID.randomUUID();
        FichaPerfilAggregate ficha = fichaReconstruida(fichaId, UUID.randomUUID());
        var command = new CambiarAsesorFichaCommand(fichaId, nuevoAsesorId);

        when(fichaPerfilOutputPort.buscarPorId(fichaId)).thenReturn(Optional.of(ficha));
        doThrow(new EstadoFichaPerfilTerminalException(EstadoFicha.APROBADA))
                .when(cambiarAsesorFichaValidator).validar(any(), any());

        // Act & Assert
        assertThatThrownBy(() -> cambiarAsesorFichaUseCase.ejecutar(command))
                .isInstanceOf(EstadoFichaPerfilTerminalException.class);

        verify(fichaPerfilOutputPort, never()).guardar(any());
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    void debePropagarExcepcionDelValidator_cuandoMismoAsesor() {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        UUID asesorActualId = UUID.randomUUID();
        FichaPerfilAggregate ficha = fichaReconstruida(fichaId, asesorActualId);
        var command = new CambiarAsesorFichaCommand(fichaId, asesorActualId);

        when(fichaPerfilOutputPort.buscarPorId(fichaId)).thenReturn(Optional.of(ficha));
        doThrow(new MismoAsesorFichaException(asesorActualId))
                .when(cambiarAsesorFichaValidator).validar(any(), any());

        // Act & Assert
        assertThatThrownBy(() -> cambiarAsesorFichaUseCase.ejecutar(command))
                .isInstanceOf(MismoAsesorFichaException.class)
                .hasMessageContaining(asesorActualId.toString());

        verify(fichaPerfilOutputPort, never()).guardar(any());
        verify(eventPublisher, never()).publish(any());
    }

    private FichaPerfilAggregate fichaReconstruida(UUID fichaId, UUID asesorId) {
        return FichaPerfilAggregate.reconstruir(fichaId, "Título de prueba", asesorId);
    }

    private AsesorContactoReadModel contactoDe(UUID asesorId) {
        return new AsesorContactoReadModel(asesorId, "Ana Gomez", "ana.gomez@soyuco.edu.co");
    }
}
