package com.arquisoft.artefactos.infrastructure.paqueteentregable.command.secondaryadapter.repository;

import com.arquisoft.artefactos.application.paqueteentregable.command.secondaryport.entity.PaqueteEntregableEntity;
import com.arquisoft.artefactos.infrastructure.artefacto.command.secondaryadapter.entity.ArtefactoJpaEntity;
import com.arquisoft.artefactos.infrastructure.paqueteentregable.command.secondaryadapter.entity.PaqueteEntregableJpaEntity;
import com.arquisoft.artefactos.infrastructure.paqueteentregable.command.secondaryadapter.projection.ArtefactoParaPaqueteProjection;
import com.arquisoft.artefactos.infrastructure.proyectogrado.command.secondaryadapter.entity.ProyectoGradoJpaEntity;
import com.arquisoft.artefactos.infrastructure.revisionasesor.command.secondaryadapter.entity.RevisionAsesorJpaEntity;
import com.arquisoft.artefactos.infrastructure.versionartefacto.command.secondaryadapter.entity.VersionArtefactoJpaEntity;
import com.arquisoft.artefactos.infrastructure.versionrepositorioartefacto.command.secondaryadapter.entity.VersionRepositorioArtefactoJpaEntity;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.PaqueteEntregableKey;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.data.jpa.repository.NativeQuery;

import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DataJpaTest
class PaqueteEntregableCommandOutputAdapterTest {

    private static final String CERRADA = "CERRADA";
    private static final String ABIERTA = "ABIERTA";

    @Autowired
    private PaqueteEntregableCommandRepository paqueteEntregableCommandRepository;

    @Autowired
    private TestEntityManager entityManager;

    private final AppLogger logger = mock(AppLogger.class);

    private PaqueteEntregableCommandOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new PaqueteEntregableCommandOutputAdapter(paqueteEntregableCommandRepository, logger);
    }

    private UUID sembrarProyecto() {
        var proyecto = UtilUUID.generarNuevoUUID();
        entityManager.persist(ProyectoGradoJpaEntity.builder().id(proyecto).nombre("Proyecto").build());
        return proyecto;
    }

    private UUID sembrarArtefacto(UUID proyecto, String documento, Instant omitidoEn) {
        var repositorio = UtilUUID.generarNuevoUUID();
        entityManager.persist(VersionRepositorioArtefactoJpaEntity.builder()
                .id(repositorio).nombreDocumento(documento).versionDocumento(1)
                .repositorioArtefacto(UtilUUID.generarNuevoUUID()).nombreRepositorio("Repo")
                .nombreArchivo("a.pdf").tipoContenido("application/pdf").tamanoBytes(1L).build());
        var artefacto = UtilUUID.generarNuevoUUID();
        entityManager.persist(ArtefactoJpaEntity.builder()
                .id(artefacto).proyectoGrado(proyecto).versionRepositorioArtefacto(repositorio)
                .asesor(UtilUUID.generarNuevoUUID()).omitidoEn(omitidoEn).build());
        return artefacto;
    }

    private UUID sembrarVersion(UUID artefacto, int numero, String estadoRevision) {
        var version = UtilUUID.generarNuevoUUID();
        entityManager.persist(VersionArtefactoJpaEntity.builder()
                .id(version).artefacto(artefacto).numeroVersion(numero)
                .fechaCreacion(Instant.parse("2026-03-01T00:00:00Z")).uriArchivo("uri-" + numero).tamanoBytes(10L)
                .build());
        if (estadoRevision != null) {
            entityManager.persist(RevisionAsesorJpaEntity.builder()
                    .id(UtilUUID.generarNuevoUUID()).versionArtefacto(version).estado(estadoRevision)
                    .fechaCreacion(Instant.parse("2026-03-02T00:00:00Z")).build());
        }
        return version;
    }

    private record Fila(UUID artefacto, String nombreDocumento, UUID versionArtefacto, int numeroVersionCerrada,
                        String uriArchivo, Instant fechaVersion, int ultimaVersion) {}

    private static UUID comoUuid(Object valor) {
        if (valor instanceof byte[] bytes) {
            var buffer = ByteBuffer.wrap(bytes);
            return new UUID(buffer.getLong(), buffer.getLong());
        }
        return (UUID) valor;
    }

    private static Instant comoInstante(Object valor) {
        if (valor instanceof Timestamp timestamp) {
            return timestamp.toInstant();
        }
        if (valor instanceof OffsetDateTime fecha) {
            return fecha.toInstant();
        }
        return (Instant) valor;
    }

    @SuppressWarnings("unchecked")
    private List<Fila> consultar(UUID proyecto) {
        try {
            var sql = PaqueteEntregableCommandRepository.class
                    .getMethod("obtenerArtefactosParaPaquete", UUID.class, String.class)
                    .getAnnotation(NativeQuery.class).value();
            var filas = (List<Object[]>) entityManager.getEntityManager().createNativeQuery(sql)
                    .setParameter("proyectoGrado", proyecto)
                    .setParameter("estadoCerrada", CERRADA)
                    .getResultList();
            return filas.stream().map(f -> new Fila(comoUuid(f[0]), (String) f[1],
                    f[2] == null ? null : comoUuid(f[2]), ((Number) f[3]).intValue(), (String) f[4],
                    f[5] == null ? null : comoInstante(f[5]), ((Number) f[6]).intValue())).toList();
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }
    }

    @Test
    void debeTraducirNulosDeLaProyeccionAValoresPorDefecto_cuandoNoHayVersionCerrada() {
        // Arrange
        var repositorio = mock(PaqueteEntregableCommandRepository.class);
        var artefacto = UtilUUID.generarNuevoUUID();
        var sinCerrar = mock(ArtefactoParaPaqueteProjection.class);
        when(sinCerrar.getArtefacto()).thenReturn(artefacto);
        when(sinCerrar.getNombreDocumento()).thenReturn("Doc");
        when(sinCerrar.getNumeroVersionCerrada()).thenReturn(0);
        when(sinCerrar.getUltimaVersion()).thenReturn(2);
        var cerrada = mock(ArtefactoParaPaqueteProjection.class);
        var version = UtilUUID.generarNuevoUUID();
        var fecha = Instant.parse("2026-03-01T00:00:00Z");
        when(cerrada.getArtefacto()).thenReturn(UtilUUID.generarNuevoUUID());
        when(cerrada.getNombreDocumento()).thenReturn("Otro");
        when(cerrada.getVersionArtefacto()).thenReturn(version);
        when(cerrada.getNumeroVersionCerrada()).thenReturn(1);
        when(cerrada.getUriArchivo()).thenReturn("uri");
        when(cerrada.getFechaVersion()).thenReturn(fecha);
        when(cerrada.getUltimaVersion()).thenReturn(1);
        var proyecto = UtilUUID.generarNuevoUUID();
        when(repositorio.obtenerArtefactosParaPaquete(proyecto, CERRADA)).thenReturn(List.of(sinCerrar, cerrada));

        // Act
        var filas = new PaqueteEntregableCommandOutputAdapter(repositorio, logger)
                .obtenerArtefactosParaPaquete(proyecto, CERRADA);

        // Assert
        assertThat(filas).hasSize(2);
        assertThat(filas.get(0).artefacto()).isEqualTo(artefacto);
        assertThat(UtilUUID.esPorDefecto(filas.get(0).versionArtefacto())).isTrue();
        assertThat(filas.get(0).uriArchivo()).isEmpty();
        assertThat(filas.get(0).fechaVersion()).isEqualTo(UtilFecha.VACIO);
        assertThat(filas.get(0).ultimaVersion()).isEqualTo(2);
        assertThat(filas.get(1).versionArtefacto()).isEqualTo(version);
        assertThat(filas.get(1).numeroVersionCerrada()).isEqualTo(1);
        assertThat(filas.get(1).uriArchivo()).isEqualTo("uri");
        assertThat(filas.get(1).fechaVersion()).isEqualTo(fecha);
    }

    @Test
    void debeRegistrarPaquete() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var entregable = UtilUUID.generarNuevoUUID();
        var proyecto = sembrarProyecto();
        var generadoEn = Instant.parse("2026-11-08T10:00:00Z");

        // Act
        adapter.registrar(new PaqueteEntregableEntity(id, entregable, proyecto, 2, "paquetes/x.zip", 99L, generadoEn));
        entityManager.flush();
        entityManager.clear();

        // Assert
        var guardado = entityManager.find(PaqueteEntregableJpaEntity.class, id);
        assertThat(guardado.getEntregableProyectoGrado()).isEqualTo(entregable);
        assertThat(guardado.getProyectoGrado()).isEqualTo(proyecto);
        assertThat(guardado.getVersionEntregable()).isEqualTo(2);
        assertThat(guardado.getClaveObjeto()).isEqualTo("paquetes/x.zip");
        assertThat(guardado.getTamanoBytes()).isEqualTo(99L);
        assertThat(guardado.getGeneradoEn()).isEqualTo(generadoEn);
        verify(logger).debug(PaqueteEntregableKey.LOG_GUARDADO, id);
    }

    @Test
    void debeObtenerSoloLasClavesDeVersionesMenoresDelMismoProyecto() {
        // Arrange
        var proyecto = sembrarProyecto();
        var otroProyecto = sembrarProyecto();
        var generadoEn = Instant.parse("2026-11-08T10:00:00Z");
        adapter.registrar(new PaqueteEntregableEntity(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                proyecto, 1, "paquetes/v1.zip", 1L, generadoEn));
        adapter.registrar(new PaqueteEntregableEntity(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                proyecto, 2, "paquetes/v2.zip", 1L, generadoEn));
        adapter.registrar(new PaqueteEntregableEntity(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                proyecto, 3, "paquetes/v3.zip", 1L, generadoEn));
        adapter.registrar(new PaqueteEntregableEntity(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                otroProyecto, 1, "paquetes/ajeno.zip", 1L, generadoEn));
        entityManager.flush();
        entityManager.clear();

        // Act
        var claves = adapter.obtenerClavesAnteriores(proyecto, 3);
        var sinAnteriores = adapter.obtenerClavesAnteriores(proyecto, 1);

        // Assert
        assertThat(claves).containsExactlyInAnyOrder("paquetes/v1.zip", "paquetes/v2.zip");
        assertThat(sinAnteriores).isEmpty();
    }

    @Test
    void debeObtenerTodasLasClavesDelProyecto_sinFiltrarPorVersion() {
        // Arrange
        var proyecto = sembrarProyecto();
        var otroProyecto = sembrarProyecto();
        var generadoEn = Instant.parse("2026-11-08T10:00:00Z");
        adapter.registrar(new PaqueteEntregableEntity(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                proyecto, 1, "paquetes/v1.zip", 1L, generadoEn));
        adapter.registrar(new PaqueteEntregableEntity(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                proyecto, 2, "paquetes/v2.zip", 1L, generadoEn));
        adapter.registrar(new PaqueteEntregableEntity(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                otroProyecto, 1, "paquetes/ajeno.zip", 1L, generadoEn));
        entityManager.flush();
        entityManager.clear();

        // Act
        var claves = adapter.obtenerClavesPorProyectoGrado(proyecto);
        var sinPaquetes = adapter.obtenerClavesPorProyectoGrado(UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(claves).containsExactlyInAnyOrder("paquetes/v1.zip", "paquetes/v2.zip");
        assertThat(sinPaquetes).isEmpty();
    }

    @Test
    void debeResponderExistencia_porEntregable() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        var proyecto = sembrarProyecto();
        adapter.registrar(new PaqueteEntregableEntity(UtilUUID.generarNuevoUUID(), entregable, proyecto, 1,
                "paquetes/x.zip", 1L, Instant.now()));
        entityManager.flush();

        // Act & Assert
        assertThat(adapter.existePorEntregable(entregable)).isTrue();
        assertThat(adapter.existePorEntregable(UtilUUID.generarNuevoUUID())).isFalse();
    }

    @Test
    void debeDevolverUltimaVersionCerradaAunqueHayaUnaPosteriorSinCerrar() {
        // Arrange
        var proyecto = sembrarProyecto();
        var artefacto = sembrarArtefacto(proyecto, "Doc", null);
        sembrarVersion(artefacto, 1, CERRADA);
        var cerrada = sembrarVersion(artefacto, 2, CERRADA);
        sembrarVersion(artefacto, 3, ABIERTA);
        entityManager.flush();
        entityManager.clear();

        // Act
        var filas = consultar(proyecto);

        // Assert
        assertThat(filas).singleElement().satisfies(fila -> {
            assertThat(fila.artefacto()).isEqualTo(artefacto);
            assertThat(fila.nombreDocumento()).isEqualTo("Doc");
            assertThat(fila.versionArtefacto()).isEqualTo(cerrada);
            assertThat(fila.numeroVersionCerrada()).isEqualTo(2);
            assertThat(fila.uriArchivo()).isEqualTo("uri-2");
            assertThat(fila.fechaVersion()).isEqualTo(Instant.parse("2026-03-01T00:00:00Z"));
            assertThat(fila.ultimaVersion()).isEqualTo(3);
        });
    }

    @Test
    @SuppressWarnings("unchecked")
    void debeTraerRepositorioYNombre_cuandoObtieneArtefactosParaPaquete() throws Exception {
        // Arrange
        var proyecto = sembrarProyecto();
        var versionRepositorio = UtilUUID.generarNuevoUUID();
        var repositorio = UtilUUID.generarNuevoUUID();
        entityManager.persist(VersionRepositorioArtefactoJpaEntity.builder()
                .id(versionRepositorio).nombreDocumento("Doc").versionDocumento(1)
                .repositorioArtefacto(repositorio).nombreRepositorio("Repo Central")
                .nombreArchivo("a.pdf").tipoContenido("application/pdf").tamanoBytes(1L).build());
        entityManager.persist(ArtefactoJpaEntity.builder()
                .id(UtilUUID.generarNuevoUUID()).proyectoGrado(proyecto).versionRepositorioArtefacto(versionRepositorio)
                .asesor(UtilUUID.generarNuevoUUID()).build());
        entityManager.flush();
        entityManager.clear();
        var sql = PaqueteEntregableCommandRepository.class
                .getMethod("obtenerArtefactosParaPaquete", UUID.class, String.class)
                .getAnnotation(NativeQuery.class).value();

        // Act
        var filas = (List<Object[]>) entityManager.getEntityManager().createNativeQuery(sql)
                .setParameter("proyectoGrado", proyecto)
                .setParameter("estadoCerrada", CERRADA)
                .getResultList();

        // Assert
        assertThat(filas).singleElement().satisfies(fila -> {
            assertThat(comoUuid(fila[7])).isEqualTo(versionRepositorio);
            assertThat(comoUuid(fila[8])).isEqualTo(repositorio);
            assertThat(fila[9]).isEqualTo("Repo Central");
        });
    }

    @Test
    void debeExcluirArtefactosOmitidos() {
        // Arrange
        var proyecto = sembrarProyecto();
        var vigente = sembrarArtefacto(proyecto, "Vigente", null);
        sembrarArtefacto(proyecto, "Omitido", Instant.parse("2026-02-01T00:00:00Z"));
        entityManager.flush();
        entityManager.clear();

        // Act
        var filas = consultar(proyecto);

        // Assert
        assertThat(filas).extracting(fila -> fila.artefacto()).containsExactly(vigente);
    }

    @Test
    void debeClasificarSinVersionesYSinRevisionCerrada() {
        // Arrange
        var proyecto = sembrarProyecto();
        var sinVersiones = sembrarArtefacto(proyecto, "A sin versiones", null);
        var sinCerrar = sembrarArtefacto(proyecto, "B sin cerrar", null);
        sembrarVersion(sinCerrar, 1, ABIERTA);
        sembrarVersion(sinCerrar, 2, null);
        entityManager.flush();
        entityManager.clear();

        // Act
        var filas = consultar(proyecto);

        // Assert
        assertThat(filas).hasSize(2);
        assertThat(filas.get(0).artefacto()).isEqualTo(sinVersiones);
        assertThat(filas.get(0).numeroVersionCerrada()).isZero();
        assertThat(filas.get(0).ultimaVersion()).isZero();
        assertThat(filas.get(0).uriArchivo()).isNull();
        assertThat(filas.get(0).versionArtefacto()).isNull();
        assertThat(filas.get(1).artefacto()).isEqualTo(sinCerrar);
        assertThat(filas.get(1).numeroVersionCerrada()).isZero();
        assertThat(filas.get(1).ultimaVersion()).isEqualTo(2);
    }

    @Test
    void debeIgnorarArtefactosDeOtroProyecto() {
        // Arrange
        var proyecto = sembrarProyecto();
        var otroProyecto = sembrarProyecto();
        sembrarArtefacto(otroProyecto, "Ajeno", null);
        entityManager.flush();
        entityManager.clear();

        // Act & Assert
        assertThat(consultar(proyecto)).isEmpty();
    }
}
