package com.arquisoft.mapas_ruta.application.maparuta.query.usecase.impl;

import com.arquisoft.mapas_ruta.application.maparuta.query.criteria.MapaRutaProyectoGradoCriteria;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarMapaRutaProyectoGradoAsesorUseCaseImplTest {

    @Mock
    private MapaRutaEstudianteQueryOutputPort mapaRutaEstudianteQueryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarMapaRutaProyectoGradoAsesorUseCaseImpl useCase;

    @Test
    void debeRetornarElMapa_cuandoElProyectoTieneMapa() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var readModel = new MapaRutaEstudianteReadModel(UtilUUID.generarNuevoUUID(), proyectoGrado,
                "Titulo", LocalDate.of(2026, 10, 1), LocalDate.of(2026, 12, 1));
        when(mapaRutaEstudianteQueryOutputPort.consultarPorProyectoGrado(proyectoGrado))
                .thenReturn(Optional.of(readModel));

        // Act
        var resultado = useCase.ejecutar(new MapaRutaProyectoGradoCriteria(proyectoGrado));

        // Assert
        assertThat(resultado).containsSame(readModel);
        var orden = inOrder(logger, mapaRutaEstudianteQueryOutputPort);
        orden.verify(logger).debug(MapaRutaKey.LOG_CONSULTANDO_PROYECTO_ASESOR, proyectoGrado);
        orden.verify(mapaRutaEstudianteQueryOutputPort).consultarPorProyectoGrado(proyectoGrado);
        orden.verify(logger).debug(MapaRutaKey.LOG_CONSULTA_PROYECTO_ASESOR_COMPLETADA, true);
        verify(mapaRutaEstudianteQueryOutputPort, times(1)).consultarPorProyectoGrado(proyectoGrado);
    }

    @Test
    void debeRetornarVacio_cuandoElProyectoNoTieneMapa() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        when(mapaRutaEstudianteQueryOutputPort.consultarPorProyectoGrado(proyectoGrado))
                .thenReturn(Optional.empty());

        // Act
        var resultado = useCase.ejecutar(new MapaRutaProyectoGradoCriteria(proyectoGrado));

        // Assert
        assertThat(resultado).isEmpty();
        verify(logger).debug(MapaRutaKey.LOG_CONSULTA_PROYECTO_ASESOR_COMPLETADA, false);
    }
}
