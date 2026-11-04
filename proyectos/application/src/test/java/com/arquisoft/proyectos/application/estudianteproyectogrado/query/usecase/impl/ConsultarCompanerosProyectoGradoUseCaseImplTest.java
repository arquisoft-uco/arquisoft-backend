package com.arquisoft.proyectos.application.estudianteproyectogrado.query.usecase.impl;

import com.arquisoft.proyectos.application.estudianteproyectogrado.query.criteria.CompaneroProyectoGradoCriteria;
import com.arquisoft.proyectos.application.estudianteproyectogrado.query.finder.EstudianteVinculadoVigenteQueryFinder;
import com.arquisoft.proyectos.application.estudianteproyectogrado.query.readmodel.CompaneroProyectoGradoReadModel;
import com.arquisoft.proyectos.application.estudianteproyectogrado.query.secondaryport.EstudianteProyectoGradoQueryOutputPort;
import com.arquisoft.proyectos.application.estudianteproyectogrado.query.validator.ConsultarCompanerosProyectoGradoValidator;
import com.arquisoft.proyectos.application.proyectogrado.query.finder.ProyectoGradoExisteQueryFinder;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.exception.EstudianteNoVinculadoException;
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
class ConsultarCompanerosProyectoGradoUseCaseImplTest {

    @Mock
    private ProyectoGradoExisteQueryFinder proyectoGradoExisteQueryFinder;

    @Mock
    private EstudianteVinculadoVigenteQueryFinder estudianteVinculadoVigenteQueryFinder;

    @Mock
    private ConsultarCompanerosProyectoGradoValidator validator;

    @Mock
    private EstudianteProyectoGradoQueryOutputPort outputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarCompanerosProyectoGradoUseCaseImpl useCase;

    @Test
    void debeValidarYConsultar_enOrden() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var estudiante = UtilUUID.generarNuevoUUID();
        var criteria = new CompaneroProyectoGradoCriteria(proyectoGrado, estudiante);
        var companeros = List.of(
                new CompaneroProyectoGradoReadModel(estudiante, "2020", "Ana", "ana@soyuco.edu.co"),
                new CompaneroProyectoGradoReadModel(UtilUUID.generarNuevoUUID(), "2021", "Beto", "beto@soyuco.edu.co"),
                new CompaneroProyectoGradoReadModel(UtilUUID.generarNuevoUUID(), "2022", "Carla", "carla@soyuco.edu.co"));
        when(proyectoGradoExisteQueryFinder.obtener(proyectoGrado)).thenReturn(true);
        when(estudianteVinculadoVigenteQueryFinder.obtener(criteria)).thenReturn(true);
        when(outputPort.consultarCompanerosVigentesPorProyecto(proyectoGrado)).thenReturn(companeros);

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isSameAs(companeros);
        var orden = inOrder(proyectoGradoExisteQueryFinder, estudianteVinculadoVigenteQueryFinder, validator, outputPort);
        orden.verify(proyectoGradoExisteQueryFinder).obtener(proyectoGrado);
        orden.verify(estudianteVinculadoVigenteQueryFinder).obtener(criteria);
        orden.verify(validator).validar(criteria, true, true);
        orden.verify(outputPort).consultarCompanerosVigentesPorProyecto(proyectoGrado);
        verify(proyectoGradoExisteQueryFinder, times(1)).obtener(any());
        verify(estudianteVinculadoVigenteQueryFinder, times(1)).obtener(any());
        verify(outputPort, times(1)).consultarCompanerosVigentesPorProyecto(any());
        verify(logger).debug(EstudianteProyectoGradoKey.LOG_CONSULTANDO_COMPANEROS, proyectoGrado, estudiante);
        verify(logger).debug(EstudianteProyectoGradoKey.LOG_CONSULTA_COMPANEROS_COMPLETADA, 3);
    }

    @Test
    void debeNoLeerCompaneros_cuandoValidatorRechaza() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var estudiante = UtilUUID.generarNuevoUUID();
        var criteria = new CompaneroProyectoGradoCriteria(proyectoGrado, estudiante);
        when(proyectoGradoExisteQueryFinder.obtener(proyectoGrado)).thenReturn(true);
        when(estudianteVinculadoVigenteQueryFinder.obtener(criteria)).thenReturn(false);
        doThrow(new EstudianteNoVinculadoException(estudiante, proyectoGrado))
                .when(validator).validar(criteria, true, false);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(criteria))
                .isInstanceOf(EstudianteNoVinculadoException.class);
        verify(outputPort, never()).consultarCompanerosVigentesPorProyecto(any());
        verify(logger, never()).debug(eq(EstudianteProyectoGradoKey.LOG_CONSULTA_COMPANEROS_COMPLETADA), any());
    }
}
