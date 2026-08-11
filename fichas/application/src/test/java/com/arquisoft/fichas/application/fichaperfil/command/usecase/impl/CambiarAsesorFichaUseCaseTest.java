package com.arquisoft.fichas.application.fichaperfil.command.usecase.impl;

import com.arquisoft.shared.message.CatalogoMensajes;
import com.arquisoft.shared.message.CatalogoMensajesResourceBundle;
import com.arquisoft.fichas.application.asesorficha.query.finder.AsesorFichaFinder;
import com.arquisoft.fichas.application.asesorficha.query.readmodel.AsesorContactoReadModel;
import com.arquisoft.fichas.application.fichaperfil.command.finder.FichaPerfilFinder;
import com.arquisoft.fichas.application.fichaperfil.command.validator.CambiarAsesorFichaValidator;
import com.arquisoft.fichas.domain.estadoficha.EstadoFicha;
import com.arquisoft.fichas.domain.estadofichaperfil.exception.EstadoFichaPerfilTerminalException;
import com.arquisoft.fichas.domain.fichaperfil.CambiarAsesorFichaDomain;
import com.arquisoft.fichas.domain.fichaperfil.FichaPerfilDomain;
import com.arquisoft.fichas.domain.fichaperfil.event.AsesorFichaCambiadoEvent;
import com.arquisoft.fichas.domain.fichaperfil.exception.AsesorFichaNoEncontradoException;
import com.arquisoft.fichas.domain.fichaperfil.exception.FichaPerfilNoEncontradaException;
import com.arquisoft.fichas.domain.fichaperfil.exception.MismoAsesorFichaException;
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
    private FichaPerfilFinder fichaPerfilFinder;

    @Mock
    private AsesorFichaFinder asesorFichaFinder;

    @Mock
    private CambiarAsesorFichaValidator cambiarAsesorFichaValidator;

    @Mock
    private EventPublisher eventPublisher;

    @Mock
    private AppLogger logger;

    // Catalogo real, no mock: varios mensajes acaban en la excepcion o en el
    // resultado, y un mock los dejaria en null.
    @Spy
    private CatalogoMensajes catalogo = CatalogoMensajesResourceBundle.porDefecto();

    @InjectMocks
    private CambiarAsesorFichaUseCaseImpl cambiarAsesorFichaUseCase;

    @Test
    void debeCambiarAsesor_cuandoDatosValidos() {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        UUID nuevoAsesorId = UUID.randomUUID();
        CambiarAsesorFichaDomain entrada = fichaEntrada(fichaId, nuevoAsesorId);

        when(fichaPerfilFinder.obtener(fichaId)).thenReturn(fichaReconstruida(fichaId, UUID.randomUUID()));
        when(asesorFichaFinder.obtener(nuevoAsesorId)).thenReturn(contactoDe(nuevoAsesorId));

        // Act
        cambiarAsesorFichaUseCase.ejecutar(entrada);

        // Assert
        verify(fichaPerfilOutputPort).actualizarAsesor(fichaId, nuevoAsesorId);
    }

    @Test
    void debePublicarAsesorFichaCambiadoEvent_cuandoElCambioSePersiste() {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        UUID nuevoAsesorId = UUID.randomUUID();
        CambiarAsesorFichaDomain entrada = fichaEntrada(fichaId, nuevoAsesorId);

        when(fichaPerfilFinder.obtener(fichaId)).thenReturn(fichaReconstruida(fichaId, UUID.randomUUID()));
        when(asesorFichaFinder.obtener(nuevoAsesorId)).thenReturn(contactoDe(nuevoAsesorId));

        // Act
        cambiarAsesorFichaUseCase.ejecutar(entrada);

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
        CambiarAsesorFichaDomain entrada = fichaEntrada(fichaId, UUID.randomUUID());

        doThrow(new FichaPerfilNoEncontradaException(fichaId)).when(fichaPerfilFinder).obtener(fichaId);

        // Act & Assert
        assertThatThrownBy(() -> cambiarAsesorFichaUseCase.ejecutar(entrada))
                .isInstanceOf(FichaPerfilNoEncontradaException.class)
                .hasMessageContaining(fichaId.toString());

        verify(fichaPerfilOutputPort, never()).actualizarAsesor(any(), any());
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    void debeLanzarAsesorFichaNoEncontradoException_cuandoElContactoNoSePuedeLeer() {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        UUID nuevoAsesorId = UUID.randomUUID();
        CambiarAsesorFichaDomain entrada = fichaEntrada(fichaId, nuevoAsesorId);

        when(fichaPerfilFinder.obtener(fichaId)).thenReturn(fichaReconstruida(fichaId, UUID.randomUUID()));
        doThrow(new AsesorFichaNoEncontradoException(nuevoAsesorId))
                .when(asesorFichaFinder).obtener(nuevoAsesorId);

        // Act & Assert
        assertThatThrownBy(() -> cambiarAsesorFichaUseCase.ejecutar(entrada))
                .isInstanceOf(AsesorFichaNoEncontradoException.class);

        verify(fichaPerfilOutputPort, never()).actualizarAsesor(any(), any());
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    void debePropagarExcepcionDelValidator_cuandoEstadoEsTerminal() {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        UUID nuevoAsesorId = UUID.randomUUID();
        CambiarAsesorFichaDomain entrada = fichaEntrada(fichaId, nuevoAsesorId);

        when(fichaPerfilFinder.obtener(fichaId)).thenReturn(fichaReconstruida(fichaId, UUID.randomUUID()));
        doThrow(new EstadoFichaPerfilTerminalException(EstadoFicha.APROBADA))
                .when(cambiarAsesorFichaValidator).validar(any(), any());

        // Act & Assert
        assertThatThrownBy(() -> cambiarAsesorFichaUseCase.ejecutar(entrada))
                .isInstanceOf(EstadoFichaPerfilTerminalException.class);

        verify(fichaPerfilOutputPort, never()).actualizarAsesor(any(), any());
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    void debePropagarExcepcionDelValidator_cuandoMismoAsesor() {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        UUID asesorActualId = UUID.randomUUID();
        CambiarAsesorFichaDomain entrada = fichaEntrada(fichaId, asesorActualId);

        when(fichaPerfilFinder.obtener(fichaId)).thenReturn(fichaReconstruida(fichaId, asesorActualId));
        doThrow(new MismoAsesorFichaException(asesorActualId))
                .when(cambiarAsesorFichaValidator).validar(any(), any());

        // Act & Assert
        assertThatThrownBy(() -> cambiarAsesorFichaUseCase.ejecutar(entrada))
                .isInstanceOf(MismoAsesorFichaException.class)
                .hasMessageContaining(asesorActualId.toString());

        verify(fichaPerfilOutputPort, never()).actualizarAsesor(any(), any());
        verify(eventPublisher, never()).publish(any());
    }

    private CambiarAsesorFichaDomain fichaEntrada(UUID fichaId, UUID nuevoAsesorId) {
        return CambiarAsesorFichaDomain.crear(fichaId, nuevoAsesorId);
    }

    private FichaPerfilDomain fichaReconstruida(UUID fichaId, UUID asesorId) {
        return FichaPerfilDomain.reconstruir(fichaId, "Título de prueba", asesorId);
    }

    private AsesorContactoReadModel contactoDe(UUID asesorId) {
        return new AsesorContactoReadModel(asesorId, "Ana Gomez", "ana.gomez@soyuco.edu.co");
    }
}
