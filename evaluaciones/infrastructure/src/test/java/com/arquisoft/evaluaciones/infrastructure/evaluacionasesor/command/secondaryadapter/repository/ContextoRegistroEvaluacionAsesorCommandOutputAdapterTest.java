package com.arquisoft.evaluaciones.infrastructure.evaluacionasesor.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.infrastructure.evaluacionasesor.command.secondaryadapter.projection.ContextoRegistroEvaluacionAsesorProjection;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContextoRegistroEvaluacionAsesorCommandOutputAdapterTest {

    @Mock
    private EvaluacionAsesorCommandRepository repository;

    @InjectMocks
    private ContextoRegistroEvaluacionAsesorCommandOutputAdapter adapter;

    @Test
    void debeMapearElContexto_cuandoLaEvaluacionDeAsesorExiste() {
        // Arrange
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var evaluacion = UtilUUID.generarNuevoUUID();
        var entregable = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var proyeccion = mock(ContextoRegistroEvaluacionAsesorProjection.class);
        when(proyeccion.getId()).thenReturn(evaluacionAsesor);
        when(proyeccion.getEvaluacion()).thenReturn(evaluacion);
        when(proyeccion.getEstado()).thenReturn("PENDIENTE");
        when(proyeccion.getEntregable()).thenReturn(entregable);
        when(proyeccion.getAsesor()).thenReturn(asesor);
        when(repository.buscarContextoBloqueado(evaluacionAsesor)).thenReturn(Optional.of(proyeccion));

        // Act
        var resultado = adapter.obtenerContextoBloqueado(evaluacionAsesor);

        // Assert
        assertThat(resultado).hasValueSatisfying(contexto -> {
            assertThat(contexto.id()).isEqualTo(evaluacionAsesor);
            assertThat(contexto.evaluacion()).isEqualTo(evaluacion);
            assertThat(contexto.estado()).isEqualTo("PENDIENTE");
            assertThat(contexto.entregable()).isEqualTo(entregable);
            assertThat(contexto.asesor()).isEqualTo(asesor);
        });
    }

    @Test
    void debeRetornarVacio_cuandoLaEvaluacionDeAsesorNoExiste() {
        // Arrange
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        when(repository.buscarContextoBloqueado(evaluacionAsesor)).thenReturn(Optional.empty());

        // Act
        var resultado = adapter.obtenerContextoBloqueado(evaluacionAsesor);

        // Assert
        assertThat(resultado).isEmpty();
    }
}
