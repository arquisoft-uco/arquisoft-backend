package com.arquisoft.artefactos.application.paqueteentregable.command.usecase.impl;

import com.arquisoft.artefactos.application.paqueteentregable.command.finder.ClavesPaquetesAnterioresFinder;
import com.arquisoft.artefactos.application.paqueteentregable.command.result.PurgaPaquetesAnterioresResult;
import com.arquisoft.artefactos.application.paqueteentregable.command.secondaryport.AlmacenamientoPaqueteOutputPort;
import com.arquisoft.artefactos.domain.paqueteentregable.PurgaPaquetesAnterioresDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.ClaveMensaje;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

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
class PurgarPaquetesAnterioresUseCaseImplTest {

    @Mock
    private AlmacenamientoPaqueteOutputPort almacenamientoPaqueteOutputPort;
    @Mock
    private ClavesPaquetesAnterioresFinder clavesPaquetesAnterioresFinder;
    @Mock
    private AppLogger logger;

    @InjectMocks
    private PurgarPaquetesAnterioresUseCaseImpl useCase;

    @Test
    void debeEliminarCadaClaveEnOrden_cuandoHayPaquetesAnteriores() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var purga = PurgaPaquetesAnterioresDomain.crear(proyecto, 4);
        when(clavesPaquetesAnterioresFinder.obtener(purga))
                .thenReturn(List.of("paquetes/v1.zip", "paquetes/v2.zip", "paquetes/v3.zip"));

        // Act
        var resultado = useCase.ejecutar(purga);

        // Assert
        var orden = inOrder(clavesPaquetesAnterioresFinder, almacenamientoPaqueteOutputPort);
        orden.verify(clavesPaquetesAnterioresFinder, times(1)).obtener(purga);
        orden.verify(almacenamientoPaqueteOutputPort).eliminar("paquetes/v1.zip");
        orden.verify(almacenamientoPaqueteOutputPort).eliminar("paquetes/v2.zip");
        orden.verify(almacenamientoPaqueteOutputPort).eliminar("paquetes/v3.zip");
        assertThat(resultado).isEqualTo(new PurgaPaquetesAnterioresResult(proyecto, 3));
    }

    @Test
    void debeRetornarCero_cuandoNoHayPaquetesAnteriores() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var purga = PurgaPaquetesAnterioresDomain.crear(proyecto, 1);
        when(clavesPaquetesAnterioresFinder.obtener(purga)).thenReturn(List.of());

        // Act
        var resultado = useCase.ejecutar(purga);

        // Assert
        assertThat(resultado).isEqualTo(new PurgaPaquetesAnterioresResult(proyecto, 0));
        verify(almacenamientoPaqueteOutputPort, never()).eliminar(anyString());
    }

    @Test
    void noDebeCerrarElFlujo_cuandoElAlmacenamientoFalla() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var purga = PurgaPaquetesAnterioresDomain.crear(proyecto, 2);
        when(clavesPaquetesAnterioresFinder.obtener(purga)).thenReturn(List.of("paquetes/v1.zip"));
        doThrow(new IllegalStateException("almacenamiento caido")).when(almacenamientoPaqueteOutputPort)
                .eliminar("paquetes/v1.zip");

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(purga)).isInstanceOf(IllegalStateException.class);
        verify(logger, never()).info(any(ClaveMensaje.class), eq(proyecto), eq(1));
    }
}
