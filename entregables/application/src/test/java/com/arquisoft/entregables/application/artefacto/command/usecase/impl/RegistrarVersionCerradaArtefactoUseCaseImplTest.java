package com.arquisoft.entregables.application.artefacto.command.usecase.impl;

import com.arquisoft.entregables.application.artefacto.command.finder.ArchivoOrigenArtefactoFinder;
import com.arquisoft.entregables.application.artefacto.command.finder.ArtefactoPorIdFinder;
import com.arquisoft.entregables.application.artefacto.command.result.RegistroVersionCerradaArtefactoResult;
import com.arquisoft.entregables.application.artefacto.command.secondaryport.AlmacenamientoArtefactoOutputPort;
import com.arquisoft.entregables.application.artefacto.command.secondaryport.ArtefactoOutputPort;
import com.arquisoft.entregables.application.artefacto.command.secondaryport.entity.ArtefactoEntity;
import com.arquisoft.entregables.application.artefacto.command.validator.RegistrarVersionCerradaArtefactoValidator;
import com.arquisoft.entregables.application.proyectogrado.command.finder.ProyectoGradoBloqueadoPorIdFinder;
import com.arquisoft.entregables.domain.artefacto.ArtefactoDomain;
import com.arquisoft.entregables.domain.artefacto.RegistroVersionCerradaArtefactoDomain;
import com.arquisoft.entregables.domain.artefacto.exception.ArchivoOrigenArtefactoNoDisponibleException;
import com.arquisoft.entregables.domain.estadoproyecto.EstadoProyecto;
import com.arquisoft.entregables.domain.proyectogrado.ProyectoGradoDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrarVersionCerradaArtefactoUseCaseImplTest {

    private static final String CLAVE_ORIGEN = "pendientes/origen.pdf";
    private static final String ETAG = "etag-1";

    @Mock
    private ProyectoGradoBloqueadoPorIdFinder proyectoGradoBloqueadoPorIdFinder;

    @Mock
    private ArtefactoPorIdFinder artefactoPorIdFinder;

    @Mock
    private ArchivoOrigenArtefactoFinder archivoOrigenArtefactoFinder;

    @Mock
    private RegistrarVersionCerradaArtefactoValidator registrarVersionCerradaArtefactoValidator;

    @Mock
    private ArtefactoOutputPort artefactoOutputPort;

    @Mock
    private AlmacenamientoArtefactoOutputPort almacenamientoArtefactoOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private RegistrarVersionCerradaArtefactoUseCaseImpl useCase;

    private static RegistroVersionCerradaArtefactoDomain registro(UUID artefacto, UUID proyecto, int numeroVersion) {
        var domain = ArtefactoDomain.crear(artefacto, proyecto, "Documento",
                new ArtefactoDomain.VersionCerrada(UtilUUID.generarNuevoUUID(), numeroVersion, Instant.now()),
                new ArtefactoDomain.DatosRepositorio(
                        UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), "Repositorio"));
        return RegistroVersionCerradaArtefactoDomain.crear(domain, CLAVE_ORIGEN);
    }

    private static ArtefactoDomain guardado(UUID artefacto, UUID proyecto, int numeroVersion) {
        return ArtefactoDomain.reconstruir(artefacto, proyecto, "Documento",
                "entregables/artefactos/" + artefacto + "/v" + numeroVersion,
                new ArtefactoDomain.VersionCerrada(UtilUUID.generarNuevoUUID(), numeroVersion, Instant.now()),
                new ArtefactoDomain.DatosRepositorio(
                        UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), "Repositorio"),
                new ArtefactoDomain.Omision(null, null));
    }

    private static ProyectoGradoDomain proyecto(UUID id, EstadoProyecto estado) {
        return ProyectoGradoDomain.reconstruir(id, "Proyecto", estado);
    }

    @Test
    void debeCopiarGuardarYBorrarAnterior_cuandoAvanza() {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();
        var proyectoId = UtilUUID.generarNuevoUUID();
        var registro = registro(artefacto, proyectoId, 3);
        var proyecto = proyecto(proyectoId, EstadoProyecto.EN_PROCESO);
        var anterior = guardado(artefacto, proyectoId, 2);
        when(proyectoGradoBloqueadoPorIdFinder.obtener(proyectoId)).thenReturn(proyecto);
        when(artefactoPorIdFinder.obtener(artefacto)).thenReturn(anterior);
        when(archivoOrigenArtefactoFinder.obtener(CLAVE_ORIGEN)).thenReturn(ETAG);
        when(artefactoOutputPort.avanzarVersion(any())).thenReturn(1);

        // Act
        var resultado = useCase.ejecutar(registro);

        // Assert
        var captor = ArgumentCaptor.forClass(ArtefactoEntity.class);
        var orden = inOrder(proyectoGradoBloqueadoPorIdFinder, artefactoPorIdFinder, archivoOrigenArtefactoFinder,
                registrarVersionCerradaArtefactoValidator, almacenamientoArtefactoOutputPort, artefactoOutputPort);
        orden.verify(proyectoGradoBloqueadoPorIdFinder).obtener(proyectoId);
        orden.verify(artefactoPorIdFinder).obtener(artefacto);
        orden.verify(archivoOrigenArtefactoFinder).obtener(CLAVE_ORIGEN);
        orden.verify(registrarVersionCerradaArtefactoValidator).validar(registro, proyecto, ETAG);
        orden.verify(almacenamientoArtefactoOutputPort)
                .copiar(CLAVE_ORIGEN, registro.getArtefacto().getClaveObjeto(), ETAG);
        orden.verify(artefactoOutputPort).avanzarVersion(captor.capture());
        orden.verify(almacenamientoArtefactoOutputPort).eliminarTrasConfirmar(anterior.getClaveObjeto());
        verify(proyectoGradoBloqueadoPorIdFinder, times(1)).obtener(any());
        verify(artefactoPorIdFinder, times(1)).obtener(any());
        verify(archivoOrigenArtefactoFinder, times(1)).obtener(any());
        assertThat(captor.getValue().id()).isEqualTo(artefacto);
        assertThat(captor.getValue().numeroVersion()).isEqualTo(3);
        assertThat(captor.getValue().claveObjeto()).isEqualTo(registro.getArtefacto().getClaveObjeto());
        assertThat(resultado).isEqualTo(new RegistroVersionCerradaArtefactoResult.Registrada(artefacto));
    }

    @Test
    void noDebeBorrar_cuandoNoHabiaCopiaAnterior() {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();
        var proyectoId = UtilUUID.generarNuevoUUID();
        var registro = registro(artefacto, proyectoId, 1);
        when(proyectoGradoBloqueadoPorIdFinder.obtener(proyectoId))
                .thenReturn(proyecto(proyectoId, EstadoProyecto.ATRASADO));
        when(artefactoPorIdFinder.obtener(artefacto)).thenReturn(ArtefactoDomain.VACIO);
        when(archivoOrigenArtefactoFinder.obtener(CLAVE_ORIGEN)).thenReturn(ETAG);

        // Act
        var resultado = useCase.ejecutar(registro);

        // Assert
        verify(almacenamientoArtefactoOutputPort).copiar(CLAVE_ORIGEN, registro.getArtefacto().getClaveObjeto(), ETAG);
        verify(artefactoOutputPort).insertar(any());
        verify(almacenamientoArtefactoOutputPort, never()).eliminarTrasConfirmar(anyString());
        assertThat(resultado).isEqualTo(new RegistroVersionCerradaArtefactoResult.Registrada(artefacto));
    }

    @Test
    void debeCopiarYLuegoInsertar_cuandoNoHabiaFila() {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();
        var proyectoId = UtilUUID.generarNuevoUUID();
        var registro = registro(artefacto, proyectoId, 1);
        when(proyectoGradoBloqueadoPorIdFinder.obtener(proyectoId))
                .thenReturn(proyecto(proyectoId, EstadoProyecto.EN_PROCESO));
        when(artefactoPorIdFinder.obtener(artefacto)).thenReturn(ArtefactoDomain.VACIO);
        when(archivoOrigenArtefactoFinder.obtener(CLAVE_ORIGEN)).thenReturn(ETAG);

        // Act
        useCase.ejecutar(registro);

        // Assert
        var orden = inOrder(almacenamientoArtefactoOutputPort, artefactoOutputPort);
        orden.verify(almacenamientoArtefactoOutputPort)
                .copiar(CLAVE_ORIGEN, registro.getArtefacto().getClaveObjeto(), ETAG);
        orden.verify(artefactoOutputPort).insertar(any());
        verify(artefactoOutputPort, never()).avanzarVersion(any());
    }

    @Test
    void noDebeBorrarNiRegistrar_cuandoAvanzarVersionAfectaCeroFilas() {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();
        var proyectoId = UtilUUID.generarNuevoUUID();
        var registro = registro(artefacto, proyectoId, 3);
        when(proyectoGradoBloqueadoPorIdFinder.obtener(proyectoId))
                .thenReturn(proyecto(proyectoId, EstadoProyecto.EN_PROCESO));
        when(artefactoPorIdFinder.obtener(artefacto)).thenReturn(guardado(artefacto, proyectoId, 2));
        when(archivoOrigenArtefactoFinder.obtener(CLAVE_ORIGEN)).thenReturn(ETAG);
        when(artefactoOutputPort.avanzarVersion(any())).thenReturn(0);

        // Act
        var resultado = useCase.ejecutar(registro);

        // Assert
        verify(artefactoOutputPort).avanzarVersion(any());
        verify(almacenamientoArtefactoOutputPort, never()).eliminarTrasConfirmar(anyString());
        assertThat(resultado).isEqualTo(new RegistroVersionCerradaArtefactoResult.Obsoleta(artefacto));
    }

    @Test
    void debeAvanzarSinBorrar_cuandoLaFilaGuardadaEsUnaLapida() {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();
        var proyectoId = UtilUUID.generarNuevoUUID();
        var registro = registro(artefacto, proyectoId, 1);
        var lapida = ArtefactoDomain.reconstruir(artefacto, proyectoId, "", "",
                new ArtefactoDomain.VersionCerrada(UtilUUID.obtenerUUIDPorDefecto(), 0, Instant.EPOCH),
                new ArtefactoDomain.DatosRepositorio(
                        UtilUUID.obtenerUUIDPorDefecto(), UtilUUID.obtenerUUIDPorDefecto(), ""),
                new ArtefactoDomain.Omision(Instant.now(), Instant.now()));
        when(proyectoGradoBloqueadoPorIdFinder.obtener(proyectoId))
                .thenReturn(proyecto(proyectoId, EstadoProyecto.EN_PROCESO));
        when(artefactoPorIdFinder.obtener(artefacto)).thenReturn(lapida);
        when(archivoOrigenArtefactoFinder.obtener(CLAVE_ORIGEN)).thenReturn(ETAG);
        when(artefactoOutputPort.avanzarVersion(any())).thenReturn(1);

        // Act
        var resultado = useCase.ejecutar(registro);

        // Assert
        verify(artefactoOutputPort).avanzarVersion(any());
        verify(artefactoOutputPort, never()).insertar(any());
        verify(almacenamientoArtefactoOutputPort, never()).eliminarTrasConfirmar(anyString());
        assertThat(resultado).isEqualTo(new RegistroVersionCerradaArtefactoResult.Registrada(artefacto));
    }

    @Test
    void debeDevolverObsoleta_cuandoLaVersionGuardadaEsIgualOMayor() {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();
        var proyectoId = UtilUUID.generarNuevoUUID();
        var registro = registro(artefacto, proyectoId, 2);
        when(proyectoGradoBloqueadoPorIdFinder.obtener(proyectoId))
                .thenReturn(proyecto(proyectoId, EstadoProyecto.EN_PROCESO));
        when(artefactoPorIdFinder.obtener(artefacto)).thenReturn(guardado(artefacto, proyectoId, 2));

        // Act
        var resultado = useCase.ejecutar(registro);

        // Assert
        verify(archivoOrigenArtefactoFinder, never()).obtener(anyString());
        verify(registrarVersionCerradaArtefactoValidator, never()).validar(any(), any(), anyString());
        verify(almacenamientoArtefactoOutputPort, never()).copiar(anyString(), anyString(), anyString());
        verify(artefactoOutputPort, never()).insertar(any());
        verify(artefactoOutputPort, never()).avanzarVersion(any());
        assertThat(resultado).isEqualTo(new RegistroVersionCerradaArtefactoResult.Obsoleta(artefacto));
    }

    @Test
    void debeDevolverProyectoCerrado_cuandoElProyectoEstaFinalizado() {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();
        var proyectoId = UtilUUID.generarNuevoUUID();
        var registro = registro(artefacto, proyectoId, 5);
        when(proyectoGradoBloqueadoPorIdFinder.obtener(proyectoId))
                .thenReturn(proyecto(proyectoId, EstadoProyecto.FINALIZADO));
        when(artefactoPorIdFinder.obtener(artefacto)).thenReturn(ArtefactoDomain.VACIO);

        // Act
        var resultado = useCase.ejecutar(registro);

        // Assert
        verify(archivoOrigenArtefactoFinder, never()).obtener(anyString());
        verify(registrarVersionCerradaArtefactoValidator, never()).validar(any(), any(), anyString());
        verify(almacenamientoArtefactoOutputPort, never()).copiar(anyString(), anyString(), anyString());
        verify(artefactoOutputPort, never()).insertar(any());
        verify(artefactoOutputPort, never()).avanzarVersion(any());
        assertThat(resultado).isEqualTo(new RegistroVersionCerradaArtefactoResult.ProyectoCerrado(artefacto));
    }

    @Test
    void debeRechazarSinCopiarNiEscribir_cuandoElValidatorLanza() {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();
        var proyectoId = UtilUUID.generarNuevoUUID();
        var registro = registro(artefacto, proyectoId, 1);
        var proyecto = proyecto(proyectoId, EstadoProyecto.EN_PROCESO);
        when(proyectoGradoBloqueadoPorIdFinder.obtener(proyectoId)).thenReturn(proyecto);
        when(artefactoPorIdFinder.obtener(artefacto)).thenReturn(ArtefactoDomain.VACIO);
        when(archivoOrigenArtefactoFinder.obtener(CLAVE_ORIGEN)).thenReturn("");
        doThrow(new ArchivoOrigenArtefactoNoDisponibleException(artefacto, "Documento", CLAVE_ORIGEN))
                .when(registrarVersionCerradaArtefactoValidator).validar(registro, proyecto, "");

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(registro))
                .isInstanceOf(ArchivoOrigenArtefactoNoDisponibleException.class);
        verify(almacenamientoArtefactoOutputPort, never()).copiar(anyString(), anyString(), anyString());
        verify(artefactoOutputPort, never()).insertar(any());
        verify(artefactoOutputPort, never()).avanzarVersion(any());
        verify(almacenamientoArtefactoOutputPort, never()).eliminarTrasConfirmar(anyString());
    }
}
