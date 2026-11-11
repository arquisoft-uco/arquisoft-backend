package com.arquisoft.artefactos.application.proyectogrado.command.usecase.impl;

import com.arquisoft.artefactos.application.proyectogrado.command.finder.ProyectoGradoPorIdFinder;
import com.arquisoft.artefactos.application.proyectogrado.command.result.FinalizacionProyectoGradoResult;
import com.arquisoft.artefactos.application.proyectogrado.command.secondaryport.ProyectoGradoOutputPort;
import com.arquisoft.artefactos.application.proyectogrado.command.validator.FinalizarProyectoGradoValidator;
import com.arquisoft.artefactos.domain.proyectogrado.FinalizacionProyectoGradoDomain;
import com.arquisoft.artefactos.domain.proyectogrado.ProyectoGradoDomain;
import com.arquisoft.artefactos.domain.proyectogrado.exception.ProyectoGradoNoEncontradoException;
import com.arquisoft.artefactos.domain.proyectogrado.model.ExistenciaProyectoGrado;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FinalizarProyectoGradoUseCaseImplTest {

    @Mock
    private ProyectoGradoOutputPort proyectoGradoOutputPort;
    @Mock
    private ProyectoGradoPorIdFinder proyectoGradoPorIdFinder;
    @Mock
    private FinalizarProyectoGradoValidator finalizarProyectoGradoValidator;
    @Mock
    private AppLogger logger;

    @InjectMocks
    private FinalizarProyectoGradoUseCaseImpl useCase;

    @Test
    void debeMarcarEntregado_cuandoNoLoEstaba() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var finalizadoEn = Instant.parse("2026-11-01T10:00:00Z");
        var finalizacion = FinalizacionProyectoGradoDomain.crear(id, finalizadoEn);
        when(proyectoGradoPorIdFinder.obtener(id))
                .thenReturn(ProyectoGradoDomain.reconstruir(id, "Proyecto", UtilFecha.VACIO, UtilFecha.VACIO, null));

        // Act
        var resultado = useCase.ejecutar(finalizacion);

        // Assert
        var orden = inOrder(proyectoGradoPorIdFinder, finalizarProyectoGradoValidator, proyectoGradoOutputPort);
        orden.verify(proyectoGradoPorIdFinder, times(1)).obtener(id);
        orden.verify(finalizarProyectoGradoValidator).validar(new ExistenciaProyectoGrado(id, true));
        orden.verify(proyectoGradoOutputPort).marcarFinalizado(id, finalizadoEn);
        assertThat(resultado).isEqualTo(new FinalizacionProyectoGradoResult.Finalizado(id));
    }

    @Test
    void debeRetornarYaFinalizado_sinMarcar() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var finalizacion = FinalizacionProyectoGradoDomain.crear(id, Instant.parse("2026-11-02T10:00:00Z"));
        when(proyectoGradoPorIdFinder.obtener(id)).thenReturn(ProyectoGradoDomain.reconstruir(
                id, "Proyecto", UtilFecha.VACIO, Instant.parse("2026-11-01T10:00:00Z"), null));

        // Act
        var resultado = useCase.ejecutar(finalizacion);

        // Assert
        assertThat(resultado).isEqualTo(new FinalizacionProyectoGradoResult.YaFinalizado(id));
        verify(proyectoGradoOutputPort, never()).marcarFinalizado(any(), any());
    }

    @Test
    void debeRechazar_cuandoValidatorLanza() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var finalizacion = FinalizacionProyectoGradoDomain.crear(id, Instant.parse("2026-11-01T10:00:00Z"));
        when(proyectoGradoPorIdFinder.obtener(id)).thenReturn(ProyectoGradoDomain.VACIO);
        doThrow(new ProyectoGradoNoEncontradoException(id)).when(finalizarProyectoGradoValidator).validar(any());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(finalizacion)).isInstanceOf(ProyectoGradoNoEncontradoException.class);
        verify(proyectoGradoOutputPort, never()).marcarFinalizado(any(), any());
    }
}
