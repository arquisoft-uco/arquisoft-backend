package com.arquisoft.artefactos.application.artefacto.command.usecase.impl;

import com.arquisoft.artefactos.application.artefacto.command.finder.ArtefactoOmitidoDelDocumentoFinder;
import com.arquisoft.artefactos.application.artefacto.command.usecase.ReactivarArtefactoUseCase;
import com.arquisoft.artefactos.application.artefacto.command.usecase.RegistrarArtefactoUseCase;
import com.arquisoft.artefactos.domain.artefacto.ArtefactoDomain;
import com.arquisoft.artefactos.domain.artefacto.AsignacionArtefactoDomain;
import com.arquisoft.artefactos.domain.artefacto.ReactivacionArtefactoDomain;
import com.arquisoft.artefactos.domain.estudianteartefacto.AgregacionEstudiantesArtefactoDomain;
import com.arquisoft.artefactos.domain.estudianteartefacto.EstudianteArtefactoDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.ArtefactoKey;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AsignarArtefactoUseCaseImplTest {

    @Mock
    private ArtefactoOmitidoDelDocumentoFinder artefactoOmitidoDelDocumentoFinder;
    @Mock
    private RegistrarArtefactoUseCase registrarArtefactoUseCase;
    @Mock
    private ReactivarArtefactoUseCase reactivarArtefactoUseCase;
    @Mock
    private AppLogger logger;

    @Captor
    private ArgumentCaptor<ReactivacionArtefactoDomain> reactivacionCaptor;

    private AsignarArtefactoUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new AsignarArtefactoUseCaseImpl(
                artefactoOmitidoDelDocumentoFinder, registrarArtefactoUseCase, reactivarArtefactoUseCase, logger);
    }

    private static AsignacionArtefactoDomain registro(List<UUID> estudiantes) {
        var artefacto = ArtefactoDomain.crear(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());
        var agregacion = AgregacionEstudiantesArtefactoDomain.crear(artefacto.getAsesor(),
                EstudianteArtefactoDomain.crear(artefacto.getId(), estudiantes));
        return AsignacionArtefactoDomain.crear(artefacto, agregacion);
    }

    @Test
    void debeRegistrarYDevolverElIdNuevo_cuandoNoHayArtefactoOmitido() {
        // Arrange
        var registro = registro(List.of(UtilUUID.generarNuevoUUID()));
        var artefacto = registro.getArtefacto();
        when(artefactoOmitidoDelDocumentoFinder.obtener(artefacto)).thenReturn(ArtefactoDomain.VACIO);
        when(registrarArtefactoUseCase.ejecutar(registro)).thenReturn(artefacto.getId());

        // Act
        var id = useCase.ejecutar(registro);

        // Assert
        assertThat(id).isEqualTo(artefacto.getId());
        var orden = inOrder(logger, artefactoOmitidoDelDocumentoFinder, registrarArtefactoUseCase);
        orden.verify(logger).info(ArtefactoKey.LOG_ASIGNANDO, artefacto.getProyectoGrado(), artefacto.getAsesor());
        orden.verify(artefactoOmitidoDelDocumentoFinder, times(1)).obtener(artefacto);
        orden.verify(logger).debug(ArtefactoKey.LOG_VERIFICACION_ASIGNAR, false);
        orden.verify(registrarArtefactoUseCase).ejecutar(registro);
        orden.verify(logger).info(ArtefactoKey.LOG_ASIGNADO, id, artefacto.getProyectoGrado());
        verify(reactivarArtefactoUseCase, never()).ejecutar(any());
    }

    @Test
    void debeReactivarConElAsesorDelRequestYDevolverElIdDelOmitido_cuandoHayArtefactoOmitido() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        var registro = registro(List.of(estudiante));
        var artefacto = registro.getArtefacto();
        var omitido = ArtefactoDomain.reconstruir(UtilUUID.generarNuevoUUID(), artefacto.getProyectoGrado(),
                artefacto.getVersionRepositorioArtefacto(), UtilUUID.generarNuevoUUID(),
                UtilFecha.generarInstanteActual());
        when(artefactoOmitidoDelDocumentoFinder.obtener(artefacto)).thenReturn(omitido);

        // Act
        var id = useCase.ejecutar(registro);

        // Assert
        assertThat(id).isEqualTo(omitido.getId());
        var orden = inOrder(logger, reactivarArtefactoUseCase);
        orden.verify(logger).debug(ArtefactoKey.LOG_VERIFICACION_ASIGNAR, true);
        orden.verify(reactivarArtefactoUseCase).ejecutar(reactivacionCaptor.capture());
        orden.verify(logger).info(ArtefactoKey.LOG_ASIGNADO, omitido.getId(), artefacto.getProyectoGrado());
        var reactivacion = reactivacionCaptor.getValue();
        assertThat(reactivacion.getArtefacto()).isEqualTo(omitido.getId());
        assertThat(reactivacion.getProyectoGrado()).isEqualTo(omitido.getProyectoGrado());
        assertThat(reactivacion.getAsesor()).isEqualTo(artefacto.getAsesor());
        assertThat(reactivacion.getEstudiantes()).containsExactly(estudiante);
        verify(registrarArtefactoUseCase, never()).ejecutar(any());
    }
}
