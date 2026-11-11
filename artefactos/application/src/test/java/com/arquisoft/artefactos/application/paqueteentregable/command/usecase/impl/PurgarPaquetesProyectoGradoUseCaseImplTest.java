package com.arquisoft.artefactos.application.paqueteentregable.command.usecase.impl;

import com.arquisoft.artefactos.application.paqueteentregable.command.finder.ClavesPaquetesProyectoGradoFinder;
import com.arquisoft.artefactos.application.paqueteentregable.command.result.PurgaPaquetesProyectoGradoResult;
import com.arquisoft.artefactos.application.paqueteentregable.command.secondaryport.AlmacenamientoPaqueteOutputPort;
import com.arquisoft.artefactos.domain.paqueteentregable.PurgaPaquetesProyectoGradoDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.PaqueteEntregableKey;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PurgarPaquetesProyectoGradoUseCaseImplTest {

    @Mock
    private AlmacenamientoPaqueteOutputPort almacenamientoPaqueteOutputPort;
    @Mock
    private ClavesPaquetesProyectoGradoFinder clavesPaquetesProyectoGradoFinder;
    @Mock
    private AppLogger logger;

    @InjectMocks
    private PurgarPaquetesProyectoGradoUseCaseImpl useCase;

    @Test
    void debeEliminarCadaClaveEnOrdenYRetornarCantidad_cuandoHayPaquetes() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var purga = PurgaPaquetesProyectoGradoDomain.crear(proyecto);
        when(clavesPaquetesProyectoGradoFinder.obtener(purga))
                .thenReturn(List.of("paquetes/v1.zip", "paquetes/v2.zip", "paquetes/v3.zip"));

        // Act
        var resultado = useCase.ejecutar(purga);

        // Assert
        var orden = inOrder(clavesPaquetesProyectoGradoFinder, logger, almacenamientoPaqueteOutputPort);
        orden.verify(clavesPaquetesProyectoGradoFinder, times(1)).obtener(purga);
        orden.verify(logger).debug(PaqueteEntregableKey.LOG_VERIFICACION_PURGAR_PROYECTO, proyecto, 3);
        orden.verify(almacenamientoPaqueteOutputPort).eliminar("paquetes/v1.zip");
        orden.verify(almacenamientoPaqueteOutputPort).eliminar("paquetes/v2.zip");
        orden.verify(almacenamientoPaqueteOutputPort).eliminar("paquetes/v3.zip");
        assertThat(resultado).isEqualTo(new PurgaPaquetesProyectoGradoResult(proyecto, 3));
    }

    @Test
    void debeRetornarCeroSinEliminar_cuandoElProyectoNoTienePaquetes() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var purga = PurgaPaquetesProyectoGradoDomain.crear(proyecto);
        when(clavesPaquetesProyectoGradoFinder.obtener(purga)).thenReturn(List.of());

        // Act
        var resultado = useCase.ejecutar(purga);

        // Assert
        assertThat(resultado).isEqualTo(new PurgaPaquetesProyectoGradoResult(proyecto, 0));
        verify(almacenamientoPaqueteOutputPort, never()).eliminar(anyString());
    }
}
