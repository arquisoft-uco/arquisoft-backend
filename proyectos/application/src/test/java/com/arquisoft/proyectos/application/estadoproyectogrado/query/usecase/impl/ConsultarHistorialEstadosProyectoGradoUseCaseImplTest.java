package com.arquisoft.proyectos.application.estadoproyectogrado.query.usecase.impl;

import com.arquisoft.proyectos.application.asesorproyectogrado.query.finder.AsesorAsignadoQueryFinder;
import com.arquisoft.proyectos.application.asesorproyectogrado.query.finder.model.AsignacionAsesorConsultada;
import com.arquisoft.proyectos.application.estadoproyectogrado.query.criteria.HistorialEstadosProyectoGradoCriteria;
import com.arquisoft.proyectos.application.estadoproyectogrado.query.readmodel.EstadoProyectoGradoReadModel;
import com.arquisoft.proyectos.application.estadoproyectogrado.query.secondaryport.EstadoProyectoGradoQueryOutputPort;
import com.arquisoft.proyectos.application.estadoproyectogrado.query.validator.ConsultarHistorialEstadosProyectoGradoValidator;
import com.arquisoft.proyectos.application.proyectogrado.query.finder.ProyectoGradoCoordinadorQueryFinder;
import com.arquisoft.proyectos.domain.estadoproyectogrado.exception.UsuarioSinAccesoEstadosProyectoGradoException;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.proyectos.EstadoProyectoGradoKey;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

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
class ConsultarHistorialEstadosProyectoGradoUseCaseImplTest {

    @Mock
    private ProyectoGradoCoordinadorQueryFinder proyectoGradoCoordinadorQueryFinder;

    @Mock
    private AsesorAsignadoQueryFinder asesorAsignadoQueryFinder;

    @Mock
    private ConsultarHistorialEstadosProyectoGradoValidator validator;

    @Mock
    private EstadoProyectoGradoQueryOutputPort outputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarHistorialEstadosProyectoGradoUseCaseImpl useCase;

    @Test
    void debeConsultarYRetornar_cuandoValidacionPasa() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var usuario = UtilUUID.generarNuevoUUID();
        var coordinador = UtilUUID.generarNuevoUUID();
        var criteria = new HistorialEstadosProyectoGradoCriteria(proyectoGrado, usuario);
        var consulta = new AsignacionAsesorConsultada(usuario, proyectoGrado);
        var historial = List.of(
                new EstadoProyectoGradoReadModel(UtilUUID.generarNuevoUUID(), "CANCELADO", "Cancelado",
                        Instant.parse("2026-10-03T10:00:00Z"), "Sin avance"),
                new EstadoProyectoGradoReadModel(UtilUUID.generarNuevoUUID(), "ATRASADO", "Atrasado",
                        Instant.parse("2026-10-02T10:00:00Z"), null),
                new EstadoProyectoGradoReadModel(UtilUUID.generarNuevoUUID(), "EN_CURSO", "En curso",
                        Instant.parse("2026-10-01T10:00:00Z"), null));
        when(proyectoGradoCoordinadorQueryFinder.obtener(proyectoGrado)).thenReturn(coordinador);
        when(asesorAsignadoQueryFinder.obtener(consulta)).thenReturn(true);
        when(outputPort.consultarPorProyecto(proyectoGrado)).thenReturn(historial);

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isSameAs(historial);
        var orden = inOrder(proyectoGradoCoordinadorQueryFinder, asesorAsignadoQueryFinder, validator, outputPort);
        orden.verify(proyectoGradoCoordinadorQueryFinder).obtener(proyectoGrado);
        orden.verify(asesorAsignadoQueryFinder).obtener(consulta);
        orden.verify(validator).validar(criteria, coordinador, true);
        orden.verify(outputPort).consultarPorProyecto(proyectoGrado);
        verify(proyectoGradoCoordinadorQueryFinder, times(1)).obtener(any());
        verify(asesorAsignadoQueryFinder, times(1)).obtener(any());
        verify(outputPort, times(1)).consultarPorProyecto(any());
        verify(logger).debug(EstadoProyectoGradoKey.LOG_CONSULTANDO_HISTORIAL, proyectoGrado, usuario);
        verify(logger).debug(EstadoProyectoGradoKey.LOG_CONSULTA_COMPLETADA, 3);
    }

    @Test
    void debeNoConsultar_cuandoValidatorLanza() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var usuario = UtilUUID.generarNuevoUUID();
        var coordinador = UtilUUID.generarNuevoUUID();
        var criteria = new HistorialEstadosProyectoGradoCriteria(proyectoGrado, usuario);
        when(proyectoGradoCoordinadorQueryFinder.obtener(proyectoGrado)).thenReturn(coordinador);
        when(asesorAsignadoQueryFinder.obtener(any())).thenReturn(false);
        doThrow(new UsuarioSinAccesoEstadosProyectoGradoException(proyectoGrado, usuario))
                .when(validator).validar(criteria, coordinador, false);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(criteria))
                .isInstanceOf(UsuarioSinAccesoEstadosProyectoGradoException.class);
        verify(outputPort, never()).consultarPorProyecto(any());
    }
}
