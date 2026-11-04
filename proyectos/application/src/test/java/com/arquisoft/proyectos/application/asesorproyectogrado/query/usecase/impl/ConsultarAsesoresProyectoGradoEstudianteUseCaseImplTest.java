package com.arquisoft.proyectos.application.asesorproyectogrado.query.usecase.impl;

import com.arquisoft.proyectos.application.asesorproyectogrado.query.criteria.AsesorProyectoGradoEstudianteCriteria;
import com.arquisoft.proyectos.application.asesorproyectogrado.query.finder.EstudianteVinculadoProyectoGradoQueryFinder;
import com.arquisoft.proyectos.application.asesorproyectogrado.query.readmodel.AsesorProyectoGradoReadModel;
import com.arquisoft.proyectos.application.asesorproyectogrado.query.secondaryport.AsesorProyectoGradoQueryOutputPort;
import com.arquisoft.proyectos.application.asesorproyectogrado.query.validator.ConsultarAsesoresProyectoGradoEstudianteValidator;
import com.arquisoft.proyectos.application.proyectogrado.query.finder.ProyectoGradoExisteQueryFinder;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.exception.EstudianteNoVinculadoException;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.proyectos.AsesorProyectoGradoKey;
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
class ConsultarAsesoresProyectoGradoEstudianteUseCaseImplTest {

    @Mock
    private ProyectoGradoExisteQueryFinder proyectoGradoExisteQueryFinder;

    @Mock
    private EstudianteVinculadoProyectoGradoQueryFinder estudianteVinculadoProyectoGradoQueryFinder;

    @Mock
    private ConsultarAsesoresProyectoGradoEstudianteValidator validator;

    @Mock
    private AsesorProyectoGradoQueryOutputPort asesorProyectoGradoQueryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarAsesoresProyectoGradoEstudianteUseCaseImpl useCase;

    private AsesorProyectoGradoReadModel asesor(UUID proyectoGrado) {
        return new AsesorProyectoGradoReadModel(UtilUUID.generarNuevoUUID(), proyectoGrado,
                UtilUUID.generarNuevoUUID(), "1001", "Ana", "ana@soyuco.edu.co", "ASIGNADO", "Asignado",
                Instant.now(), true, true);
    }

    @Test
    void debeConsultarEnOrden_cuandoAccesoValido() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var estudiante = UtilUUID.generarNuevoUUID();
        var criteria = new AsesorProyectoGradoEstudianteCriteria(proyectoGrado, estudiante);
        var asesores = List.of(asesor(proyectoGrado), asesor(proyectoGrado), asesor(proyectoGrado));
        when(proyectoGradoExisteQueryFinder.obtener(proyectoGrado)).thenReturn(true);
        when(estudianteVinculadoProyectoGradoQueryFinder.obtener(criteria)).thenReturn(true);
        when(asesorProyectoGradoQueryOutputPort.consultarPorProyecto(proyectoGrado)).thenReturn(asesores);

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isSameAs(asesores);
        var orden = inOrder(proyectoGradoExisteQueryFinder, estudianteVinculadoProyectoGradoQueryFinder, validator,
                asesorProyectoGradoQueryOutputPort);
        orden.verify(proyectoGradoExisteQueryFinder).obtener(proyectoGrado);
        orden.verify(estudianteVinculadoProyectoGradoQueryFinder).obtener(criteria);
        orden.verify(validator).validar(criteria, true, true);
        orden.verify(asesorProyectoGradoQueryOutputPort).consultarPorProyecto(proyectoGrado);
        verify(proyectoGradoExisteQueryFinder, times(1)).obtener(any());
        verify(estudianteVinculadoProyectoGradoQueryFinder, times(1)).obtener(any());
        verify(asesorProyectoGradoQueryOutputPort, times(1)).consultarPorProyecto(any());
        verify(logger).debug(AsesorProyectoGradoKey.LOG_CONSULTANDO_ASIGNADOS, proyectoGrado, estudiante);
        verify(logger).debug(AsesorProyectoGradoKey.LOG_CONSULTA_COMPLETADA, 3);
    }

    @Test
    void debeNoLeerAsesores_cuandoValidatorRechaza() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var estudiante = UtilUUID.generarNuevoUUID();
        var criteria = new AsesorProyectoGradoEstudianteCriteria(proyectoGrado, estudiante);
        when(proyectoGradoExisteQueryFinder.obtener(proyectoGrado)).thenReturn(true);
        when(estudianteVinculadoProyectoGradoQueryFinder.obtener(criteria)).thenReturn(false);
        doThrow(new EstudianteNoVinculadoException(estudiante, proyectoGrado))
                .when(validator).validar(criteria, true, false);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(criteria))
                .isInstanceOf(EstudianteNoVinculadoException.class);
        verify(asesorProyectoGradoQueryOutputPort, never()).consultarPorProyecto(any());
        verify(logger, never()).debug(eq(AsesorProyectoGradoKey.LOG_CONSULTA_COMPLETADA), any());
    }
}
