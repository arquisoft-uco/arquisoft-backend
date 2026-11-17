package com.arquisoft.biblioteca.application.proyectogrado.command.usecase.impl;

import com.arquisoft.biblioteca.application.proyectogrado.command.finder.ProyectoGradoPorIdFinder;
import com.arquisoft.biblioteca.application.proyectogrado.command.secondaryport.ProyectoGradoOutputPort;
import com.arquisoft.biblioteca.application.proyectogrado.command.secondaryport.entity.ProyectoGradoEntity;
import com.arquisoft.biblioteca.domain.proyectogrado.ProyectoGradoDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AgregarProyectoGradoUseCaseImplTest {

    private static final Instant T1 = Instant.parse("2026-11-15T10:00:00Z");
    private static final Instant T2 = Instant.parse("2026-11-16T10:00:00Z");

    @Mock
    private ProyectoGradoPorIdFinder proyectoGradoPorIdFinder;
    @Mock
    private ProyectoGradoOutputPort proyectoGradoOutputPort;
    @Mock
    private AppLogger logger;

    private AgregarProyectoGradoUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new AgregarProyectoGradoUseCaseImpl(proyectoGradoPorIdFinder, proyectoGradoOutputPort, logger);
    }

    @Test
    void debeInsertarElProyecto_cuandoNoExiste() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var proyecto = ProyectoGradoDomain.crear(id, "Titulo", T1);
        when(proyectoGradoPorIdFinder.obtener(id)).thenReturn(ProyectoGradoDomain.VACIO);

        // Act
        useCase.ejecutar(proyecto);

        // Assert
        verify(proyectoGradoOutputPort).guardar(new ProyectoGradoEntity(id, "Titulo", T1));
    }

    @Test
    void debeActualizarTituloYFecha_cuandoElEventoEsPosterior() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var existente = ProyectoGradoDomain.reconstruir(id, "Viejo", T1);
        when(proyectoGradoPorIdFinder.obtener(id)).thenReturn(existente);

        // Act
        useCase.ejecutar(ProyectoGradoDomain.crear(id, "Nuevo", T2));

        // Assert
        verify(proyectoGradoOutputPort).guardar(new ProyectoGradoEntity(id, "Nuevo", T2));
    }

    @Test
    void noDebeGuardar_cuandoElEventoEsMasViejo() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        when(proyectoGradoPorIdFinder.obtener(id)).thenReturn(ProyectoGradoDomain.reconstruir(id, "Actual", T2));

        // Act
        useCase.ejecutar(ProyectoGradoDomain.crear(id, "Viejo", T1));

        // Assert
        verify(proyectoGradoOutputPort, never()).guardar(any());
    }
}
