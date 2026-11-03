package com.arquisoft.artefactos.infrastructure.versionartefacto.command.secondaryadapter.repository;

import com.arquisoft.artefactos.application.versionartefacto.command.secondaryport.entity.VersionArtefactoEntity;
import com.arquisoft.artefactos.infrastructure.revisionasesor.support.EstudianteArtefactoTestEntity;
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
        return new VersionArtefactoEntity(id, artefacto, numero, Instant.now(), "versiones/" + id);
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
}
