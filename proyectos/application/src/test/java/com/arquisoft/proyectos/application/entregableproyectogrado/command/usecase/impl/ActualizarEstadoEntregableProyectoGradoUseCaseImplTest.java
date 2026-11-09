package com.arquisoft.proyectos.application.entregableproyectogrado.command.usecase.impl;

import com.arquisoft.proyectos.application.entregableproyectogrado.command.finder.EntregableProyectoGradoPorIdFinder;
import com.arquisoft.proyectos.application.entregableproyectogrado.command.result.ActualizacionEstadoEntregableProyectoGradoResult;
import com.arquisoft.proyectos.application.entregableproyectogrado.command.secondaryport.EntregableProyectoGradoOutputPort;
import com.arquisoft.proyectos.application.entregableproyectogrado.command.secondaryport.entity.EntregableProyectoGradoEntity;
import com.arquisoft.proyectos.domain.entregableproyectogrado.EntregableProyectoGradoDomain;
import com.arquisoft.proyectos.domain.estadoentregable.EstadoEntregable;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActualizarEstadoEntregableProyectoGradoUseCaseImplTest {

    private static final Instant VIGENTE_EN = Instant.parse("2026-11-01T10:00:00Z");

    @Mock
    private EntregableProyectoGradoOutputPort entregableProyectoGradoOutputPort;

    @Mock
    private EntregableProyectoGradoPorIdFinder entregableProyectoGradoPorIdFinder;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ActualizarEstadoEntregableProyectoGradoUseCaseImpl useCase;

    private final UUID id = UtilUUID.generarNuevoUUID();
    private final UUID proyectoGrado = UtilUUID.generarNuevoUUID();

    private EntregableProyectoGradoDomain entrada(EstadoEntregable estado, Instant ocurridoEn) {
        return EntregableProyectoGradoDomain.reconstruir(id, proyectoGrado, 1, estado, ocurridoEn);
    }

    @Test
    void debeGuardarYRetornarActualizado_cuandoElEntregableNoExiste() {
        // Arrange
        var entrada = entrada(EstadoEntregable.DISPONIBLE_EVALUACION, VIGENTE_EN);
        when(entregableProyectoGradoPorIdFinder.obtener(id)).thenReturn(EntregableProyectoGradoDomain.VACIO);

        // Act
        var resultado = useCase.ejecutar(entrada);

        // Assert
        var guardado = ArgumentCaptor.forClass(EntregableProyectoGradoEntity.class);
        verify(entregableProyectoGradoOutputPort).guardar(guardado.capture());
        assertThat(guardado.getValue().id()).isEqualTo(id);
        assertThat(guardado.getValue().estadoEntregable()).isEqualTo("DISPONIBLE_EVALUACION");
        assertThat(resultado).isInstanceOfSatisfying(ActualizacionEstadoEntregableProyectoGradoResult.Actualizado.class,
                actualizado -> {
                    assertThat(actualizado.entregableProyectoGrado()).isEqualTo(id);
                    assertThat(actualizado.estadoEntregable()).isEqualTo("DISPONIBLE_EVALUACION");
                });
    }

    @Test
    void debeGuardarYRetornarActualizado_cuandoElEventoEsMasNuevo() {
        // Arrange
        var vigente = entrada(EstadoEntregable.DISPONIBLE_EVALUACION, VIGENTE_EN);
        var entrada = entrada(EstadoEntregable.CERRADO, VIGENTE_EN.plusSeconds(1));
        when(entregableProyectoGradoPorIdFinder.obtener(id)).thenReturn(vigente);

        // Act
        var resultado = useCase.ejecutar(entrada);

        // Assert
        var guardado = ArgumentCaptor.forClass(EntregableProyectoGradoEntity.class);
        verify(entregableProyectoGradoOutputPort).guardar(guardado.capture());
        assertThat(guardado.getValue().estadoEntregable()).isEqualTo("CERRADO");
        assertThat(resultado).isInstanceOf(ActualizacionEstadoEntregableProyectoGradoResult.Actualizado.class);
    }

    @Test
    void debeDescartar_cuandoElEventoEsIgualOMasViejo() {
        // Arrange
        var vigente = entrada(EstadoEntregable.CERRADO, VIGENTE_EN);
        var igual = entrada(EstadoEntregable.CANCELADO, VIGENTE_EN);
        var viejo = entrada(EstadoEntregable.CANCELADO, VIGENTE_EN.minusSeconds(1));
        when(entregableProyectoGradoPorIdFinder.obtener(id)).thenReturn(vigente);

        // Act
        var resultadoIgual = useCase.ejecutar(igual);
        var resultadoViejo = useCase.ejecutar(viejo);

        // Assert
        verify(entregableProyectoGradoOutputPort, never()).guardar(any());
        assertThat(resultadoIgual).isInstanceOfSatisfying(
                ActualizacionEstadoEntregableProyectoGradoResult.Descartado.class,
                descartado -> assertThat(descartado.ocurridoEnVigente()).isEqualTo(VIGENTE_EN));
        assertThat(resultadoViejo).isInstanceOf(ActualizacionEstadoEntregableProyectoGradoResult.Descartado.class);
    }
}
