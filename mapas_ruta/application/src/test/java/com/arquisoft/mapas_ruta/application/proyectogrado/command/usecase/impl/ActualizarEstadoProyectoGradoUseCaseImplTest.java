package com.arquisoft.mapas_ruta.application.proyectogrado.command.usecase.impl;

import com.arquisoft.mapas_ruta.application.proyectogrado.command.finder.ProyectoGradoPorIdFinder;
import com.arquisoft.mapas_ruta.application.proyectogrado.command.result.ActualizacionEstadoProyectoGradoResult;
import com.arquisoft.mapas_ruta.application.proyectogrado.command.secondaryport.ProyectoGradoOutputPort;
import com.arquisoft.mapas_ruta.application.proyectogrado.command.validator.ActualizarEstadoProyectoGradoValidator;
import com.arquisoft.mapas_ruta.domain.proyectogrado.ActualizacionEstadoProyectoGradoDomain;
import com.arquisoft.mapas_ruta.domain.proyectogrado.ProyectoGradoDomain;
import com.arquisoft.mapas_ruta.domain.proyectogrado.exception.ProyectoGradoNoEncontradoException;
import com.arquisoft.mapas_ruta.domain.proyectogrado.model.EstadoProyectoGrado;
import com.arquisoft.mapas_ruta.domain.proyectogrado.model.ExistenciaProyectoGrado;
import com.arquisoft.shared.logger.AppLogger;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActualizarEstadoProyectoGradoUseCaseImplTest {

    private static final Instant GUARDADO = Instant.parse("2026-11-01T10:00:00Z");

    @Mock
    private ProyectoGradoOutputPort proyectoGradoOutputPort;

    @Mock
    private ProyectoGradoPorIdFinder proyectoGradoPorIdFinder;

    @Mock
    private ActualizarEstadoProyectoGradoValidator validator;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ActualizarEstadoProyectoGradoUseCaseImpl useCase;

    private static ProyectoGradoDomain proyecto() {
        return ProyectoGradoDomain.reconstruir(UtilUUID.generarNuevoUUID(), EstadoProyectoGrado.EN_PROCESO,
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), "Titulo", GUARDADO);
    }

    @Test
    void debeActualizar_cuandoOcurridoEnEsPosterior() {
        // Arrange
        var proyecto = proyecto();
        var ocurridoEn = GUARDADO.plusSeconds(60);
        var actualizacion = ActualizacionEstadoProyectoGradoDomain.crear(proyecto.getId(), "ATRASADO", ocurridoEn);
        when(proyectoGradoPorIdFinder.obtener(proyecto.getId())).thenReturn(proyecto);

        // Act
        var resultado = useCase.ejecutar(actualizacion);

        // Assert
        assertThat(resultado).isEqualTo(
                new ActualizacionEstadoProyectoGradoResult.Actualizado(proyecto.getId(), "ATRASADO"));
        var orden = inOrder(proyectoGradoPorIdFinder, validator, proyectoGradoOutputPort);
        orden.verify(proyectoGradoPorIdFinder).obtener(proyecto.getId());
        orden.verify(validator).validar(new ExistenciaProyectoGrado(proyecto.getId(), true));
        orden.verify(proyectoGradoOutputPort).actualizarEstado(proyecto.getId(), "ATRASADO", ocurridoEn);
    }

    @Test
    void debeDevolverDescartado_cuandoOcurridoEnEsIgualOAnterior() {
        // Arrange
        var proyecto = proyecto();
        var actualizacion = ActualizacionEstadoProyectoGradoDomain.crear(
                proyecto.getId(), "ATRASADO", GUARDADO.minusSeconds(60));
        when(proyectoGradoPorIdFinder.obtener(proyecto.getId())).thenReturn(proyecto);

        // Act
        var resultado = useCase.ejecutar(actualizacion);

        // Assert
        assertThat(resultado).isEqualTo(
                new ActualizacionEstadoProyectoGradoResult.Descartado(proyecto.getId(), GUARDADO));
        verify(proyectoGradoOutputPort, never()).actualizarEstado(any(), any(), any());
    }

    @Test
    void debeLanzar_cuandoValidatorRechaza() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var actualizacion = ActualizacionEstadoProyectoGradoDomain.crear(id, "ATRASADO", GUARDADO);
        when(proyectoGradoPorIdFinder.obtener(id)).thenReturn(ProyectoGradoDomain.VACIO);
        doThrow(new ProyectoGradoNoEncontradoException(id)).when(validator).validar(any());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(actualizacion))
                .isInstanceOf(ProyectoGradoNoEncontradoException.class);
        verify(proyectoGradoOutputPort, never()).actualizarEstado(any(), any(), any());
    }
}
