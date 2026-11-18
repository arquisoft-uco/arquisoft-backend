package com.arquisoft.entregables.application.entregableproyectogrado.command.usecase.impl;

import com.arquisoft.entregables.application.artefacto.command.finder.ArtefactosVigentesProyectoFinder;
import com.arquisoft.entregables.application.artefactoentregable.command.usecase.AgregarArtefactosEntregableUseCase;
import com.arquisoft.entregables.application.asesor.command.finder.AsesorVigenteFinder;
import com.arquisoft.entregables.application.asesorproyectogrado.command.finder.AsesorVigenteProyectoGradoFinder;
import com.arquisoft.entregables.application.entregableproyectogrado.command.finder.EntregableVigenteExisteFinder;
import com.arquisoft.entregables.application.entregableproyectogrado.command.finder.UltimaVersionEntregableProyectoGradoFinder;
import com.arquisoft.entregables.application.entregableproyectogrado.command.secondaryport.EjecutorTransaccionalOutputPort;
import com.arquisoft.entregables.application.entregableproyectogrado.command.secondaryport.EntregableProyectoGradoOutputPort;
import com.arquisoft.entregables.application.entregableproyectogrado.command.secondaryport.entity.EntregableProyectoGradoEntity;
import com.arquisoft.entregables.application.entregableproyectogrado.command.validator.ConfirmarGeneracionEntregableProyectoGradoValidator;
import com.arquisoft.entregables.application.entregableproyectogrado.command.validator.GenerarEntregableProyectoGradoValidator;
import com.arquisoft.entregables.application.estadoentregableproyectogrado.command.secondaryport.EstadoEntregableProyectoGradoOutputPort;
import com.arquisoft.entregables.application.estadoentregableproyectogrado.command.secondaryport.entity.EstadoEntregableProyectoGradoEntity;
import com.arquisoft.entregables.application.paqueteentregable.command.finder.PaquetesProyectoGradoFinder;
import com.arquisoft.entregables.application.paqueteentregable.command.secondaryport.ArmadoPaqueteOutputPort;
import com.arquisoft.entregables.application.paqueteentregable.command.secondaryport.PaqueteEntregableOutputPort;
import com.arquisoft.entregables.application.paqueteentregable.command.secondaryport.entity.PaqueteEntregableEntity;
import com.arquisoft.entregables.application.paqueteentregable.command.secondaryport.model.ArchivoPaqueteArmado;
import com.arquisoft.entregables.application.paqueteentregable.command.secondaryport.model.EntradaPaqueteArmada;
import com.arquisoft.entregables.application.proyectogrado.command.finder.ProyectoGradoBloqueadoPorIdFinder;
import com.arquisoft.entregables.application.proyectogrado.command.finder.ProyectoGradoPorIdFinder;
import com.arquisoft.entregables.domain.artefacto.ArtefactoDomain;
import com.arquisoft.entregables.domain.artefactoentregable.AgregacionArtefactosEntregableDomain;
import com.arquisoft.entregables.domain.entregableproyectogrado.GeneracionEntregableProyectoGradoDomain;
import com.arquisoft.entregables.domain.entregableproyectogrado.event.EntregableProyectoGradoGeneradoEvent;
import com.arquisoft.entregables.domain.estadoentregable.EstadoEntregable;
import com.arquisoft.entregables.domain.paqueteentregable.PaqueteEntregableDomain;
import com.arquisoft.entregables.domain.paqueteentregable.exception.PaqueteArmadoDesactualizadoException;
import com.arquisoft.entregables.domain.paqueteentregable.model.VigenciaPaqueteArmado;
import com.arquisoft.entregables.domain.proyectogrado.ProyectoGradoDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.ClaveMensaje;
import com.arquisoft.shared.message.key.entregables.EntregableProyectoGradoKey;
import com.arquisoft.shared.publisher.EventPublisher;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GenerarEntregableProyectoGradoUseCaseImplTest {

    @Mock
    private EntregableProyectoGradoOutputPort entregableProyectoGradoOutputPort;

    @Mock
    private EstadoEntregableProyectoGradoOutputPort estadoEntregableProyectoGradoOutputPort;

    @Mock
    private PaqueteEntregableOutputPort paqueteEntregableOutputPort;

    @Mock
    private ArmadoPaqueteOutputPort armadoPaqueteOutputPort;

    @Mock
    private EjecutorTransaccionalOutputPort ejecutorTransaccionalOutputPort;

    @Mock
    private ProyectoGradoPorIdFinder proyectoGradoPorIdFinder;

    @Mock
    private ProyectoGradoBloqueadoPorIdFinder proyectoGradoBloqueadoPorIdFinder;

    @Mock
    private EntregableVigenteExisteFinder entregableVigenteExisteFinder;

    @Mock
    private AsesorVigenteFinder asesorVigenteFinder;

    @Mock
    private AsesorVigenteProyectoGradoFinder asesorVigenteProyectoGradoFinder;

    @Mock
    private UltimaVersionEntregableProyectoGradoFinder ultimaVersionEntregableProyectoGradoFinder;

    @Mock
    private ArtefactosVigentesProyectoFinder artefactosVigentesProyectoFinder;

    @Mock
    private PaquetesProyectoGradoFinder paquetesProyectoGradoFinder;

    @Mock
    private GenerarEntregableProyectoGradoValidator generarEntregableProyectoGradoValidator;

    @Mock
    private ConfirmarGeneracionEntregableProyectoGradoValidator confirmarGeneracionValidator;

    @Mock
    private AgregarArtefactosEntregableUseCase agregarArtefactosEntregableUseCase;

    @Mock
    private EventPublisher eventPublisher;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private GenerarEntregableProyectoGradoUseCaseImpl useCase;

    @SuppressWarnings("unchecked")
    private void ejecutarTransacciones() {
        lenient().when(ejecutorTransaccionalOutputPort.enLectura(any()))
                .thenAnswer(invocacion -> ((Supplier<Object>) invocacion.getArgument(0)).get());
        lenient().when(ejecutorTransaccionalOutputPort.enEscritura(any()))
                .thenAnswer(invocacion -> ((Supplier<Object>) invocacion.getArgument(0)).get());
    }

    private void prepararLecturas(java.util.UUID proyecto, java.util.UUID asesor, ProyectoGradoDomain proyectoDomain,
                                  ArtefactoDomain artefacto) {
        ejecutarTransacciones();
        when(proyectoGradoPorIdFinder.obtener(proyecto)).thenReturn(proyectoDomain);
        when(proyectoGradoBloqueadoPorIdFinder.obtener(proyecto)).thenReturn(proyectoDomain);
        when(entregableVigenteExisteFinder.obtener(proyecto)).thenReturn(false);
        when(asesorVigenteFinder.obtener(asesor)).thenReturn(true);
        when(asesorVigenteProyectoGradoFinder.obtener(proyecto)).thenReturn(asesor);
        when(ultimaVersionEntregableProyectoGradoFinder.obtener(proyecto)).thenReturn(2);
        when(artefactosVigentesProyectoFinder.obtener(proyecto)).thenReturn(List.of(artefacto));
    }

    private static ArtefactoDomain artefacto(java.util.UUID proyecto) {
        return ArtefactoDomain.crear(UtilUUID.generarNuevoUUID(), proyecto, "Documento",
                new ArtefactoDomain.VersionCerrada(UtilUUID.generarNuevoUUID(), 2, Instant.now()),
                new ArtefactoDomain.DatosRepositorio(
                        UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), "Repositorio"));
    }

    private static PaqueteEntregableDomain paqueteAnterior() {
        return PaqueteEntregableDomain.crear(UtilUUID.generarNuevoUUID(), "bucket", "paquetes/a.zip",
                "a.zip", 10L, Instant.now());
    }

    @Test
    void debeArmarFueraDeLaTransaccionPersistirFotoYBorrarAnteriores_cuandoLasDosLecturasCoinciden() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var entrada = GeneracionEntregableProyectoGradoDomain.crear(proyecto, asesor);
        var proyectoDomain = ProyectoGradoDomain.crear(proyecto, "Sistema de gestion");
        var artefacto = artefacto(proyecto);
        var anterior = paqueteAnterior();
        prepararLecturas(proyecto, asesor, proyectoDomain, artefacto);
        when(paquetesProyectoGradoFinder.obtener(proyecto)).thenReturn(List.of(anterior));
        when(armadoPaqueteOutputPort.armar(any())).thenReturn(new ArchivoPaqueteArmado(
                "bucket", "paquetes/nuevo.zip", "nuevo.zip", 20L,
                List.of(new EntradaPaqueteArmada(artefacto.getId(), "001-Documento/v2-archivo.pdf"))));

        // Act
        var id = useCase.ejecutar(entrada);

        // Assert
        var captor = ArgumentCaptor.forClass(EntregableProyectoGradoEntity.class);
        var captorEstado = ArgumentCaptor.forClass(EstadoEntregableProyectoGradoEntity.class);
        var captorPaquete = ArgumentCaptor.forClass(PaqueteEntregableEntity.class);
        var captorFoto = ArgumentCaptor.forClass(AgregacionArtefactosEntregableDomain.class);
        var orden = inOrder(ejecutorTransaccionalOutputPort, generarEntregableProyectoGradoValidator,
                armadoPaqueteOutputPort, confirmarGeneracionValidator, entregableProyectoGradoOutputPort,
                estadoEntregableProyectoGradoOutputPort, paqueteEntregableOutputPort,
                agregarArtefactosEntregableUseCase, eventPublisher);
        orden.verify(ejecutorTransaccionalOutputPort).enLectura(any());
        orden.verify(generarEntregableProyectoGradoValidator)
                .validar(entrada, proyectoDomain, true, asesor, false, List.of(artefacto));
        orden.verify(armadoPaqueteOutputPort).armar(any());
        orden.verify(ejecutorTransaccionalOutputPort).enEscritura(any());
        orden.verify(armadoPaqueteOutputPort).eliminarSiRevierte("bucket", "paquetes/nuevo.zip");
        orden.verify(generarEntregableProyectoGradoValidator)
                .validar(entrada, proyectoDomain, true, asesor, false, List.of(artefacto));
        orden.verify(confirmarGeneracionValidator).validar(proyectoDomain, 2, 2,
                Set.of(artefacto.getVersionArtefacto()), Set.of(artefacto.getVersionArtefacto()));
        orden.verify(entregableProyectoGradoOutputPort).registrar(captor.capture());
        orden.verify(estadoEntregableProyectoGradoOutputPort).registrar(captorEstado.capture());
        orden.verify(paqueteEntregableOutputPort).guardar(captorPaquete.capture());
        orden.verify(agregarArtefactosEntregableUseCase).ejecutar(captorFoto.capture());
        orden.verify(armadoPaqueteOutputPort).eliminarTrasConfirmar("bucket", "paquetes/a.zip");
        orden.verify(eventPublisher).publish(any(EntregableProyectoGradoGeneradoEvent.class));
        verify(armadoPaqueteOutputPort, times(1)).armar(any());
        assertThat(captorEstado.getValue().entregableProyectoGrado()).isEqualTo(id);
        assertThat(captorEstado.getValue().estadoEntregable()).isEqualTo(EstadoEntregable.NUEVO.getId());
        assertThat(captor.getValue().id()).isEqualTo(id);
        assertThat(captor.getValue().proyectoGrado()).isEqualTo(proyecto);
        assertThat(captor.getValue().asesor()).isEqualTo(asesor);
        assertThat(captor.getValue().version()).isEqualTo(3);
        assertThat(captorPaquete.getValue().entregableProyectoGrado()).isEqualTo(id);
        assertThat(captorPaquete.getValue().claveObjeto()).isEqualTo("paquetes/nuevo.zip");
        assertThat(captorFoto.getValue().getArtefactos()).singleElement().satisfies(foto -> {
            assertThat(foto.getArtefacto()).isEqualTo(artefacto.getId());
            assertThat(foto.getUriArchivo()).isEqualTo("001-Documento/v2-archivo.pdf");
            assertThat(foto.getVersionArtefacto()).isEqualTo(artefacto.getVersionArtefacto());
        });
        verify(logger).info(any(ClaveMensaje.class), eq(id), eq(proyecto), eq(3));
    }

    @Test
    void noDebeArmarNiPublicar_cuandoElValidatorRechaza() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var entrada = GeneracionEntregableProyectoGradoDomain.crear(proyecto, asesor);
        ejecutarTransacciones();
        when(proyectoGradoPorIdFinder.obtener(proyecto)).thenReturn(ProyectoGradoDomain.VACIO);
        when(entregableVigenteExisteFinder.obtener(proyecto)).thenReturn(false);
        when(asesorVigenteFinder.obtener(asesor)).thenReturn(true);
        when(asesorVigenteProyectoGradoFinder.obtener(proyecto)).thenReturn(asesor);
        when(ultimaVersionEntregableProyectoGradoFinder.obtener(proyecto)).thenReturn(0);
        when(artefactosVigentesProyectoFinder.obtener(proyecto)).thenReturn(List.of());
        doThrow(new IllegalStateException("rechazado")).when(generarEntregableProyectoGradoValidator)
                .validar(any(), any(), anyBoolean(), any(), anyBoolean(), any());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(entrada)).isInstanceOf(IllegalStateException.class);
        verify(ejecutorTransaccionalOutputPort, never()).enEscritura(any());
        verify(armadoPaqueteOutputPort, never()).armar(any());
        verify(entregableProyectoGradoOutputPort, never()).registrar(any());
        verify(estadoEntregableProyectoGradoOutputPort, never()).registrar(any());
        verify(paqueteEntregableOutputPort, never()).guardar(any());
        verify(eventPublisher, never()).publish(any());
        verify(logger, never()).info(any(ClaveMensaje.class), any(), eq(proyecto), anyInt());
    }

    @Test
    void noDebePersistirNiPublicar_cuandoElPaqueteArmadoQuedoDesactualizadoBajoLock() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var entrada = GeneracionEntregableProyectoGradoDomain.crear(proyecto, asesor);
        var proyectoDomain = ProyectoGradoDomain.crear(proyecto, "Sistema de gestion");
        var artefacto = artefacto(proyecto);
        prepararLecturas(proyecto, asesor, proyectoDomain, artefacto);
        when(paquetesProyectoGradoFinder.obtener(proyecto)).thenReturn(List.of(paqueteAnterior()));
        when(armadoPaqueteOutputPort.armar(any())).thenReturn(new ArchivoPaqueteArmado(
                "bucket", "paquetes/nuevo.zip", "nuevo.zip", 20L, List.of()));
        var desactualizado = new PaqueteArmadoDesactualizadoException(new VigenciaPaqueteArmado(
                proyecto, "Sistema de gestion", 2, 3, Set.of(), Set.of()));
        doThrow(desactualizado).when(confirmarGeneracionValidator).validar(any(), anyInt(), anyInt(), any(), any());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(entrada)).isSameAs(desactualizado);
        var orden = inOrder(armadoPaqueteOutputPort, confirmarGeneracionValidator);
        orden.verify(armadoPaqueteOutputPort).armar(any());
        orden.verify(armadoPaqueteOutputPort).eliminarSiRevierte("bucket", "paquetes/nuevo.zip");
        orden.verify(confirmarGeneracionValidator).validar(any(), anyInt(), anyInt(), any(), any());
        verify(proyectoGradoPorIdFinder, times(1)).obtener(proyecto);
        verify(proyectoGradoBloqueadoPorIdFinder, times(1)).obtener(proyecto);
        verify(logger, times(2)).debug(eq(EntregableProyectoGradoKey.LOG_VERIFICACION_GENERAR), anyBoolean(), any(),
                anyBoolean(), anyBoolean(), anyBoolean(), anyInt(), anyInt());
        verify(entregableProyectoGradoOutputPort, never()).registrar(any());
        verify(estadoEntregableProyectoGradoOutputPort, never()).registrar(any());
        verify(paqueteEntregableOutputPort, never()).guardar(any());
        verify(agregarArtefactosEntregableUseCase, never()).ejecutar(any());
        verify(armadoPaqueteOutputPort, never()).eliminarTrasConfirmar(any(), any());
        verify(eventPublisher, never()).publish(any());
    }
}
