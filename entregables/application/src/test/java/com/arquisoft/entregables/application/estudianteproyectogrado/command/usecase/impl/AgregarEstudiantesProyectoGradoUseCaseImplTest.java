package com.arquisoft.entregables.application.estudianteproyectogrado.command.usecase.impl;

import com.arquisoft.entregables.application.estudianteproyectogrado.command.finder.VinculosDeProyectoGradoFinder;
import com.arquisoft.entregables.application.estudianteproyectogrado.command.result.AgregacionEstudiantesProyectoGradoResult;
import com.arquisoft.entregables.application.estudianteproyectogrado.command.secondaryport.EstudianteProyectoGradoOutputPort;
import com.arquisoft.entregables.application.estudianteproyectogrado.command.secondaryport.entity.EstudianteProyectoGradoEntity;
import com.arquisoft.entregables.domain.estudianteproyectogrado.AgregacionEstudiantesProyectoGradoDomain;
import com.arquisoft.entregables.domain.estudianteproyectogrado.EstudianteProyectoGradoDomain;
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
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AgregarEstudiantesProyectoGradoUseCaseImplTest {

    private static final Instant GUARDADO_EN = Instant.parse("2026-10-20T10:00:00Z");
    private static final Instant NUEVO = GUARDADO_EN.plusSeconds(3600);

    @Mock
    private EstudianteProyectoGradoOutputPort estudianteProyectoGradoOutputPort;
    @Mock
    private VinculosDeProyectoGradoFinder vinculosDeProyectoGradoFinder;
    @Mock
    private AppLogger logger;

    private AgregarEstudiantesProyectoGradoUseCaseImpl useCase;

    private final UUID proyectoGrado = UtilUUID.generarNuevoUUID();

    @BeforeEach
    void setUp() {
        useCase = new AgregarEstudiantesProyectoGradoUseCaseImpl(
                estudianteProyectoGradoOutputPort, vinculosDeProyectoGradoFinder, logger);
    }

    private AgregacionEstudiantesProyectoGradoDomain entrada(Instant ocurridoEn, UUID... estudiantes) {
        var vinculos = Arrays.stream(estudiantes)
                .map(estudiante -> EstudianteProyectoGradoDomain.crear(estudiante, proyectoGrado, ocurridoEn))
                .toList();
        return AgregacionEstudiantesProyectoGradoDomain.crear(proyectoGrado, vinculos);
    }

    private EstudianteProyectoGradoDomain guardado(UUID estudiante, Instant eliminadoEn) {
        return EstudianteProyectoGradoDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), estudiante, proyectoGrado, GUARDADO_EN, eliminadoEn);
    }

    @SuppressWarnings("unchecked")
    private List<EstudianteProyectoGradoEntity> guardadosCapturados() {
        var captor = ArgumentCaptor.forClass(List.class);
        verify(estudianteProyectoGradoOutputPort).guardarTodos(captor.capture());
        return (List<EstudianteProyectoGradoEntity>) captor.getValue();
    }

    @Test
    void debeGuardarVinculosNuevos_cuandoNoExisten() {
        // Arrange
        var primero = UtilUUID.generarNuevoUUID();
        var segundo = UtilUUID.generarNuevoUUID();
        var entrada = entrada(GUARDADO_EN, primero, segundo);
        when(vinculosDeProyectoGradoFinder.obtener(proyectoGrado)).thenReturn(List.of());

        // Act
        var resultado = useCase.ejecutar(entrada);

        // Assert
        assertThat(resultado).isEqualTo(new AgregacionEstudiantesProyectoGradoResult(proyectoGrado, 2, 0, 0, 0));
        var guardados = guardadosCapturados();
        assertThat(guardados).extracting(EstudianteProyectoGradoEntity::estudiante).containsExactly(primero, segundo);
        assertThat(guardados.get(0).id()).isEqualTo(entrada.getVinculos().get(0).getId());
        assertThat(guardados).extracting(EstudianteProyectoGradoEntity::eliminadoEn).containsOnly(UtilFecha.VACIO);
    }

    @Test
    void debeContarDuplicado_cuandoVinculoVigente() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        when(vinculosDeProyectoGradoFinder.obtener(proyectoGrado))
                .thenReturn(List.of(guardado(estudiante, UtilFecha.VACIO)));

        // Act
        var resultado = useCase.ejecutar(entrada(NUEVO, estudiante));

        // Assert
        assertThat(resultado).isEqualTo(new AgregacionEstudiantesProyectoGradoResult(proyectoGrado, 0, 0, 1, 0));
        assertThat(guardadosCapturados()).isEmpty();
    }

    @Test
    void debeReactivar_cuandoVinculoDadoDeBajaYEventoMasNuevo() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        var existente = guardado(estudiante, GUARDADO_EN);
        when(vinculosDeProyectoGradoFinder.obtener(proyectoGrado)).thenReturn(List.of(existente));

        // Act
        var resultado = useCase.ejecutar(entrada(NUEVO, estudiante));

        // Assert
        assertThat(resultado).isEqualTo(new AgregacionEstudiantesProyectoGradoResult(proyectoGrado, 0, 1, 0, 0));
        var guardados = guardadosCapturados();
        assertThat(guardados).hasSize(1);
        assertThat(guardados.getFirst().id()).isEqualTo(existente.getId());
        assertThat(guardados.getFirst().ocurridoEn()).isEqualTo(NUEVO);
        assertThat(guardados.getFirst().eliminadoEn()).isEqualTo(UtilFecha.VACIO);
    }

    @Test
    void debeDescartarTodos_cuandoNingunEventoEsMasNuevoQueElVinculoGuardado() {
        // Arrange
        var mismoInstante = UtilUUID.generarNuevoUUID();
        var vigenteMasNuevo = UtilUUID.generarNuevoUUID();
        var lapidaMasNueva = UtilUUID.generarNuevoUUID();
        var lapida = EstudianteProyectoGradoDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), lapidaMasNueva, proyectoGrado, NUEVO, NUEVO);
        when(vinculosDeProyectoGradoFinder.obtener(proyectoGrado)).thenReturn(List.of(
                guardado(mismoInstante, GUARDADO_EN),
                guardado(vigenteMasNuevo, UtilFecha.VACIO),
                lapida));

        // Act
        var resultado = useCase.ejecutar(entrada(GUARDADO_EN, mismoInstante, vigenteMasNuevo, lapidaMasNueva));

        // Assert
        assertThat(resultado).isEqualTo(new AgregacionEstudiantesProyectoGradoResult(proyectoGrado, 0, 0, 0, 3));
        assertThat(guardadosCapturados()).isEmpty();
    }

    @Test
    void debeCombinarDesenlaces_cuandoLoteMixto() {
        // Arrange
        var nuevo = UtilUUID.generarNuevoUUID();
        var vigente = UtilUUID.generarNuevoUUID();
        var dadoDeBaja = UtilUUID.generarNuevoUUID();
        when(vinculosDeProyectoGradoFinder.obtener(proyectoGrado))
                .thenReturn(List.of(guardado(vigente, UtilFecha.VACIO), guardado(dadoDeBaja, GUARDADO_EN)));

        // Act
        var resultado = useCase.ejecutar(entrada(NUEVO, nuevo, vigente, dadoDeBaja));

        // Assert
        assertThat(resultado).isEqualTo(new AgregacionEstudiantesProyectoGradoResult(proyectoGrado, 1, 1, 1, 0));
        assertThat(guardadosCapturados()).extracting(EstudianteProyectoGradoEntity::estudiante)
                .containsExactlyInAnyOrder(nuevo, dadoDeBaja);
    }

    @Test
    void debeLlamarAlFinderUnaVez_cuandoHayVariosEstudiantes() {
        // Arrange
        when(vinculosDeProyectoGradoFinder.obtener(proyectoGrado)).thenReturn(List.of());

        // Act
        useCase.ejecutar(entrada(NUEVO, UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                UtilUUID.generarNuevoUUID()));

        // Assert
        verify(vinculosDeProyectoGradoFinder, times(1)).obtener(proyectoGrado);
    }
}
