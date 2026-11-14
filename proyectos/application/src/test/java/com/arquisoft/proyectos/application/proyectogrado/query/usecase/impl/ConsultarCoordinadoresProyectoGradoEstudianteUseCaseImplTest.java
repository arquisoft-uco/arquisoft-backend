package com.arquisoft.proyectos.application.proyectogrado.query.usecase.impl;

import com.arquisoft.proyectos.application.proyectogrado.query.criteria.CoordinadorProyectoGradoEstudianteCriteria;
import com.arquisoft.proyectos.application.proyectogrado.query.readmodel.CoordinadorProyectoGradoReadModel;
import com.arquisoft.proyectos.application.proyectogrado.query.secondaryport.CoordinadorProyectoGradoQueryOutputPort;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.proyectos.ProyectoGradoKey;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarCoordinadoresProyectoGradoEstudianteUseCaseImplTest {

    @Mock
    private CoordinadorProyectoGradoQueryOutputPort coordinadorProyectoGradoQueryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarCoordinadoresProyectoGradoEstudianteUseCaseImpl useCase;

    @Test
    void debeRetornarLosCoordinadoresDelPuerto_cuandoHayResultados() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        var criteria = new CoordinadorProyectoGradoEstudianteCriteria(estudiante);
        var coordinadores = List.of(
                new CoordinadorProyectoGradoReadModel(UtilUUID.generarNuevoUUID(), "A",
                        UtilUUID.generarNuevoUUID(), "1001", "Ana", "ana@uco.edu.co"),
                new CoordinadorProyectoGradoReadModel(UtilUUID.generarNuevoUUID(), "B",
                        UtilUUID.generarNuevoUUID(), "1002", "Beto", "beto@uco.edu.co"),
                new CoordinadorProyectoGradoReadModel(UtilUUID.generarNuevoUUID(), "C",
                        UtilUUID.generarNuevoUUID(), "1003", "Carla", "carla@uco.edu.co"));
        when(coordinadorProyectoGradoQueryOutputPort.consultarPorEstudiante(criteria)).thenReturn(coordinadores);

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isSameAs(coordinadores);
        verify(coordinadorProyectoGradoQueryOutputPort, times(1)).consultarPorEstudiante(criteria);
        var orden = inOrder(logger, coordinadorProyectoGradoQueryOutputPort);
        orden.verify(logger).debug(eq(ProyectoGradoKey.LOG_CONSULTANDO_COORDINADORES), eq(estudiante));
        orden.verify(coordinadorProyectoGradoQueryOutputPort).consultarPorEstudiante(criteria);
        orden.verify(logger).debug(eq(ProyectoGradoKey.LOG_CONSULTA_COORDINADORES_COMPLETADA), eq(3));
    }

    @Test
    void debeRetornarListaVacia_cuandoElPuertoNoDevuelveNada() {
        // Arrange
        var criteria = new CoordinadorProyectoGradoEstudianteCriteria(UtilUUID.generarNuevoUUID());
        when(coordinadorProyectoGradoQueryOutputPort.consultarPorEstudiante(criteria)).thenReturn(List.of());

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isEmpty();
        verify(logger).debug(eq(ProyectoGradoKey.LOG_CONSULTA_COORDINADORES_COMPLETADA), eq(0));
    }
}
