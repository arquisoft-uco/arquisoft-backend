package com.arquisoft.artefactos.infrastructure.versionartefacto.command.secondaryadapter.repository;

import com.arquisoft.artefactos.application.versionartefacto.command.secondaryport.entity.VersionArtefactoEntity;
import com.arquisoft.artefactos.infrastructure.artefacto.command.secondaryadapter.entity.ArtefactoJpaEntity;
import com.arquisoft.artefactos.infrastructure.revisionasesor.support.EstudianteArtefactoTestEntity;
import com.arquisoft.artefactos.infrastructure.revisionasesor.support.RevisionAsesorTestEntity;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.EliminarVersionArtefactoKey;
import com.arquisoft.shared.message.key.artefactos.VersionArtefactoKey;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@DataJpaTest
class VersionArtefactoCommandOutputAdapterTest {

    @Autowired
    private VersionArtefactoCommandRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    private final AppLogger logger = mock(AppLogger.class);

    private VersionArtefactoCommandOutputAdapter adapter() {
        return new VersionArtefactoCommandOutputAdapter(repository, logger);
    }

    private static VersionArtefactoEntity version(UUID artefacto, int numero) {
        var id = UtilUUID.generarNuevoUUID();
        return new VersionArtefactoEntity(id, artefacto, numero, Instant.now(), "versiones/" + id, 1024L);
    }

    @Test
    void debeRegistrar() {
        // Arrange
        var entity = version(UtilUUID.generarNuevoUUID(), 1);

        // Act
        adapter().registrar(entity);
        entityManager.flush();
        entityManager.clear();

        // Assert
        var guardado = repository.findById(entity.id()).orElseThrow();
        assertThat(guardado.getArtefacto()).isEqualTo(entity.artefacto());
        assertThat(guardado.getNumeroVersion()).isEqualTo(1);
        assertThat(guardado.getUriArchivo()).isEqualTo(entity.uriArchivo());
        verify(logger).debug(VersionArtefactoKey.LOG_GUARDADA, entity.id());
    }

    @Test
    void debeRetornarCero_cuandoSinVersiones() {
        // Act & Assert
        assertThat(adapter().ultimoNumeroPorArtefacto(UtilUUID.generarNuevoUUID())).isZero();
    }

    @Test
    void debeRetornarUltimoNumero_porArtefacto() {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();
        var otro = UtilUUID.generarNuevoUUID();
        adapter().registrar(version(artefacto, 1));
        adapter().registrar(version(artefacto, 2));
        adapter().registrar(version(otro, 7));
        entityManager.flush();

        // Act & Assert
        assertThat(adapter().ultimoNumeroPorArtefacto(artefacto)).isEqualTo(2);
        assertThat(adapter().ultimoNumeroPorArtefacto(otro)).isEqualTo(7);
    }

    @Test
    void debeObtenerPorId_cuandoExiste() {
        // Arrange
        var entity = version(UtilUUID.generarNuevoUUID(), 4);
        adapter().registrar(entity);
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter().obtenerPorId(entity.id());

        // Assert
        assertThat(resultado).hasValueSatisfying(encontrada -> {
            assertThat(encontrada.id()).isEqualTo(entity.id());
            assertThat(encontrada.artefacto()).isEqualTo(entity.artefacto());
            assertThat(encontrada.numeroVersion()).isEqualTo(4);
            assertThat(encontrada.uriArchivo()).isEqualTo(entity.uriArchivo());
        });
        assertThat(adapter().obtenerPorId(UtilUUID.generarNuevoUUID())).isEmpty();
    }

    @Test
    void debeSerUltima_cuandoNoHayMayor() {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();
        var primera = version(artefacto, 1);
        var segunda = version(artefacto, 2);
        adapter().registrar(primera);
        adapter().registrar(segunda);
        adapter().registrar(version(UtilUUID.generarNuevoUUID(), 9));
        entityManager.flush();

        // Act & Assert
        assertThat(adapter().esUltimaDeSuArtefacto(segunda.id())).isTrue();
    }

    @Test
    void noDebeSerUltima_cuandoHayMayor() {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();
        var primera = version(artefacto, 1);
        adapter().registrar(primera);
        adapter().registrar(version(artefacto, 2));
        entityManager.flush();

        // Act & Assert
        assertThat(adapter().esUltimaDeSuArtefacto(primera.id())).isFalse();
    }

