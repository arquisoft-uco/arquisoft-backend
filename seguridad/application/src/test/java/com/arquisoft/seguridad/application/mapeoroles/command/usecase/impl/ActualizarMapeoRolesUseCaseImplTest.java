package com.arquisoft.seguridad.application.mapeoroles.command.usecase.impl;

import com.arquisoft.seguridad.application.mapeoroles.command.finder.MapeoRolesVigenteFinder;
import com.arquisoft.seguridad.application.mapeoroles.command.result.ActualizacionMapeoRolesResult;
import com.arquisoft.seguridad.application.mapeoroles.command.secondaryport.FuenteMapeoRolesOutputPort;
import com.arquisoft.seguridad.application.mapeoroles.command.secondaryport.MapeoRolesOutputPort;
import com.arquisoft.seguridad.application.mapeoroles.command.secondaryport.entity.MapeoRolesEntity;
import com.arquisoft.seguridad.application.mapeoroles.command.secondaryport.model.ObtencionMapeoRoles;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.seguridad.MapeoRolesKey;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActualizarMapeoRolesUseCaseImplTest {

    @Mock
    private MapeoRolesVigenteFinder mapeoRolesVigenteFinder;

    @Mock
    private MapeoRolesOutputPort mapeoRolesOutputPort;

    @Mock
    private FuenteMapeoRolesOutputPort fuenteMapeoRolesOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ActualizarMapeoRolesUseCaseImpl useCase;

    @Test
    void debeActualizar_cuandoNoVigenteYGanaLaReserva() {
        // Arrange
        var mapeo = new MapeoRolesEntity(Map.of(
                "asesor", Set.of("fichas:ver", "fichas:editar"),
                "estudiante", Set.of("fichas:ver")));
        when(mapeoRolesVigenteFinder.obtener()).thenReturn(false);
        when(mapeoRolesOutputPort.reservarActualizacion()).thenReturn(true);
        when(fuenteMapeoRolesOutputPort.obtener()).thenReturn(new ObtencionMapeoRoles.Obtenida(mapeo));

        // Act
        var resultado = useCase.ejecutar();

        // Assert
        assertThat(resultado).isEqualTo(new ActualizacionMapeoRolesResult.Actualizada(2, 2));
        var orden = inOrder(mapeoRolesVigenteFinder, mapeoRolesOutputPort, fuenteMapeoRolesOutputPort, logger);
        orden.verify(mapeoRolesVigenteFinder).obtener();
        orden.verify(mapeoRolesOutputPort).reservarActualizacion();
        orden.verify(fuenteMapeoRolesOutputPort).obtener();
        orden.verify(mapeoRolesOutputPort).guardar(mapeo);
        orden.verify(logger).info(MapeoRolesKey.LOG_ACTUALIZADA, 2, 2);
    }

    @Test
    void debeDevolverVigente_cuandoElMapeoEstaVigente() {
        // Arrange
        when(mapeoRolesVigenteFinder.obtener()).thenReturn(true);

        // Act
        var resultado = useCase.ejecutar();

        // Assert
        assertThat(resultado).isInstanceOf(ActualizacionMapeoRolesResult.Vigente.class);
        verifyNoInteractions(fuenteMapeoRolesOutputPort);
        verify(mapeoRolesOutputPort, never()).reservarActualizacion();
        verify(mapeoRolesOutputPort, never()).guardar(any());
    }

    @Test
    void debeOmitir_cuandoOtraReplicaTieneLaReserva() {
        // Arrange
        when(mapeoRolesVigenteFinder.obtener()).thenReturn(false);
        when(mapeoRolesOutputPort.reservarActualizacion()).thenReturn(false);

        // Act
        var resultado = useCase.ejecutar();

        // Assert
        assertThat(resultado).isInstanceOf(ActualizacionMapeoRolesResult.Omitida.class);
        verifyNoInteractions(fuenteMapeoRolesOutputPort);
        verify(mapeoRolesOutputPort, never()).guardar(any());
        verify(logger).debug(MapeoRolesKey.LOG_ACTUALIZACION_OMITIDA);
    }

    @Test
    void debeConservarElMapeo_cuandoLaFuenteNoEstaDisponible() {
        // Arrange
        when(mapeoRolesVigenteFinder.obtener()).thenReturn(false);
        when(mapeoRolesOutputPort.reservarActualizacion()).thenReturn(true);
        when(fuenteMapeoRolesOutputPort.obtener()).thenReturn(new ObtencionMapeoRoles.NoDisponible("503"));

        // Act
        var resultado = useCase.ejecutar();

        // Assert
        assertThat(resultado).isEqualTo(new ActualizacionMapeoRolesResult.Fallida("503"));
        verify(mapeoRolesOutputPort, never()).guardar(any());
        verify(logger).warn(MapeoRolesKey.LOG_MAPEO_CONSERVADO, "503");
    }
}
