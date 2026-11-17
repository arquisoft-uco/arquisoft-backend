package com.arquisoft.biblioteca.application.estudianteproyectogrado.command.usecase.impl;

import com.arquisoft.biblioteca.application.estudianteproyectogrado.command.finder.VinculosDeProyectoGradoFinder;
import com.arquisoft.biblioteca.application.estudianteproyectogrado.command.secondaryport.EstudianteProyectoGradoOutputPort;
import com.arquisoft.biblioteca.application.estudianteproyectogrado.command.secondaryport.entity.EstudianteProyectoGradoEntity;
import com.arquisoft.biblioteca.application.proyectogrado.command.usecase.AgregarProyectoGradoUseCase;
import com.arquisoft.biblioteca.domain.estudianteproyectogrado.AgregacionEstudiantesProyectoGradoDomain;
import com.arquisoft.biblioteca.domain.estudianteproyectogrado.EstudianteProyectoGradoDomain;
import com.arquisoft.biblioteca.domain.proyectogrado.ProyectoGradoDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AgregarEstudiantesProyectoGradoUseCaseImplTest {

    private static final Instant T1 = Instant.parse("2026-11-15T10:00:00Z");
    private static final Instant T2 = Instant.parse("2026-11-16T10:00:00Z");
    private static final Instant T5 = Instant.parse("2026-11-19T10:00:00Z");

    @Mock
    private AgregarProyectoGradoUseCase agregarProyectoGradoUseCase;
    @Mock
    private EstudianteProyectoGradoOutputPort estudianteProyectoGradoOutputPort;
    @Mock
    private VinculosDeProyectoGradoFinder vinculosDeProyectoGradoFinder;
    @Mock
    private AppLogger logger;

    private AgregarEstudiantesProyectoGradoUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new AgregarEstudiantesProyectoGradoUseCaseImpl(agregarProyectoGradoUseCase,
                estudianteProyectoGradoOutputPort, vinculosDeProyectoGradoFinder, logger);
    }

    private AgregacionEstudiantesProyectoGradoDomain evento(UUID proyecto, List<UUID> autores, Instant ocurridoEn) {
        var vinculos = autores.stream()
                .map(autor -> EstudianteProyectoGradoDomain.crear(autor, proyecto, ocurridoEn))
                .toList();
        return AgregacionEstudiantesProyectoGradoDomain.crear(
                ProyectoGradoDomain.crear(proyecto, "Titulo", ocurridoEn), vinculos);
    }

    private EstudianteProyectoGradoDomain guardado(UUID proyecto, UUID autor, Instant ocurridoEn, Instant eliminadoEn) {
        return EstudianteProyectoGradoDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), autor, proyecto, ocurridoEn, eliminadoEn);
    }

    @Test
    void debeAplicarElProyectoYGuardarTodosLosVinculos_cuandoNingunoExiste() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var autores = List.of(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());
        var entrada = evento(proyecto, autores, T1);
        when(vinculosDeProyectoGradoFinder.obtener(proyecto)).thenReturn(List.of());

        // Act
        var resultado = useCase.ejecutar(entrada);

        // Assert
        var orden = inOrder(agregarProyectoGradoUseCase, vinculosDeProyectoGradoFinder,
                estudianteProyectoGradoOutputPort);
        orden.verify(agregarProyectoGradoUseCase).ejecutar(entrada.getProyecto());
        orden.verify(vinculosDeProyectoGradoFinder, times(1)).obtener(proyecto);
        var captor = ArgumentCaptor.<List<EstudianteProyectoGradoEntity>>captor();
        orden.verify(estudianteProyectoGradoOutputPort).guardarTodos(captor.capture());
        assertThat(captor.getValue()).extracting(EstudianteProyectoGradoEntity::autor)
                .containsExactlyElementsOf(autores);
        assertThat(resultado.proyectoGrado()).isEqualTo(proyecto);
        assertThat(resultado.agregados()).isEqualTo(3);
        assertThat(resultado.reactivados() + resultado.duplicados() + resultado.descartados()).isZero();
    }

    @Test
    void debeContarCadaDesenlace_cuandoLosVinculosEstanEnDistintoEstado() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var nuevo = UtilUUID.generarNuevoUUID();
        var vigente = UtilUUID.generarNuevoUUID();
        var masReciente = UtilUUID.generarNuevoUUID();
        var retirado = UtilUUID.generarNuevoUUID();
        var entrada = evento(proyecto, List.of(nuevo, vigente, masReciente, retirado), T2);
        when(vinculosDeProyectoGradoFinder.obtener(proyecto)).thenReturn(List.of(
                guardado(proyecto, vigente, T1, UtilFecha.VACIO),
                guardado(proyecto, masReciente, T5, UtilFecha.VACIO),
                guardado(proyecto, retirado, T1, T1)));

        // Act
        var resultado = useCase.ejecutar(entrada);

        // Assert
        assertThat(resultado.agregados()).isEqualTo(1);
        assertThat(resultado.duplicados()).isEqualTo(1);
        assertThat(resultado.descartados()).isEqualTo(1);
        assertThat(resultado.reactivados()).isEqualTo(1);
        var captor = ArgumentCaptor.<List<EstudianteProyectoGradoEntity>>captor();
        verify(estudianteProyectoGradoOutputPort).guardarTodos(captor.capture());
        assertThat(captor.getValue()).extracting(EstudianteProyectoGradoEntity::autor)
                .containsExactlyInAnyOrder(nuevo, retirado);
        assertThat(captor.getValue()).filteredOn(entity -> entity.autor().equals(retirado))
                .singleElement()
                .satisfies(entity -> assertThat(entity.eliminadoEn()).isEqualTo(UtilFecha.VACIO));
    }
}
