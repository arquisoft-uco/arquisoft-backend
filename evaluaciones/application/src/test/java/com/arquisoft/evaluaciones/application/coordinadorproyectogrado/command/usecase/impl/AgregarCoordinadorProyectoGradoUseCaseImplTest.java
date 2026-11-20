package com.arquisoft.evaluaciones.application.coordinadorproyectogrado.command.usecase.impl;

import com.arquisoft.evaluaciones.application.coordinadorproyectogrado.command.finder.CoordinadorProyectoGradoExisteFinder;
import com.arquisoft.evaluaciones.application.coordinadorproyectogrado.command.result.AgregacionCoordinadorProyectoGradoResult;
import com.arquisoft.evaluaciones.application.coordinadorproyectogrado.command.secondaryport.CoordinadorProyectoGradoOutputPort;
import com.arquisoft.evaluaciones.application.coordinadorproyectogrado.command.secondaryport.entity.CoordinadorProyectoGradoEntity;
import com.arquisoft.evaluaciones.domain.coordinadorproyectogrado.CoordinadorProyectoGradoDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AgregarCoordinadorProyectoGradoUseCaseImplTest {

    @Mock
    private CoordinadorProyectoGradoOutputPort outputPort;

    @Mock
    private CoordinadorProyectoGradoExisteFinder existeFinder;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private AgregarCoordinadorProyectoGradoUseCaseImpl useCase;

    private static CoordinadorProyectoGradoDomain coordinador() {
        return CoordinadorProyectoGradoDomain.crear(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                "Coordinador", "coord@uco.edu.co", Instant.parse("2026-11-19T10:00:00Z"));
    }

    @Test
    void debeGuardarYRetornarAgregada_cuandoElProyectoNoTieneCoordinadorReplicado() {
        // Arrange
        var coordinador = coordinador();
        when(existeFinder.obtener(coordinador.getProyectoGrado())).thenReturn(false);

        // Act
        var resultado = useCase.ejecutar(coordinador);

        // Assert
        assertThat(resultado).isEqualTo(new AgregacionCoordinadorProyectoGradoResult.Agregada(
                coordinador.getProyectoGrado()));
        verify(outputPort).guardar(new CoordinadorProyectoGradoEntity(coordinador.getProyectoGrado(),
                coordinador.getCoordinador(), "Coordinador", "coord@uco.edu.co", coordinador.getOcurridoEn()));
    }

    @Test
    void debeRetornarDuplicadaSinGuardar_cuandoElProyectoYaTieneCoordinadorReplicado() {
        // Arrange
        var coordinador = coordinador();
        when(existeFinder.obtener(coordinador.getProyectoGrado())).thenReturn(true);

        // Act
        var resultado = useCase.ejecutar(coordinador);

        // Assert
        assertThat(resultado).isEqualTo(new AgregacionCoordinadorProyectoGradoResult.Duplicada(
                coordinador.getProyectoGrado()));
        verify(outputPort, never()).guardar(any());
    }
}