    @Test
    void debeObtenerBloqueada_cuandoExisteYVacioCuandoNo() {
        // Arrange
        var entity = version(UtilUUID.generarNuevoUUID(), 3);
        adapter().registrar(entity);
        entityManager.flush();
        entityManager.clear();

        // Act
        var existente = adapter().obtenerBloqueadaPorId(entity.id());
        var ausente = adapter().obtenerBloqueadaPorId(UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(existente).hasValueSatisfying(encontrada -> {
            assertThat(encontrada.id()).isEqualTo(entity.id());
            assertThat(encontrada.numeroVersion()).isEqualTo(3);
            assertThat(encontrada.uriArchivo()).isEqualTo(entity.uriArchivo());
        });
        assertThat(ausente).isEmpty();
    }

    @Test
    void debeExistirParticipacionSoloDelEstudianteVinculado() {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();
        var vinculado = UtilUUID.generarNuevoUUID();
        var ajeno = UtilUUID.generarNuevoUUID();
        var entity = version(artefacto, 1);
        adapter().registrar(entity);
        entityManager.persist(new EstudianteArtefactoTestEntity(UtilUUID.generarNuevoUUID(), artefacto, vinculado));
        entityManager.flush();

        // Act & Assert
        assertThat(adapter().existeParticipacion(entity.id(), vinculado)).isTrue();
        assertThat(adapter().existeParticipacion(entity.id(), ajeno)).isFalse();
        assertThat(adapter().existeParticipacion(UtilUUID.generarNuevoUUID(), vinculado)).isFalse();
    }

    @Test
    void debeEliminarSoloLaVersionIndicada_yReutilizarElNumero() {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();
        var primera = version(artefacto, 1);
        var segunda = version(artefacto, 2);
        adapter().registrar(primera);
        adapter().registrar(segunda);
        entityManager.flush();

        // Act
        adapter().eliminar(segunda.id());
        entityManager.flush();
        entityManager.clear();

        // Assert
        assertThat(repository.findById(segunda.id())).isEmpty();
        assertThat(repository.findById(primera.id())).isPresent();
        assertThat(adapter().ultimoNumeroPorArtefacto(artefacto)).isEqualTo(1);
        verify(logger).debug(EliminarVersionArtefactoKey.LOG_ELIMINADO, segunda.id());
    }

    private UUID artefactoDe(UUID proyecto, boolean omitido) {
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persist(ArtefactoJpaEntity.builder()
                .id(id)
                .proyectoGrado(proyecto)
                .versionRepositorioArtefacto(UtilUUID.generarNuevoUUID())
                .asesor(UtilUUID.generarNuevoUUID())
                .omitidoEn(omitido ? Instant.now() : null)
                .build());
        return id;
    }

    private UUID versionDe(UUID artefacto, int numero, long tamano) {
        var id = UtilUUID.generarNuevoUUID();
        adapter().registrar(new VersionArtefactoEntity(id, artefacto, numero, Instant.now(), "versiones/" + id, tamano));
        return id;
    }

    private void revisionDe(UUID version, String estado) {
        entityManager.persist(new RevisionAsesorTestEntity(UtilUUID.generarNuevoUUID(), version, estado));
    }

    @Test
    void debePersistirTamano() {
        // Arrange
        var entity = new VersionArtefactoEntity(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), 1,
                Instant.now(), "versiones/x", 7_340_032L);

        // Act
        adapter().registrar(entity);
        entityManager.flush();
        entityManager.clear();

        // Assert
        assertThat(repository.findById(entity.id()).orElseThrow().getTamanoBytes()).isEqualTo(7_340_032L);
    }

    @Test
    void debeSumarTodas_cuandoSinRevisionCerrada() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var artefacto = artefactoDe(proyecto, false);
        var otroArtefacto = artefactoDe(proyecto, false);
        var primera = versionDe(artefacto, 1, 100L);
        versionDe(artefacto, 2, 200L);
        versionDe(otroArtefacto, 1, 50L);
        revisionDe(primera, "ENVIADA");
        entityManager.flush();

