package com.arquisoft.artefactos.application.proyectogrado.command.usecase.impl;

import com.arquisoft.artefactos.application.proyectogrado.command.finder.ProyectoGradoPorIdFinder;
import com.arquisoft.artefactos.application.proyectogrado.command.result.ActualizacionEstadoProyectoGradoResult;
import com.arquisoft.artefactos.application.proyectogrado.command.secondaryport.ProyectoGradoOutputPort;
import com.arquisoft.artefactos.application.proyectogrado.command.validator.ActualizarEstadoProyectoGradoValidator;
import com.arquisoft.artefactos.domain.proyectogrado.ActualizacionEstadoProyectoGradoDomain;
import com.arquisoft.artefactos.domain.proyectogrado.ProyectoGradoDomain;
import com.arquisoft.artefactos.domain.proyectogrado.model.EstadoProyecto;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActualizarEstadoProyectoGradoUseCaseImplTest {

    private static final Instant GUARDADO = Instant.parse("2026-11-02T10:00:00Z");

    @Mock
    private ProyectoGradoOutputPort proyectoGradoOutputPort;
    @Mock
    private ProyectoGradoPorIdFinder proyectoGradoPorIdFinder;
    @Mock
    private ActualizarEstadoProyectoGradoValidator actualizarEstadoProyectoGradoValidator;
    @Mock
    private AppLogger logger;

    @InjectMocks
    private ActualizarEstadoProyectoGradoUseCaseImpl useCase;

    @Test
    void debeActualizar_cuandoEsMasReciente() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var ocurridoEn = GUARDADO.plusSeconds(60);
        var actualizacion = ActualizacionEstadoProyectoGradoDomain.crear(id, "FINALIZADO", ocurridoEn);
        when(proyectoGradoPorIdFinder.obtener(id)).thenReturn(
                ProyectoGradoDomain.reconstruir(id, "Proyecto", UtilFecha.VACIO, EstadoProyecto.EN_PROCESO, GUARDADO));

        // Act
        var resultado = useCase.ejecutar(actualizacion);

        // Assert
        var orden = inOrder(proyectoGradoPorIdFinder, actualizarEstadoProyectoGradoValidator, proyectoGradoOutputPort);
        orden.verify(proyectoGradoPorIdFinder, times(1)).obtener(id);
        orden.verify(actualizarEstadoProyectoGradoValidator).validar(new ExistenciaProyectoGrado(id, true));
        orden.verify(proyectoGradoOutputPort).actualizarEstado(id, "FINALIZADO", ocurridoEn);
        assertThat(resultado).isEqualTo(new ActualizacionEstadoProyectoGradoResult.Actualizado(id, "FINALIZADO"));
    }

    @Test
    void debeActualizar_cuandoNoHayOcurridoEnGuardado() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var actualizacion = ActualizacionEstadoProyectoGradoDomain.crear(id, "ATRASADO", GUARDADO);
        when(proyectoGradoPorIdFinder.obtener(id)).thenReturn(
                ProyectoGradoDomain.reconstruir(id, "Proyecto", UtilFecha.VACIO, EstadoProyecto.EN_PROCESO, null));

        // Act
        var resultado = useCase.ejecutar(actualizacion);

        // Assert
        verify(proyectoGradoOutputPort).actualizarEstado(id, "ATRASADO", GUARDADO);
        assertThat(resultado).isEqualTo(new ActualizacionEstadoProyectoGradoResult.Actualizado(id, "ATRASADO"));
    }

    @Test
    void debeDescartar_cuandoEsMasViejo() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var actualizacion = ActualizacionEstadoProyectoGradoDomain.crear(id, "CANCELADO", GUARDADO.minusSeconds(60));
        when(proyectoGradoPorIdFinder.obtener(id)).thenReturn(
                ProyectoGradoDomain.reconstruir(id, "Proyecto", UtilFecha.VACIO, EstadoProyecto.FINALIZADO, GUARDADO));

        // Act
        var resultado = useCase.ejecutar(actualizacion);

        // Assert
        assertThat(resultado).isEqualTo(new ActualizacionEstadoProyectoGradoResult.Descartado(id, GUARDADO));
        verify(proyectoGradoOutputPort, never()).actualizarEstado(any(), any(), any());
    }

    @Test
    void debeDescartar_cuandoEsIgual() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var actualizacion = ActualizacionEstadoProyectoGradoDomain.crear(id, "FINALIZADO", GUARDADO);
        when(proyectoGradoPorIdFinder.obtener(id)).thenReturn(
                ProyectoGradoDomain.reconstruir(id, "Proyecto", UtilFecha.VACIO, EstadoProyecto.FINALIZADO, GUARDADO));

        // Act
        var resultado = useCase.ejecutar(actualizacion);

        // Assert
        assertThat(resultado).isEqualTo(new ActualizacionEstadoProyectoGradoResult.Descartado(id, GUARDADO));
        verify(proyectoGradoOutputPort, never()).actualizarEstado(any(), any(), any());
    }
}
