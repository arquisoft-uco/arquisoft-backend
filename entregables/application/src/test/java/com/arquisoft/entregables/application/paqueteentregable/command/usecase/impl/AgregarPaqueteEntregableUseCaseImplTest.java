package com.arquisoft.entregables.application.paqueteentregable.command.usecase.impl;

import com.arquisoft.entregables.application.artefactoentregable.command.usecase.AgregarArtefactosEntregableUseCase;
import com.arquisoft.entregables.application.paqueteentregable.command.finder.PaqueteEntregableExisteFinder;
import com.arquisoft.entregables.application.paqueteentregable.command.result.AgregacionPaqueteEntregableResult;
import com.arquisoft.entregables.application.paqueteentregable.command.secondaryport.PaqueteEntregableOutputPort;
import com.arquisoft.entregables.application.paqueteentregable.command.secondaryport.entity.PaqueteEntregableEntity;
import com.arquisoft.entregables.domain.artefactoentregable.AgregacionArtefactosEntregableDomain;
import com.arquisoft.entregables.domain.paqueteentregable.PaqueteEntregableDomain;
import com.arquisoft.entregables.domain.paqueteentregable.RegistroPaqueteEntregableDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AgregarPaqueteEntregableUseCaseImplTest {

    @Mock
    private PaqueteEntregableOutputPort paqueteEntregableOutputPort;

    @Mock
    private PaqueteEntregableExisteFinder paqueteEntregableExisteFinder;

    @Mock
    private AgregarArtefactosEntregableUseCase agregarArtefactosEntregableUseCase;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private AgregarPaqueteEntregableUseCaseImpl useCase;

    private static RegistroPaqueteEntregableDomain registro(PaqueteEntregableDomain paquete) {
        return RegistroPaqueteEntregableDomain.crear(paquete, AgregacionArtefactosEntregableDomain.crear(
                paquete.getEntregableProyectoGrado(), List.of()));
    }

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
        var registro = registro(paquete);
        var resultado = useCase.ejecutar(registro);

        // Assert
        var captor = ArgumentCaptor.forClass(PaqueteEntregableEntity.class);
        var orden = inOrder(paqueteEntregableExisteFinder, paqueteEntregableOutputPort, agregarArtefactosEntregableUseCase);
        orden.verify(paqueteEntregableExisteFinder, times(1)).obtener(paquete.getEntregableProyectoGrado());
        orden.verify(paqueteEntregableOutputPort).guardar(captor.capture());
        orden.verify(agregarArtefactosEntregableUseCase).ejecutar(registro.getArtefactos());
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
        var resultado = useCase.ejecutar(registro(paquete));

        // Assert
        verify(paqueteEntregableOutputPort, never()).guardar(any());
        verifyNoInteractions(agregarArtefactosEntregableUseCase);
        assertThat(resultado).isEqualTo(
                new AgregacionPaqueteEntregableResult.Duplicado(paquete.getEntregableProyectoGrado()));
    }
}
