package com.arquisoft.proyectos.application.estadoproyectogrado.query.usecase.impl;

import com.arquisoft.proyectos.application.estadoproyectogrado.query.criteria.EstadoProyectoGradoEstudianteCriteria;
import com.arquisoft.proyectos.application.estadoproyectogrado.query.finder.EstudianteVinculadoEstadoProyectoGradoQueryFinder;
import com.arquisoft.proyectos.application.estadoproyectogrado.query.readmodel.EstadoProyectoGradoReadModel;
import com.arquisoft.proyectos.application.estadoproyectogrado.query.secondaryport.EstadoProyectoGradoQueryOutputPort;
import com.arquisoft.proyectos.application.estadoproyectogrado.query.validator.ConsultarEstadosProyectoGradoEstudianteValidator;
import com.arquisoft.proyectos.application.proyectogrado.query.finder.ProyectoGradoExisteQueryFinder;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarEstadosProyectoGradoEstudianteUseCaseImplTest {

    @Mock
    private ProyectoGradoExisteQueryFinder proyectoGradoExisteQueryFinder;

    @Mock
    private EstudianteVinculadoEstadoProyectoGradoQueryFinder estudianteVinculadoEstadoProyectoGradoQueryFinder;

    @Mock
    private ConsultarEstadosProyectoGradoEstudianteValidator validator;

    @Mock
    private EstadoProyectoGradoQueryOutputPort estadoProyectoGradoQueryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarEstadosProyectoGradoEstudianteUseCaseImpl useCase;

    private EstadoProyectoGradoReadModel estado(String estadoProyecto, String nombre) {
        return new EstadoProyectoGradoReadModel(UtilUUID.generarNuevoUUID(), estadoProyecto, nombre, Instant.now());
    }

    @Test
    void debeConsultarEnOrden_cuandoProyectoExisteYEstudianteVinculado() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var estudiante = UtilUUID.generarNuevoUUID();
        var criteria = new EstadoProyectoGradoEstudianteCriteria(proyectoGrado, estudiante);
        var estados = List.of(estado("FINALIZADO", "Finalizado"), estado("ATRASADO", "Atrasado"),
                estado("EN_PROCESO", "En proceso"));
        when(proyectoGradoExisteQueryFinder.obtener(proyectoGrado)).thenReturn(true);
        when(estudianteVinculadoEstadoProyectoGradoQueryFinder.obtener(criteria)).thenReturn(true);
        when(estadoProyectoGradoQueryOutputPort.consultarPorProyecto(proyectoGrado)).thenReturn(estados);

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isSameAs(estados);
        var orden = inOrder(proyectoGradoExisteQueryFinder, estudianteVinculadoEstadoProyectoGradoQueryFinder,
                validator, estadoProyectoGradoQueryOutputPort);
        orden.verify(proyectoGradoExisteQueryFinder).obtener(proyectoGrado);
        orden.verify(estudianteVinculadoEstadoProyectoGradoQueryFinder).obtener(criteria);
        orden.verify(validator).validar(criteria, true, true);
        orden.verify(estadoProyectoGradoQueryOutputPort).consultarPorProyecto(proyectoGrado);
        verify(proyectoGradoExisteQueryFinder, times(1)).obtener(any());
        verify(estudianteVinculadoEstadoProyectoGradoQueryFinder, times(1)).obtener(any());
        verify(estadoProyectoGradoQueryOutputPort, times(1)).consultarPorProyecto(any());
        verify(logger).debug(EstadoProyectoGradoKey.LOG_CONSULTANDO, proyectoGrado, estudiante);
        verify(logger).debug(EstadoProyectoGradoKey.LOG_CONSULTA_COMPLETADA, 3);
    }
}
