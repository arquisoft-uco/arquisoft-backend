package com.arquisoft.proyectos.application.proyectogrado.query.usecase.impl;

import com.arquisoft.proyectos.application.estadoproyecto.query.readmodel.EstadoProyectoReadModel;
import com.arquisoft.proyectos.application.proyectogrado.query.criteria.ProyectoGradoEstudianteCriteria;
import com.arquisoft.proyectos.application.proyectogrado.query.readmodel.ProyectoGradoReadModel;
import com.arquisoft.proyectos.application.proyectogrado.query.secondaryport.ProyectoGradoQueryOutputPort;
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
class ConsultarProyectosGradoEstudianteUseCaseImplTest {

    @Mock
    private ProyectoGradoQueryOutputPort proyectoGradoQueryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarProyectosGradoEstudianteUseCaseImpl useCase;

    @Test
    void debeRetornarProyectos_cuandoPuertoDevuelveResultados() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        var criteria = new ProyectoGradoEstudianteCriteria(estudiante);
        var estado = new EstadoProyectoReadModel("EN_PROCESO", "En proceso");
        var proyectos = List.of(
                new ProyectoGradoReadModel(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), "A", estado),
                new ProyectoGradoReadModel(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), "B", estado));
        when(proyectoGradoQueryOutputPort.consultarPorEstudiante(criteria)).thenReturn(proyectos);

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isSameAs(proyectos);
        verify(proyectoGradoQueryOutputPort, times(1)).consultarPorEstudiante(criteria);
        var orden = inOrder(logger, proyectoGradoQueryOutputPort);
        orden.verify(logger).debug(eq(ProyectoGradoKey.LOG_CONSULTANDO_ESTUDIANTE), eq(estudiante));
        orden.verify(proyectoGradoQueryOutputPort).consultarPorEstudiante(criteria);
        orden.verify(logger).debug(eq(ProyectoGradoKey.LOG_CONSULTA_ESTUDIANTE_COMPLETADA), eq(2));
    }

    @Test
    void debeRetornarVacio_cuandoPuertoNoDevuelveNada() {
        // Arrange
        var criteria = new ProyectoGradoEstudianteCriteria(UtilUUID.generarNuevoUUID());
        when(proyectoGradoQueryOutputPort.consultarPorEstudiante(criteria)).thenReturn(List.of());

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isEmpty();
        verify(logger).debug(eq(ProyectoGradoKey.LOG_CONSULTA_ESTUDIANTE_COMPLETADA), eq(0));
    }
}