        // Act & Assert
        assertThat(adapter().tamanoVigenteDelProyectoDeArtefacto(artefacto, "CERRADA")).isEqualTo(350L);
    }

    @Test
    void debeSumarDesdeLaUltimaCerrada() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var artefacto = artefactoDe(proyecto, false);
        var otroArtefacto = artefactoDe(proyecto, false);
        versionDe(artefacto, 1, 100L);
        var segunda = versionDe(artefacto, 2, 200L);
        var tercera = versionDe(artefacto, 3, 400L);
        versionDe(otroArtefacto, 1, 50L);
        revisionDe(segunda, "CERRADA");
        revisionDe(tercera, "PENDIENTE");
        entityManager.flush();

        // Act & Assert
        assertThat(adapter().tamanoVigenteDelProyectoDeArtefacto(artefacto, "CERRADA")).isEqualTo(650L);
        assertThat(adapter().tamanoVigenteDelArtefacto(artefacto, "CERRADA")).isEqualTo(600L);
    }

    @Test
    void debeIgnorarOtrosProyectos() {
        // Arrange
        var artefacto = artefactoDe(UtilUUID.generarNuevoUUID(), false);
        var ajeno = artefactoDe(UtilUUID.generarNuevoUUID(), false);
        versionDe(artefacto, 1, 100L);
        versionDe(ajeno, 1, 999L);
        entityManager.flush();

        // Act & Assert
        assertThat(adapter().tamanoVigenteDelProyectoDeArtefacto(artefacto, "CERRADA")).isEqualTo(100L);
    }

    @Test
    void debeExcluirArtefactosOmitidos() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var activo = artefactoDe(proyecto, false);
        var omitido = artefactoDe(proyecto, true);
        versionDe(activo, 1, 100L);
        versionDe(omitido, 1, 1000L);
        entityManager.flush();

        // Act & Assert
        assertThat(adapter().tamanoVigenteDelProyectoDeArtefacto(activo, "CERRADA")).isEqualTo(100L);
    }

    @Test
    void debeSumarDesdeArtefactoOmitido_cuandoBaseEsOmitido() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var activo = artefactoDe(proyecto, false);
        var omitido = artefactoDe(proyecto, true);
        versionDe(activo, 1, 100L);
        versionDe(omitido, 1, 1000L);
        entityManager.flush();

        // Act & Assert
        assertThat(adapter().tamanoVigenteDelProyectoDeArtefacto(omitido, "CERRADA")).isEqualTo(100L);
    }

    @Test
    void debeRetornarCero_cuandoProyectoSinVersiones() {
        // Arrange
        var artefacto = artefactoDe(UtilUUID.generarNuevoUUID(), false);
        entityManager.flush();

        // Act & Assert
        assertThat(adapter().tamanoVigenteDelProyectoDeArtefacto(artefacto, "CERRADA")).isZero();
        assertThat(adapter().tamanoVigenteDelArtefacto(artefacto, "CERRADA")).isZero();
    }

    @Test
    void debeCalcularPesoDeArtefactoOmitido_sinFiltrarPorOmision() {
        // Arrange
        var omitido = artefactoDe(UtilUUID.generarNuevoUUID(), true);
        versionDe(omitido, 1, 50L);
        versionDe(omitido, 2, 70L);
        entityManager.flush();

        // Act & Assert
        assertThat(adapter().tamanoVigenteDelArtefacto(omitido, "CERRADA")).isEqualTo(120L);
    }

    @Test
    void debeListarUris_delProyectoIncluyendoOmitidosYSinOtrosProyectos() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var activo = artefactoDe(proyecto, false);
        var omitido = artefactoDe(proyecto, true);
        var ajeno = artefactoDe(UtilUUID.generarNuevoUUID(), false);
        var v1 = versionDe(activo, 1, 10L);
        var v2 = versionDe(activo, 2, 10L);
        var v3 = versionDe(omitido, 1, 10L);
        versionDe(ajeno, 1, 10L);
        entityManager.flush();

        // Act
        var uris = adapter().obtenerUrisArchivoPorProyectoGrado(proyecto);

        // Assert
        assertThat(uris).containsExactlyInAnyOrder("versiones/" + v1, "versiones/" + v2, "versiones/" + v3);
    }

    @Test
    void debeRetornarVacio_cuandoElProyectoNoTieneVersiones() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        artefactoDe(proyecto, false);
        entityManager.flush();

        // Act & Assert
        assertThat(adapter().obtenerUrisArchivoPorProyectoGrado(proyecto)).isEmpty();
    }
}
