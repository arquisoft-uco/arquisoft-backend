package com.arquisoft.proyectos.application.proyectogrado.query.usecase.impl;

import com.arquisoft.proyectos.application.asesorproyectogrado.query.finder.AsesorAsignadoQueryFinder;
import com.arquisoft.proyectos.application.asesorproyectogrado.query.finder.model.AsignacionAsesorConsultada;
import com.arquisoft.proyectos.application.estadoproyecto.query.readmodel.EstadoProyectoReadModel;
import com.arquisoft.proyectos.application.proyectogrado.query.criteria.DetalleProyectoGradoAsesorCriteria;
import com.arquisoft.proyectos.application.proyectogrado.query.finder.ProyectoGradoExisteQueryFinder;
import com.arquisoft.proyectos.application.proyectogrado.query.readmodel.ProyectoGradoReadModel;
import com.arquisoft.proyectos.application.proyectogrado.query.secondaryport.ProyectoGradoQueryOutputPort;
import com.arquisoft.proyectos.application.proyectogrado.query.validator.ConsultarDetalleProyectoGradoAsesorValidator;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.proyectos.ProyectoGradoKey;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarDetalleProyectoGradoAsesorUseCaseImplTest {

    @Mock
    private ProyectoGradoExisteQueryFinder proyectoGradoExisteQueryFinder;

    @Mock
    private AsesorAsignadoQueryFinder asesorAsignadoQueryFinder;

    @Mock
    private ConsultarDetalleProyectoGradoAsesorValidator validator;

    @Mock
    private ProyectoGradoQueryOutputPort proyectoGradoQueryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarDetalleProyectoGradoAsesorUseCaseImpl useCase;

    @Test
    void debeRetornarProyecto_cuandoAsesorAsignado() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var criteria = new DetalleProyectoGradoAsesorCriteria(proyectoGrado, asesor);
        var entradaFinder = new AsignacionAsesorConsultada(asesor, proyectoGrado);
        var proyecto = new ProyectoGradoReadModel(proyectoGrado, UtilUUID.generarNuevoUUID(), "Titulo",
                new EstadoProyectoReadModel("EN_PROCESO", "En proceso"));
        when(proyectoGradoExisteQueryFinder.obtener(proyectoGrado)).thenReturn(true);
        when(asesorAsignadoQueryFinder.obtener(entradaFinder)).thenReturn(true);
        when(proyectoGradoQueryOutputPort.consultarPorId(proyectoGrado)).thenReturn(proyecto);

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isSameAs(proyecto);
        var orden = inOrder(proyectoGradoExisteQueryFinder, asesorAsignadoQueryFinder, validator,
                proyectoGradoQueryOutputPort);
        orden.verify(proyectoGradoExisteQueryFinder).obtener(proyectoGrado);
        orden.verify(asesorAsignadoQueryFinder).obtener(entradaFinder);
        orden.verify(validator).validar(criteria, true, true);
        orden.verify(proyectoGradoQueryOutputPort).consultarPorId(proyectoGrado);
        verify(proyectoGradoExisteQueryFinder, times(1)).obtener(any());
        verify(asesorAsignadoQueryFinder, times(1)).obtener(any());
        verify(proyectoGradoQueryOutputPort, times(1)).consultarPorId(any());
        verify(logger).debug(ProyectoGradoKey.LOG_CONSULTANDO_DETALLE_ASESOR, proyectoGrado, asesor);
        verify(logger).debug(ProyectoGradoKey.LOG_CONSULTA_DETALLE_ASESOR_COMPLETADA, proyectoGrado);
    }
}
