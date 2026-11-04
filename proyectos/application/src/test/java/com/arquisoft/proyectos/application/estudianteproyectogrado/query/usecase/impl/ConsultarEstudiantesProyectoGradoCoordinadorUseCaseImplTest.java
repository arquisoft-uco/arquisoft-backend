package com.arquisoft.proyectos.application.estudianteproyectogrado.query.usecase.impl;

import com.arquisoft.proyectos.application.estudianteproyectogrado.query.criteria.EstudianteProyectoGradoCoordinadorCriteria;
import com.arquisoft.proyectos.application.estudianteproyectogrado.query.readmodel.EstudianteProyectoGradoReadModel;
import com.arquisoft.proyectos.application.estudianteproyectogrado.query.secondaryport.EstudianteProyectoGradoQueryOutputPort;
import com.arquisoft.proyectos.application.estudianteproyectogrado.query.validator.ConsultarEstudiantesProyectoGradoCoordinadorValidator;
import com.arquisoft.proyectos.application.proyectogrado.query.finder.ProyectoGradoCoordinadorQueryFinder;
import com.arquisoft.proyectos.domain.proyectogrado.exception.ProyectoGradoNoPerteneceCoordinadorException;
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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarEstudiantesProyectoGradoCoordinadorUseCaseImplTest {

    @Mock
    private ProyectoGradoCoordinadorQueryFinder proyectoGradoCoordinadorQueryFinder;

    @Mock
    private ConsultarEstudiantesProyectoGradoCoordinadorValidator validator;

    @Mock
    private EstudianteProyectoGradoQueryOutputPort outputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarEstudiantesProyectoGradoCoordinadorUseCaseImpl useCase;

    @Test
    void debeRetornarEstudiantes_cuandoValidacionPasa() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var coordinador = UtilUUID.generarNuevoUUID();
        var criteria = new EstudianteProyectoGradoCoordinadorCriteria(proyectoGrado, coordinador);
        var estudiantes = List.of(
                new EstudianteProyectoGradoReadModel(UtilUUID.generarNuevoUUID(), proyectoGrado,
                        UtilUUID.generarNuevoUUID(), "2020", "Ana", "ana@soyuco.edu.co", true),
                new EstudianteProyectoGradoReadModel(UtilUUID.generarNuevoUUID(), proyectoGrado,
                        UtilUUID.generarNuevoUUID(), "2021", "Beto", "beto@soyuco.edu.co", false),
                new EstudianteProyectoGradoReadModel(UtilUUID.generarNuevoUUID(), proyectoGrado,
                        UtilUUID.generarNuevoUUID(), "2022", "Carla", "carla@soyuco.edu.co", true));
        when(proyectoGradoCoordinadorQueryFinder.obtener(proyectoGrado)).thenReturn(coordinador);
        when(outputPort.consultarPorProyecto(proyectoGrado)).thenReturn(estudiantes);

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isSameAs(estudiantes);
        var orden = inOrder(proyectoGradoCoordinadorQueryFinder, validator, outputPort);
        orden.verify(proyectoGradoCoordinadorQueryFinder).obtener(proyectoGrado);
        orden.verify(validator).validar(proyectoGrado, coordinador, coordinador);
        orden.verify(outputPort).consultarPorProyecto(proyectoGrado);
        verify(proyectoGradoCoordinadorQueryFinder, times(1)).obtener(any());
        verify(outputPort, times(1)).consultarPorProyecto(any());
        verify(logger).debug(EstudianteProyectoGradoKey.LOG_CONSULTANDO, proyectoGrado, coordinador);
        verify(logger).debug(EstudianteProyectoGradoKey.LOG_CONSULTA_COMPLETADA, 3);
    }

    @Test
    void debeNoLeerEstudiantes_cuandoValidatorRechaza() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var solicitante = UtilUUID.generarNuevoUUID();
        var duenio = UtilUUID.generarNuevoUUID();
        var criteria = new EstudianteProyectoGradoCoordinadorCriteria(proyectoGrado, solicitante);
        when(proyectoGradoCoordinadorQueryFinder.obtener(proyectoGrado)).thenReturn(duenio);
        doThrow(new ProyectoGradoNoPerteneceCoordinadorException(proyectoGrado, solicitante))
                .when(validator).validar(proyectoGrado, duenio, solicitante);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(criteria))
                .isInstanceOf(ProyectoGradoNoPerteneceCoordinadorException.class);
        verify(outputPort, never()).consultarPorProyecto(any());
    }
}
