package com.arquisoft.usuarios.application.estadousuario.query.usecase.impl;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.ClaveMensaje;
import com.arquisoft.shared.message.key.usuarios.ConsultarEstadosUsuarioKey;
import com.arquisoft.usuarios.application.estadousuario.query.readmodel.EstadoUsuarioReadModel;
import com.arquisoft.usuarios.application.estadousuario.query.secondaryport.EstadoUsuarioQueryOutputPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarEstadosUsuarioUseCaseImplTest {

    @Mock
    private EstadoUsuarioQueryOutputPort estadoUsuarioQueryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarEstadosUsuarioUseCaseImpl useCase;

    @Test
    void debeRetornarLosEstadosYRegistrarElTotal_cuandoElPuertoLosDevuelve() {
        // Arrange
        var estados = List.of(
                new EstadoUsuarioReadModel("ACTIVO", "Activo", "Indica que un usuario puede desempeñarse"),
                new EstadoUsuarioReadModel("INACTIVO", "Inactivo", "Indica que un usuario no puede desempeñarse"));
        when(estadoUsuarioQueryOutputPort.consultarTodos()).thenReturn(estados);

        // Act
        var resultado = useCase.ejecutar();

        // Assert
        assertThat(resultado).containsExactlyElementsOf(estados);
        verify(estadoUsuarioQueryOutputPort, times(1)).consultarTodos();
        verify(logger).debug(eq(ConsultarEstadosUsuarioKey.LOG_CONSULTA_COMPLETADA), eq(2));
        verify(logger, never()).info(any(ClaveMensaje.class), any());
    }

    @Test
    void debeRetornarListaVacia_cuandoNoHayEstados() {
        // Arrange
        when(estadoUsuarioQueryOutputPort.consultarTodos()).thenReturn(List.of());

        // Act
        var resultado = useCase.ejecutar();

        // Assert
        assertThat(resultado).isEmpty();
        verify(logger).debug(eq(ConsultarEstadosUsuarioKey.LOG_CONSULTA_COMPLETADA), eq(0));
    }
}
