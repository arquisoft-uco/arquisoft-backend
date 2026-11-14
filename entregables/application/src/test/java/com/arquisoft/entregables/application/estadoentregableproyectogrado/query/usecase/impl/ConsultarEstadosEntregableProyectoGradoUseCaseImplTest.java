package com.arquisoft.entregables.application.estadoentregableproyectogrado.query.usecase.impl;

import com.arquisoft.entregables.application.entregableproyectogrado.query.finder.EntregableProyectoGradoAccesoQueryFinder;
import com.arquisoft.entregables.application.entregableproyectogrado.query.finder.AsesorVigenteDeEntregableProyectoGradoQueryFinder;
import com.arquisoft.entregables.application.estadoentregableproyectogrado.query.criteria.EstadoEntregableProyectoGradoCriteria;
import com.arquisoft.entregables.application.estadoentregableproyectogrado.query.readmodel.EstadoEntregableProyectoGradoReadModel;
import com.arquisoft.entregables.application.estadoentregableproyectogrado.query.secondaryport.EstadoEntregableProyectoGradoQueryOutputPort;
import com.arquisoft.entregables.application.estadoentregableproyectogrado.query.validator.ConsultarEstadosEntregableProyectoGradoValidator;
import com.arquisoft.entregables.domain.asesorproyectogrado.exception.AsesorNoAsignadoProyectoGradoException;
import com.arquisoft.entregables.domain.entregableproyectogrado.EntregableProyectoGradoDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.entregables.EstadoEntregableProyectoGradoKey;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarEstadosEntregableProyectoGradoUseCaseImplTest {

    @Mock
    private EstadoEntregableProyectoGradoQueryOutputPort outputPort;

    @Mock
    private EntregableProyectoGradoAccesoQueryFinder accesoFinder;

    @Mock
    private AsesorVigenteDeEntregableProyectoGradoQueryFinder finder;

    @Mock
    private ConsultarEstadosEntregableProyectoGradoValidator validator;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarEstadosEntregableProyectoGradoUseCaseImpl useCase;

    private static EstadoEntregableProyectoGradoReadModel estado() {
        return new EstadoEntregableProyectoGradoReadModel(UtilUUID.generarNuevoUUID(), "DISPONIBLE_EVALUACION",
                "Disponible", Instant.now());
    }

    private static EntregableProyectoGradoDomain existente(UUID id) {
        return EntregableProyectoGradoDomain.reconstruir(id, UtilUUID.generarNuevoUUID(), 1, UtilUUID.generarNuevoUUID());
    }

    @Test
    void debeRetornarHistorial_cuandoValidacionPasa() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var estados = List.of(estado(), estado(), estado());
        var dominio = existente(entregable);
        when(accesoFinder.obtener(entregable)).thenReturn(dominio);
        when(finder.obtener(entregable)).thenReturn(asesor);
        when(outputPort.consultarPorEntregable(entregable)).thenReturn(estados);

        // Act
        var resultado = useCase.ejecutar(new EstadoEntregableProyectoGradoCriteria(entregable, asesor));

        // Assert
        assertThat(resultado).isSameAs(estados);
        var orden = inOrder(accesoFinder, finder, validator, outputPort);
        orden.verify(accesoFinder, times(1)).obtener(entregable);
        orden.verify(finder, times(1)).obtener(entregable);
        orden.verify(validator).validar(entregable, dominio, asesor, asesor);
        orden.verify(outputPort, times(1)).consultarPorEntregable(entregable);
        verify(logger).debug(eq(EstadoEntregableProyectoGradoKey.LOG_CONSULTANDO_HISTORIAL), eq(entregable));
        verify(logger).debug(eq(EstadoEntregableProyectoGradoKey.LOG_HISTORIAL_CONSULTADO), eq(entregable), eq(3));
    }

    @Test
    void debeNoLeerHistorial_cuandoValidadorLanza() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        var solicitante = UtilUUID.generarNuevoUUID();
        var asignado = UtilUUID.generarNuevoUUID();
        var dominio = existente(entregable);
        when(accesoFinder.obtener(entregable)).thenReturn(dominio);
        when(finder.obtener(entregable)).thenReturn(asignado);
        doThrow(new AsesorNoAsignadoProyectoGradoException(solicitante, entregable))
                .when(validator).validar(entregable, dominio, asignado, solicitante);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(new EstadoEntregableProyectoGradoCriteria(entregable, solicitante)))
                .isInstanceOf(AsesorNoAsignadoProyectoGradoException.class);
        verify(outputPort, never()).consultarPorEntregable(any(UUID.class));
        verify(logger, never()).debug(eq(EstadoEntregableProyectoGradoKey.LOG_HISTORIAL_CONSULTADO),
                eq(entregable), any());
    }
}
