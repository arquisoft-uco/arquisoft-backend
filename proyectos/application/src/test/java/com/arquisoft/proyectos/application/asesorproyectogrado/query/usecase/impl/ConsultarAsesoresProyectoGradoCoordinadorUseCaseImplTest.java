package com.arquisoft.proyectos.application.asesorproyectogrado.query.usecase.impl;

import com.arquisoft.proyectos.application.asesorproyectogrado.query.criteria.AsesorProyectoGradoCoordinadorCriteria;
import com.arquisoft.proyectos.application.asesorproyectogrado.query.readmodel.AsesorProyectoGradoReadModel;
import com.arquisoft.proyectos.application.asesorproyectogrado.query.secondaryport.AsesorProyectoGradoQueryOutputPort;
import com.arquisoft.proyectos.application.asesorproyectogrado.query.validator.ConsultarAsesoresProyectoGradoCoordinadorValidator;
import com.arquisoft.proyectos.application.proyectogrado.query.finder.ProyectoGradoCoordinadorQueryFinder;
import com.arquisoft.proyectos.domain.proyectogrado.exception.ProyectoGradoNoPerteneceCoordinadorException;
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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarAsesoresProyectoGradoCoordinadorUseCaseImplTest {

    @Mock
    private ProyectoGradoCoordinadorQueryFinder proyectoGradoCoordinadorQueryFinder;

    @Mock
    private ConsultarAsesoresProyectoGradoCoordinadorValidator validator;

    @Mock
    private AsesorProyectoGradoQueryOutputPort outputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarAsesoresProyectoGradoCoordinadorUseCaseImpl useCase;

    private AsesorProyectoGradoReadModel asesor(UUID proyectoGrado, String nombre, String estado) {
        return new AsesorProyectoGradoReadModel(UtilUUID.generarNuevoUUID(), proyectoGrado,
                UtilUUID.generarNuevoUUID(), "1001", nombre, nombre + "@soyuco.edu.co", estado, estado,
                Instant.parse("2026-10-01T10:00:00Z"), true);
    }

    @Test
    void debeRetornarAsesores_cuandoValidacionPasa() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var coordinador = UtilUUID.generarNuevoUUID();
        var criteria = new AsesorProyectoGradoCoordinadorCriteria(proyectoGrado, coordinador);
        var asesores = List.of(asesor(proyectoGrado, "Ana", "ASIGNADO"),
                asesor(proyectoGrado, "Beto", "FINALIZADO"),
                asesor(proyectoGrado, "Carla", "FINALIZADO"));
        when(proyectoGradoCoordinadorQueryFinder.obtener(proyectoGrado)).thenReturn(coordinador);
        when(outputPort.consultarPorProyecto(proyectoGrado)).thenReturn(asesores);

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isSameAs(asesores);
        var orden = inOrder(proyectoGradoCoordinadorQueryFinder, validator, outputPort);
        orden.verify(proyectoGradoCoordinadorQueryFinder).obtener(proyectoGrado);
        orden.verify(validator).validar(proyectoGrado, coordinador, coordinador);
        orden.verify(outputPort).consultarPorProyecto(proyectoGrado);
        verify(proyectoGradoCoordinadorQueryFinder, times(1)).obtener(any());
        verify(outputPort, times(1)).consultarPorProyecto(any());
        verify(logger).debug(AsesorProyectoGradoKey.LOG_CONSULTANDO, proyectoGrado, coordinador);
        verify(logger).debug(AsesorProyectoGradoKey.LOG_CONSULTA_COMPLETADA, 3);
    }

    @Test
    void debeNoLeerAsesores_cuandoValidatorRechaza() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var solicitante = UtilUUID.generarNuevoUUID();
        var duenio = UtilUUID.generarNuevoUUID();
        var criteria = new AsesorProyectoGradoCoordinadorCriteria(proyectoGrado, solicitante);
        when(proyectoGradoCoordinadorQueryFinder.obtener(proyectoGrado)).thenReturn(duenio);
        doThrow(new ProyectoGradoNoPerteneceCoordinadorException(proyectoGrado, solicitante))
                .when(validator).validar(proyectoGrado, duenio, solicitante);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(criteria))
                .isInstanceOf(ProyectoGradoNoPerteneceCoordinadorException.class);
        verify(outputPort, never()).consultarPorProyecto(any());
    }
}
