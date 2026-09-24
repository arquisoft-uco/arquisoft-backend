package com.arquisoft.usuarios.application.asesorficha.command.usecase.impl;

import com.arquisoft.shared.exception.InfrastructureException;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.constant.UsuariosRealmRoles;
import com.arquisoft.shared.publisher.EventPublisher;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.usuarios.application.asesorficha.command.finder.AsesorFichaPorUsuarioFinder;
import com.arquisoft.usuarios.application.asesorficha.command.secondaryport.AsesorFichaOutputPort;
import com.arquisoft.usuarios.application.asesorficha.command.validator.RemoverAsesorFichaValidator;
import com.arquisoft.usuarios.application.usuario.command.finder.UsuarioPorIdFinder;
import com.arquisoft.usuarios.application.usuario.command.secondaryport.ProveedorIdentidadOutputPort;
import com.arquisoft.usuarios.domain.asesorficha.AsesorFichaDomain;
import com.arquisoft.usuarios.domain.asesorficha.event.AsesorFichaRemovidoEvent;
import com.arquisoft.usuarios.domain.asesorficha.exception.AsesorFichaNoEncontradoException;
import com.arquisoft.usuarios.domain.estadousuario.EstadoUsuario;
import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RemoverAsesorFichaUseCaseImplTest {

    @Mock
    private AsesorFichaOutputPort asesorFichaOutputPort;
    @Mock
    private ProveedorIdentidadOutputPort proveedorIdentidadOutputPort;
    @Mock
    private AsesorFichaPorUsuarioFinder asesorFichaPorUsuarioFinder;
    @Mock
    private UsuarioPorIdFinder usuarioPorIdFinder;
    @Mock
    private RemoverAsesorFichaValidator removerAsesorFichaValidator;
    @Mock
    private EventPublisher eventPublisher;
    @Mock
    private AppLogger logger;

    private RemoverAsesorFichaUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new RemoverAsesorFichaUseCaseImpl(asesorFichaOutputPort, proveedorIdentidadOutputPort,
                asesorFichaPorUsuarioFinder, usuarioPorIdFinder, removerAsesorFichaValidator, eventPublisher,
                logger);
    }

    private UsuarioDomain usuario(UUID id) {
        return UsuarioDomain.reconstruir(id, "1036950123", "Laura Gomez", "laura@uco.edu.co", "573001112233",
                EstadoUsuario.ACTIVO);
    }

    @Test
    void debeEliminarRevocarYPublicarEnOrden_cuandoAsesorFichaEstaVigente() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var vigente = AsesorFichaDomain.crear(id);
        var usuario = usuario(id);
        when(asesorFichaPorUsuarioFinder.obtener(id)).thenReturn(vigente);
        when(usuarioPorIdFinder.obtener(id)).thenReturn(usuario);

        // Act
        useCase.ejecutar(AsesorFichaDomain.crear(id));

        // Assert
        verify(asesorFichaPorUsuarioFinder, times(1)).obtener(id);
        verify(usuarioPorIdFinder, times(1)).obtener(id);

        var orden = inOrder(removerAsesorFichaValidator, asesorFichaOutputPort, proveedorIdentidadOutputPort,
                eventPublisher);
        var captorInstante = ArgumentCaptor.forClass(Instant.class);
        var captorEvento = ArgumentCaptor.forClass(AsesorFichaRemovidoEvent.class);
        orden.verify(removerAsesorFichaValidator).validar(id, vigente);
        orden.verify(asesorFichaOutputPort).eliminarLogica(eq(id), captorInstante.capture());
        orden.verify(proveedorIdentidadOutputPort).revocarRealmRole(id, UsuariosRealmRoles.ASESOR_FICHA);
        orden.verify(eventPublisher).publish(captorEvento.capture());

        assertThat(captorInstante.getValue()).isNotEqualTo(UtilFecha.VACIO);
        var evento = captorEvento.getValue();
        assertThat(evento.getUsuario()).isEqualTo(id);
        assertThat(evento.getIdentificador()).isEqualTo(usuario.getIdentificador());
        assertThat(evento.getNombre()).isEqualTo(usuario.getNombre());
        assertThat(evento.getEmail()).isEqualTo(usuario.getEmail());
    }

    @Test
    void noDebeEscribirRevocarNiPublicar_cuandoElValidatorRechaza() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        when(asesorFichaPorUsuarioFinder.obtener(id)).thenReturn(AsesorFichaDomain.VACIO);
        when(usuarioPorIdFinder.obtener(id)).thenReturn(UsuarioDomain.VACIO);
        doThrow(new AsesorFichaNoEncontradoException(id))
                .when(removerAsesorFichaValidator).validar(id, AsesorFichaDomain.VACIO);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(AsesorFichaDomain.crear(id)))
                .isInstanceOf(AsesorFichaNoEncontradoException.class);
        verify(asesorFichaOutputPort, never()).eliminarLogica(any(), any());
        verify(proveedorIdentidadOutputPort, never()).revocarRealmRole(any(), anyString());
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    void debePropagarYNoPublicar_cuandoProveedorIdentidadFalla() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        when(asesorFichaPorUsuarioFinder.obtener(id)).thenReturn(AsesorFichaDomain.crear(id));
        when(usuarioPorIdFinder.obtener(id)).thenReturn(usuario(id));
        var fallo = new InfrastructureException("Proveedor de identidad caido", "PROVEEDOR_NO_DISPONIBLE");
        doThrow(fallo).when(proveedorIdentidadOutputPort).revocarRealmRole(id, UsuariosRealmRoles.ASESOR_FICHA);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(AsesorFichaDomain.crear(id))).isSameAs(fallo);
        verify(asesorFichaOutputPort, times(1)).eliminarLogica(eq(id), any());
        verify(eventPublisher, never()).publish(any());
    }
}
