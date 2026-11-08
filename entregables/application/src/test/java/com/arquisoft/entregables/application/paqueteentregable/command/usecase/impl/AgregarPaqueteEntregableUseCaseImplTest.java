package com.arquisoft.entregables.application.paqueteentregable.command.usecase.impl;

import com.arquisoft.entregables.application.paqueteentregable.command.finder.PaqueteEntregableExisteFinder;
import com.arquisoft.entregables.application.paqueteentregable.command.result.AgregacionPaqueteEntregableResult;
import com.arquisoft.entregables.application.paqueteentregable.command.secondaryport.PaqueteEntregableOutputPort;
import com.arquisoft.entregables.application.paqueteentregable.command.secondaryport.entity.PaqueteEntregableEntity;
import com.arquisoft.entregables.domain.paqueteentregable.PaqueteEntregableDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AgregarPaqueteEntregableUseCaseImplTest {

    @Mock
    private PaqueteEntregableOutputPort paqueteEntregableOutputPort;

    @Mock
    private PaqueteEntregableExisteFinder paqueteEntregableExisteFinder;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private AgregarPaqueteEntregableUseCaseImpl useCase;

    private static PaqueteEntregableDomain paquete() {
        return PaqueteEntregableDomain.crear(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), "bucket",
                "paquetes/x.zip", "p.zip", 10L, Instant.now());
    }

    @Test
    void debeGuardarYDevolverAgregado_cuandoNoExiste() {
        // Arrange
        var paquete = paquete();
        when(paqueteEntregableExisteFinder.obtener(paquete.getEntregableProyectoGrado())).thenReturn(false);

        // Act
        var resultado = useCase.ejecutar(paquete);

        // Assert
        var captor = ArgumentCaptor.forClass(PaqueteEntregableEntity.class);
        var orden = inOrder(paqueteEntregableExisteFinder, paqueteEntregableOutputPort);
        orden.verify(paqueteEntregableExisteFinder, times(1)).obtener(paquete.getEntregableProyectoGrado());
        orden.verify(paqueteEntregableOutputPort).guardar(captor.capture());
        assertThat(captor.getValue().id()).isEqualTo(paquete.getId());
        assertThat(captor.getValue().entregableProyectoGrado()).isEqualTo(paquete.getEntregableProyectoGrado());
        assertThat(captor.getValue().bucket()).isEqualTo("bucket");
        assertThat(captor.getValue().claveObjeto()).isEqualTo("paquetes/x.zip");
        assertThat(captor.getValue().nombreArchivo()).isEqualTo("p.zip");
        assertThat(captor.getValue().tamanoBytes()).isEqualTo(10L);
        assertThat(resultado).isEqualTo(
                new AgregacionPaqueteEntregableResult.Agregado(paquete.getEntregableProyectoGrado()));
    }

    @Test
    void debeDevolverDuplicadoSinGuardar_cuandoYaExiste() {
        // Arrange
        var paquete = paquete();
        when(paqueteEntregableExisteFinder.obtener(paquete.getEntregableProyectoGrado())).thenReturn(true);

        // Act
        var resultado = useCase.ejecutar(paquete);

        // Assert
        verify(paqueteEntregableOutputPort, never()).guardar(any());
        assertThat(resultado).isEqualTo(
                new AgregacionPaqueteEntregableResult.Duplicado(paquete.getEntregableProyectoGrado()));
    }
}
