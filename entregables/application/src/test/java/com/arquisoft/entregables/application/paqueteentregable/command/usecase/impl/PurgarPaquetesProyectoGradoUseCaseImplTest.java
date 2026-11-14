package com.arquisoft.entregables.application.paqueteentregable.command.usecase.impl;

import com.arquisoft.entregables.application.paqueteentregable.command.finder.PaquetesProyectoGradoFinder;
import com.arquisoft.entregables.application.paqueteentregable.command.secondaryport.ArmadoPaqueteOutputPort;
import com.arquisoft.entregables.domain.paqueteentregable.PaqueteEntregableDomain;
import com.arquisoft.entregables.domain.paqueteentregable.PurgaPaquetesProyectoGradoDomain;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PurgarPaquetesProyectoGradoUseCaseImplTest {

    @Mock
    private PaquetesProyectoGradoFinder paquetesProyectoGradoFinder;

    @Mock
    private ArmadoPaqueteOutputPort armadoPaqueteOutputPort;

    @InjectMocks
    private PurgarPaquetesProyectoGradoUseCaseImpl useCase;

    private static PaqueteEntregableDomain paquete(String bucket, String clave) {
        return PaqueteEntregableDomain.crear(UtilUUID.generarNuevoUUID(), bucket, clave, "p.zip", 10L, Instant.now());
    }

    @Test
    void debeEliminarCadaZipEnSuBucket_cuandoElProyectoTienePaquetes() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        when(paquetesProyectoGradoFinder.obtener(proyecto)).thenReturn(List.of(
                paquete("artefactos", "paquetes/viejo.zip"), paquete("entregables", "entregables/paquetes/nuevo.zip")));

        // Act
        var resultado = useCase.ejecutar(PurgaPaquetesProyectoGradoDomain.crear(proyecto));

        // Assert
        verify(armadoPaqueteOutputPort).eliminar("artefactos", "paquetes/viejo.zip");
        verify(armadoPaqueteOutputPort).eliminar("entregables", "entregables/paquetes/nuevo.zip");
        assertThat(resultado.proyectoGrado()).isEqualTo(proyecto);
        assertThat(resultado.eliminados()).isEqualTo(2);
    }

    @Test
    void noDebeEliminarNada_cuandoElProyectoNoTienePaquetes() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        when(paquetesProyectoGradoFinder.obtener(proyecto)).thenReturn(List.of());

        // Act
        var resultado = useCase.ejecutar(PurgaPaquetesProyectoGradoDomain.crear(proyecto));

        // Assert
        verify(armadoPaqueteOutputPort, never()).eliminar(anyString(), anyString());
        assertThat(resultado.eliminados()).isZero();
    }
}
