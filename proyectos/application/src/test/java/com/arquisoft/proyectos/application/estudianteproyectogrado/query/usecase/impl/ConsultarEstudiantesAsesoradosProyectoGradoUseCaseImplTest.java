package com.arquisoft.proyectos.application.estudianteproyectogrado.query.usecase.impl;

import com.arquisoft.proyectos.application.asesorproyectogrado.query.finder.AsesorAsignadoQueryFinder;
import com.arquisoft.proyectos.application.asesorproyectogrado.query.finder.model.AsignacionAsesorConsultada;
import com.arquisoft.proyectos.application.estudianteproyectogrado.query.criteria.EstudianteAsesoradoProyectoGradoCriteria;
import com.arquisoft.proyectos.application.estudianteproyectogrado.query.readmodel.EstudianteAsesoradoProyectoGradoReadModel;
import com.arquisoft.proyectos.application.estudianteproyectogrado.query.secondaryport.EstudianteProyectoGradoQueryOutputPort;
import com.arquisoft.proyectos.application.estudianteproyectogrado.query.validator.ConsultarEstudiantesAsesoradosProyectoGradoValidator;
import com.arquisoft.proyectos.application.proyectogrado.query.finder.ProyectoGradoExisteQueryFinder;
import com.arquisoft.proyectos.domain.asesorproyectogrado.exception.AsesorProyectoGradoNoEncontradoException;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.proyectos.EstudianteProyectoGradoKey;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

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
class ConsultarEstudiantesAsesoradosProyectoGradoUseCaseImplTest {

    @Mock
    private ProyectoGradoExisteQueryFinder proyectoGradoExisteQueryFinder;

    @Mock
    private AsesorAsignadoQueryFinder asesorAsignadoQueryFinder;

    @Mock
    private ConsultarEstudiantesAsesoradosProyectoGradoValidator validator;

    @Mock
    private EstudianteProyectoGradoQueryOutputPort outputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarEstudiantesAsesoradosProyectoGradoUseCaseImpl useCase;

    @Test
    void debeRetornarEstudiantes_cuandoAsesorAsignado() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var criteria = new EstudianteAsesoradoProyectoGradoCriteria(proyectoGrado, asesor);
        var entradaFinder = new AsignacionAsesorConsultada(asesor, proyectoGrado);
        var estudiantes = List.of(
                new EstudianteAsesoradoProyectoGradoReadModel(UtilUUID.generarNuevoUUID(), "2020", "Ana",
                        "ana@soyuco.edu.co"),
                new EstudianteAsesoradoProyectoGradoReadModel(UtilUUID.generarNuevoUUID(), "2021", "Beto",
                        "beto@soyuco.edu.co"),
                new EstudianteAsesoradoProyectoGradoReadModel(UtilUUID.generarNuevoUUID(), "2022", "Carla",
                        "carla@soyuco.edu.co"));
        when(proyectoGradoExisteQueryFinder.obtener(proyectoGrado)).thenReturn(true);
        when(asesorAsignadoQueryFinder.obtener(entradaFinder)).thenReturn(true);
        when(outputPort.consultarAsesoradosVigentesPorProyecto(proyectoGrado)).thenReturn(estudiantes);

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isSameAs(estudiantes);
        var orden = inOrder(proyectoGradoExisteQueryFinder, asesorAsignadoQueryFinder, validator, outputPort);
        orden.verify(proyectoGradoExisteQueryFinder).obtener(proyectoGrado);
        orden.verify(asesorAsignadoQueryFinder).obtener(entradaFinder);
        orden.verify(validator).validar(criteria, true, true);
        orden.verify(outputPort).consultarAsesoradosVigentesPorProyecto(proyectoGrado);
        verify(proyectoGradoExisteQueryFinder, times(1)).obtener(any());
        verify(asesorAsignadoQueryFinder, times(1)).obtener(any());
        verify(outputPort, times(1)).consultarAsesoradosVigentesPorProyecto(any());
        verify(logger).debug(EstudianteProyectoGradoKey.LOG_CONSULTANDO_ASESORADOS, proyectoGrado, asesor);
        verify(logger).debug(EstudianteProyectoGradoKey.LOG_CONSULTA_ASESORADOS_COMPLETADA, 3);
    }

    @Test
    void debeLanzarExcepcion_cuandoValidatorRechaza() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var criteria = new EstudianteAsesoradoProyectoGradoCriteria(proyectoGrado, asesor);
        when(proyectoGradoExisteQueryFinder.obtener(proyectoGrado)).thenReturn(true);
        when(asesorAsignadoQueryFinder.obtener(new AsignacionAsesorConsultada(asesor, proyectoGrado)))
                .thenReturn(false);
        doThrow(new AsesorProyectoGradoNoEncontradoException(asesor, proyectoGrado))
                .when(validator).validar(criteria, true, false);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(criteria))
                .isInstanceOf(AsesorProyectoGradoNoEncontradoException.class);
        verify(outputPort, never()).consultarAsesoradosVigentesPorProyecto(any());
        verify(logger, never()).debug(eq(EstudianteProyectoGradoKey.LOG_CONSULTA_ASESORADOS_COMPLETADA), any());
    }
}
