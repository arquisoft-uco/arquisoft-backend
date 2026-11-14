package com.arquisoft.artefactos.application.versionartefacto.command.usecase.impl;

import com.arquisoft.artefactos.application.proyectogrado.command.finder.ProyectoGradoPorIdFinder;
import com.arquisoft.artefactos.application.versionartefacto.command.finder.UrisArchivoVersionesProyectoGradoFinder;
import com.arquisoft.artefactos.application.versionartefacto.command.result.PurgaVersionesProyectoGradoResult;
import com.arquisoft.artefactos.application.versionartefacto.command.secondaryport.AlmacenamientoVersionOutputPort;
import com.arquisoft.artefactos.application.versionartefacto.command.validator.PurgarVersionesProyectoGradoValidator;
import com.arquisoft.artefactos.domain.proyectogrado.ProyectoGradoDomain;
import com.arquisoft.artefactos.domain.proyectogrado.model.EstadoProyecto;
import com.arquisoft.artefactos.domain.proyectogrado.exception.ProyectoGradoNoConcluidoException;
import com.arquisoft.artefactos.domain.proyectogrado.model.EstadoProyectoGrado;
import com.arquisoft.artefactos.domain.proyectogrado.model.ExistenciaProyectoGrado;
import com.arquisoft.artefactos.domain.versionartefacto.PurgaVersionesProyectoGradoDomain;
import com.arquisoft.shared.exception.InfrastructureException;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PurgarVersionesProyectoGradoUseCaseImplTest {

    @Mock
    private AlmacenamientoVersionOutputPort almacenamientoVersionOutputPort;
    @Mock
    private ProyectoGradoPorIdFinder proyectoGradoPorIdFinder;
    @Mock
    private UrisArchivoVersionesProyectoGradoFinder urisArchivoVersionesProyectoGradoFinder;
    @Mock
    private PurgarVersionesProyectoGradoValidator purgarVersionesProyectoGradoValidator;
    @Mock
    private AppLogger logger;

    @InjectMocks
    private PurgarVersionesProyectoGradoUseCaseImpl useCase;

    private UUID id;
    private PurgaVersionesProyectoGradoDomain purga;
    private ProyectoGradoDomain proyectoFinalizado;

    @BeforeEach
    void setUp() {
        id = UtilUUID.generarNuevoUUID();
        purga = PurgaVersionesProyectoGradoDomain.crear(id);
        proyectoFinalizado = ProyectoGradoDomain.reconstruir(
                id, "Proyecto", UtilFecha.VACIO, EstadoProyecto.FINALIZADO, Instant.parse("2026-11-01T10:00:00Z"));
    }

    @Test
    void debeBorrarCadaObjetoEnOrdenYRetornarCantidad_cuandoProyectoFinalizado() {
        // Arrange
        var uris = List.of("versiones/a", "versiones/b", "versiones/c");
        when(proyectoGradoPorIdFinder.obtener(id)).thenReturn(proyectoFinalizado);
        when(urisArchivoVersionesProyectoGradoFinder.obtener(id)).thenReturn(uris);

        // Act
        var resultado = useCase.ejecutar(purga);

        // Assert
        var orden = inOrder(proyectoGradoPorIdFinder, urisArchivoVersionesProyectoGradoFinder,
                purgarVersionesProyectoGradoValidator, almacenamientoVersionOutputPort);
        orden.verify(proyectoGradoPorIdFinder, times(1)).obtener(id);
        orden.verify(urisArchivoVersionesProyectoGradoFinder, times(1)).obtener(id);
        orden.verify(purgarVersionesProyectoGradoValidator).validar(
                new ExistenciaProyectoGrado(id, true), new EstadoProyectoGrado(id, "Proyecto", true, true));
        orden.verify(almacenamientoVersionOutputPort).eliminarArchivo("versiones/a");
        orden.verify(almacenamientoVersionOutputPort).eliminarArchivo("versiones/b");
        orden.verify(almacenamientoVersionOutputPort).eliminarArchivo("versiones/c");
        assertThat(resultado).isEqualTo(new PurgaVersionesProyectoGradoResult(id, 3));
    }

    @Test
    void debePurgar_cuandoProyectoCancelado() {
        // Arrange
        var cancelado = ProyectoGradoDomain.reconstruir(
                id, "Proyecto", UtilFecha.VACIO, EstadoProyecto.CANCELADO, Instant.parse("2026-11-02T10:00:00Z"));
        when(proyectoGradoPorIdFinder.obtener(id)).thenReturn(cancelado);
        when(urisArchivoVersionesProyectoGradoFinder.obtener(id)).thenReturn(List.of("versiones/a"));

        // Act
        var resultado = useCase.ejecutar(purga);

        // Assert
        verify(purgarVersionesProyectoGradoValidator).validar(
                new ExistenciaProyectoGrado(id, true), new EstadoProyectoGrado(id, "Proyecto", true, true));
        verify(almacenamientoVersionOutputPort).eliminarArchivo("versiones/a");
        assertThat(resultado).isEqualTo(new PurgaVersionesProyectoGradoResult(id, 1));
    }

    @Test
    void debeNoBorrarNada_cuandoValidatorLanza() {
        // Arrange
        when(proyectoGradoPorIdFinder.obtener(id)).thenReturn(
                ProyectoGradoDomain.reconstruir(id, "Proyecto", Instant.now(), EstadoProyecto.EN_PROCESO, null));
        when(urisArchivoVersionesProyectoGradoFinder.obtener(id)).thenReturn(List.of("versiones/a"));
        doThrow(new ProyectoGradoNoConcluidoException(new EstadoProyectoGrado(id, "Proyecto", true, false)))
                .when(purgarVersionesProyectoGradoValidator).validar(any(), any());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(purga)).isInstanceOf(ProyectoGradoNoConcluidoException.class);
        verify(almacenamientoVersionOutputPort, never()).eliminarArchivo(anyString());
    }

    @Test
    void debePropagarFallo_cuandoUnBorradoFalla() {
        // Arrange
        when(proyectoGradoPorIdFinder.obtener(id)).thenReturn(proyectoFinalizado);
        when(urisArchivoVersionesProyectoGradoFinder.obtener(id)).thenReturn(List.of("versiones/a", "versiones/b"));
        var fallo = mock(InfrastructureException.class);
        doThrow(fallo).when(almacenamientoVersionOutputPort).eliminarArchivo("versiones/a");

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(purga)).isSameAs(fallo);
        verify(almacenamientoVersionOutputPort, never()).eliminarArchivo("versiones/b");
    }

    @Test
    void debeCompletarConCero_cuandoSinVersiones() {
        // Arrange
        when(proyectoGradoPorIdFinder.obtener(id)).thenReturn(proyectoFinalizado);
        when(urisArchivoVersionesProyectoGradoFinder.obtener(id)).thenReturn(List.of());

        // Act
        var resultado = useCase.ejecutar(purga);

        // Assert
        assertThat(resultado.archivos()).isZero();
        verify(almacenamientoVersionOutputPort, never()).eliminarArchivo(anyString());
    }
}
