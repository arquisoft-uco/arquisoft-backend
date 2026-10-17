package com.arquisoft.mapas_ruta.application.maparuta.query.usecase.impl;

import com.arquisoft.mapas_ruta.application.asignacionproyecto.query.finder.ProyectoGradoDeEstudianteQueryFinder;
import com.arquisoft.mapas_ruta.application.maparuta.query.criteria.MapaRutaEstudianteCriteria;
import com.arquisoft.mapas_ruta.application.maparuta.query.readmodel.MapaRutaEstudianteReadModel;
import com.arquisoft.mapas_ruta.application.maparuta.query.secondaryport.MapaRutaEstudianteQueryOutputPort;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.mapas_ruta.MapaRutaKey;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarMapaRutaEstudianteUseCaseImplTest {

    @Mock
    private ProyectoGradoDeEstudianteQueryFinder proyectoGradoDeEstudianteQueryFinder;

    @Mock
    private MapaRutaEstudianteQueryOutputPort mapaRutaEstudianteQueryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarMapaRutaEstudianteUseCaseImpl useCase;

    @Test
    void debeRetornarElMapa_cuandoElEstudianteTieneProyectoConMapa() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var readModel = new MapaRutaEstudianteReadModel(UtilUUID.generarNuevoUUID(), proyectoGrado,
                "Titulo", LocalDate.of(2026, 10, 1), LocalDate.of(2026, 12, 1));
        when(proyectoGradoDeEstudianteQueryFinder.obtener(estudiante)).thenReturn(proyectoGrado);
        when(mapaRutaEstudianteQueryOutputPort.consultarPorProyectoGrado(proyectoGrado))
                .thenReturn(Optional.of(readModel));

        // Act
        var resultado = useCase.ejecutar(new MapaRutaEstudianteCriteria(estudiante));

        // Assert
        assertThat(resultado).containsSame(readModel);
        var orden = inOrder(logger, proyectoGradoDeEstudianteQueryFinder, mapaRutaEstudianteQueryOutputPort);
        orden.verify(logger).debug(MapaRutaKey.LOG_CONSULTANDO_ESTUDIANTE, estudiante);
        orden.verify(proyectoGradoDeEstudianteQueryFinder).obtener(estudiante);
        orden.verify(mapaRutaEstudianteQueryOutputPort).consultarPorProyectoGrado(proyectoGrado);
        orden.verify(logger).debug(MapaRutaKey.LOG_CONSULTA_ESTUDIANTE_COMPLETADA, true, true);
        verify(proyectoGradoDeEstudianteQueryFinder, times(1)).obtener(estudiante);
    }

    @Test
    void debeRetornarVacioSinConsultarElMapa_cuandoElEstudianteNoTieneProyecto() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        when(proyectoGradoDeEstudianteQueryFinder.obtener(estudiante)).thenReturn(UtilUUID.obtenerUUIDPorDefecto());

        // Act
        var resultado = useCase.ejecutar(new MapaRutaEstudianteCriteria(estudiante));

        // Assert
        assertThat(resultado).isEmpty();
        verifyNoInteractions(mapaRutaEstudianteQueryOutputPort);
        verify(logger).debug(MapaRutaKey.LOG_CONSULTA_ESTUDIANTE_COMPLETADA, false, false);
    }

    @Test
    void debeRetornarVacio_cuandoElProyectoNoTieneMapa() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        when(proyectoGradoDeEstudianteQueryFinder.obtener(estudiante)).thenReturn(proyectoGrado);
        when(mapaRutaEstudianteQueryOutputPort.consultarPorProyectoGrado(proyectoGrado))
                .thenReturn(Optional.empty());

        // Act
        var resultado = useCase.ejecutar(new MapaRutaEstudianteCriteria(estudiante));

        // Assert
        assertThat(resultado).isEmpty();
        verify(mapaRutaEstudianteQueryOutputPort).consultarPorProyectoGrado(proyectoGrado);
        verify(logger).debug(MapaRutaKey.LOG_CONSULTA_ESTUDIANTE_COMPLETADA, true, false);
    }
}
