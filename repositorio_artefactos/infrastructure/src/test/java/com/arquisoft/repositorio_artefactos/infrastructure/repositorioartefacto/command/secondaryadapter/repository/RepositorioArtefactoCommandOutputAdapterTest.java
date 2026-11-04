package com.arquisoft.repositorio_artefactos.infrastructure.repositorioartefacto.command.secondaryadapter.repository;

import com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.secondaryport.entity.RepositorioArtefactoEntity;
import com.arquisoft.repositorio_artefactos.infrastructure.administrador.command.secondaryadapter.entity.AdministradorJpaEntity;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.repositorio_artefactos.RepositorioArtefactoKey;
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
class RepositorioArtefactoCommandOutputAdapterTest {

    @Autowired
    private RepositorioArtefactoCommandRepository repositorioArtefactoCommandRepository;

    @Autowired
    private TestEntityManager entityManager;

    private final AppLogger logger = mock(AppLogger.class);

    private UUID sembrarAdministrador() {
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persistAndFlush(AdministradorJpaEntity.builder()
                .id(id).identificador("123").nombre("Ana Perez")
                .email("ana@uco.edu.co").ocurridoEn(Instant.now()).build());
        return id;
    }

    @Test
    void debeRegistrarElRepositorio_cuandoSePersiste() {
        // Arrange
        var adapter = new RepositorioArtefactoCommandOutputAdapter(repositorioArtefactoCommandRepository, logger);
        var administrador = sembrarAdministrador();
        var id = UtilUUID.generarNuevoUUID();

        // Act
        adapter.registrar(new RepositorioArtefactoEntity(id, "Repo", "Descripcion", administrador));
        entityManager.flush();
        entityManager.clear();

        // Assert
        var guardado = repositorioArtefactoCommandRepository.findById(id).orElseThrow();
        assertThat(guardado.getNombreRepositorio()).isEqualTo("Repo");
        assertThat(guardado.getDescripcion()).isEqualTo("Descripcion");
        assertThat(guardado.getAdministrador()).isEqualTo(administrador);
        verify(logger).debug(RepositorioArtefactoKey.LOG_GUARDADO, id);
    }

    @Test
    void debeIndicarSiElNombreExiste_cuandoSeConsulta() {
        // Arrange
        var adapter = new RepositorioArtefactoCommandOutputAdapter(repositorioArtefactoCommandRepository, logger);
        adapter.registrar(new RepositorioArtefactoEntity(
                UtilUUID.generarNuevoUUID(), "Repo", "Descripcion", sembrarAdministrador()));
        entityManager.flush();

        // Act
        var mismoNombre = adapter.existePorNombreRepositorio("Repo");
        var otroNombre = adapter.existePorNombreRepositorio("Otro");

        // Assert
        assertThat(mismoNombre).isTrue();
        assertThat(otroNombre).isFalse();
    }

    @Test
    void debeObtenerPorId_cuandoExiste() {
        // Arrange
        var adapter = new RepositorioArtefactoCommandOutputAdapter(repositorioArtefactoCommandRepository, logger);
        var administrador = sembrarAdministrador();
        var id = UtilUUID.generarNuevoUUID();
        adapter.registrar(new RepositorioArtefactoEntity(id, "Repo", "Descripcion", administrador));
        entityManager.flush();
        entityManager.clear();

        // Act
        var encontrado = adapter.obtenerPorId(id);

        // Assert
        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().nombreRepositorio()).isEqualTo("Repo");
        assertThat(encontrado.get().administrador()).isEqualTo(administrador);
    }

    @Test
    void debeRetornarVacio_cuandoAusente() {
        // Arrange
        var adapter = new RepositorioArtefactoCommandOutputAdapter(repositorioArtefactoCommandRepository, logger);

        // Act & Assert
        assertThat(adapter.obtenerPorId(UtilUUID.generarNuevoUUID())).isEmpty();
    }

    @Test
    void debeObtenerBloqueadoPorId_cuandoExiste() {
        // Arrange
        var adapter = new RepositorioArtefactoCommandOutputAdapter(repositorioArtefactoCommandRepository, logger);
        var administrador = sembrarAdministrador();
        var id = UtilUUID.generarNuevoUUID();
        adapter.registrar(new RepositorioArtefactoEntity(id, "Repo", "Descripcion", administrador));
        entityManager.flush();
        entityManager.clear();

        // Act
        var encontrado = adapter.obtenerBloqueadoPorId(id);

        // Assert
        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().nombreRepositorio()).isEqualTo("Repo");
    }

    @Test
    void debeRetornarVacio_cuandoElBloqueadoEstaAusente() {
        // Arrange
        var adapter = new RepositorioArtefactoCommandOutputAdapter(repositorioArtefactoCommandRepository, logger);

        // Act & Assert
        assertThat(adapter.obtenerBloqueadoPorId(UtilUUID.generarNuevoUUID())).isEmpty();
    }

    @Test
    void debeActualizarNombreYDescripcion_sinTocarAdministrador() {
        // Arrange
        var adapter = new RepositorioArtefactoCommandOutputAdapter(repositorioArtefactoCommandRepository, logger);
        var administrador = sembrarAdministrador();
        var id = UtilUUID.generarNuevoUUID();
        var otro = UtilUUID.generarNuevoUUID();
        adapter.registrar(new RepositorioArtefactoEntity(id, "Repo", "Descripcion", administrador));
        adapter.registrar(new RepositorioArtefactoEntity(otro, "Otro", "Descripcion otro", administrador));
        entityManager.flush();

        // Act
        adapter.actualizarDatos(id, "Repo Nuevo", "Descripcion nueva");
        entityManager.clear();

        // Assert
        var actualizado = repositorioArtefactoCommandRepository.findById(id).orElseThrow();
        assertThat(actualizado.getNombreRepositorio()).isEqualTo("Repo Nuevo");
        assertThat(actualizado.getDescripcion()).isEqualTo("Descripcion nueva");
        assertThat(actualizado.getAdministrador()).isEqualTo(administrador);
        var intacto = repositorioArtefactoCommandRepository.findById(otro).orElseThrow();
        assertThat(intacto.getNombreRepositorio()).isEqualTo("Otro");
        assertThat(intacto.getDescripcion()).isEqualTo("Descripcion otro");
        verify(logger).debug(RepositorioArtefactoKey.LOG_ACTUALIZADO, id);
    }

    @Test
    void debeDistinguirNombreDeOtro_cuandoElNombreEsPropioAjenoOLibre() {
        // Arrange
        var adapter = new RepositorioArtefactoCommandOutputAdapter(repositorioArtefactoCommandRepository, logger);
        var administrador = sembrarAdministrador();
        var propio = UtilUUID.generarNuevoUUID();
        var ajeno = UtilUUID.generarNuevoUUID();
        adapter.registrar(new RepositorioArtefactoEntity(propio, "Repo Propio", "Descripcion", administrador));
        adapter.registrar(new RepositorioArtefactoEntity(ajeno, "Repo Ajeno", "Descripcion", administrador));
        entityManager.flush();

        // Act
        var nombrePropio = adapter.existeOtroConNombreRepositorio("Repo Propio", propio);
        var nombreAjeno = adapter.existeOtroConNombreRepositorio("Repo Ajeno", propio);
        var nombreLibre = adapter.existeOtroConNombreRepositorio("Repo Libre", propio);

        // Assert
        assertThat(nombrePropio).isFalse();
        assertThat(nombreAjeno).isTrue();
        assertThat(nombreLibre).isFalse();
    }
}
