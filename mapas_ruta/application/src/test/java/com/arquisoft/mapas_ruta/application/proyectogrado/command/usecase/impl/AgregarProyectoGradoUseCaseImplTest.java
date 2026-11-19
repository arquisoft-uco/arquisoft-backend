package com.arquisoft.mapas_ruta.application.proyectogrado.command.usecase.impl;

import com.arquisoft.mapas_ruta.application.proyectogrado.command.finder.ProyectoGradoExisteFinder;
import com.arquisoft.mapas_ruta.application.proyectogrado.command.result.AgregacionProyectoGradoResult;
import com.arquisoft.mapas_ruta.application.proyectogrado.command.secondaryport.ProyectoGradoOutputPort;
import com.arquisoft.mapas_ruta.application.proyectogrado.command.secondaryport.entity.ProyectoGradoEntity;
import com.arquisoft.mapas_ruta.domain.proyectogrado.ProyectoGradoDomain;
import com.arquisoft.mapas_ruta.domain.proyectogrado.model.EstadoProyectoGrado;
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
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AgregarProyectoGradoUseCaseImplTest {

    @Mock
    private ProyectoGradoOutputPort proyectoGradoOutputPort;

    @Mock
    private ProyectoGradoExisteFinder proyectoGradoExisteFinder;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private AgregarProyectoGradoUseCaseImpl useCase;

    private static ProyectoGradoDomain proyecto() {
        return ProyectoGradoDomain.crear(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                UtilUUID.generarNuevoUUID(), "Titulo", Instant.parse("2026-11-01T10:00:00Z"));
    }

    @Test
    void debeGuardarYDevolverAgregado_cuandoNoExiste() {
        // Arrange
        var proyecto = proyecto();
        when(proyectoGradoExisteFinder.obtener(proyecto.getId())).thenReturn(false);

        // Act
        var resultado = useCase.ejecutar(proyecto);

        // Assert
        assertThat(resultado).isEqualTo(new AgregacionProyectoGradoResult.Agregado(proyecto.getId()));
        var orden = inOrder(proyectoGradoExisteFinder, proyectoGradoOutputPort);
        orden.verify(proyectoGradoExisteFinder).obtener(proyecto.getId());
        orden.verify(proyectoGradoOutputPort).guardar(new ProyectoGradoEntity(
                proyecto.getId(), EstadoProyectoGrado.EN_PROCESO.getId(), proyecto.getCoordinador(),
                proyecto.getFichaPerfil(), "Titulo", proyecto.getOcurridoEn()));
    }

    @Test
    void debeDevolverDuplicado_cuandoYaExiste() {
        // Arrange
        var proyecto = proyecto();
        when(proyectoGradoExisteFinder.obtener(proyecto.getId())).thenReturn(true);

        // Act
        var resultado = useCase.ejecutar(proyecto);

        // Assert
        assertThat(resultado).isEqualTo(new AgregacionProyectoGradoResult.Duplicado(proyecto.getId()));
        verify(proyectoGradoOutputPort, never()).guardar(any());
    }
}
