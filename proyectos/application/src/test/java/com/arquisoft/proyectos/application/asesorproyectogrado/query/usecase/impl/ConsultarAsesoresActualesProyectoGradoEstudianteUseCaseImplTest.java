package com.arquisoft.proyectos.application.asesorproyectogrado.query.usecase.impl;

import com.arquisoft.proyectos.application.asesorproyectogrado.query.criteria.AsesorActualProyectoGradoEstudianteCriteria;
import com.arquisoft.proyectos.application.asesorproyectogrado.query.readmodel.AsesorActualProyectoGradoReadModel;
import com.arquisoft.proyectos.application.asesorproyectogrado.query.secondaryport.AsesorActualProyectoGradoQueryOutputPort;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.proyectos.AsesorProyectoGradoKey;
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
class ConsultarAsesoresActualesProyectoGradoEstudianteUseCaseImplTest {

    @Mock
    private AsesorActualProyectoGradoQueryOutputPort asesorActualProyectoGradoQueryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarAsesoresActualesProyectoGradoEstudianteUseCaseImpl useCase;

    @Test
    void debeRetornarLosAsesoresDelPuerto_cuandoHayResultados() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        var criteria = new AsesorActualProyectoGradoEstudianteCriteria(estudiante);
        var asesores = List.of(
                new AsesorActualProyectoGradoReadModel(UtilUUID.generarNuevoUUID(), "A",
                        UtilUUID.generarNuevoUUID(), "1001", "Ana", "ana@soyuco.edu.co"),
                new AsesorActualProyectoGradoReadModel(UtilUUID.generarNuevoUUID(), "B",
                        UtilUUID.generarNuevoUUID(), "1002", "Beto", "beto@soyuco.edu.co"),
                new AsesorActualProyectoGradoReadModel(UtilUUID.generarNuevoUUID(), "C",
                        UtilUUID.generarNuevoUUID(), "1003", "Carla", "carla@soyuco.edu.co"));
        when(asesorActualProyectoGradoQueryOutputPort.consultarPorEstudiante(criteria)).thenReturn(asesores);

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isSameAs(asesores);
        verify(asesorActualProyectoGradoQueryOutputPort, times(1)).consultarPorEstudiante(criteria);
        var orden = inOrder(logger, asesorActualProyectoGradoQueryOutputPort);
        orden.verify(logger).debug(eq(AsesorProyectoGradoKey.LOG_CONSULTANDO_ACTUALES), eq(estudiante));
        orden.verify(asesorActualProyectoGradoQueryOutputPort).consultarPorEstudiante(criteria);
        orden.verify(logger).debug(eq(AsesorProyectoGradoKey.LOG_CONSULTA_ACTUALES_COMPLETADA), eq(3));
    }

    @Test
    void debeRetornarListaVacia_cuandoElPuertoNoDevuelveNada() {
        // Arrange
        var criteria = new AsesorActualProyectoGradoEstudianteCriteria(UtilUUID.generarNuevoUUID());
        when(asesorActualProyectoGradoQueryOutputPort.consultarPorEstudiante(criteria)).thenReturn(List.of());

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isEmpty();
        verify(logger).debug(eq(AsesorProyectoGradoKey.LOG_CONSULTA_ACTUALES_COMPLETADA), eq(0));
    }
}
