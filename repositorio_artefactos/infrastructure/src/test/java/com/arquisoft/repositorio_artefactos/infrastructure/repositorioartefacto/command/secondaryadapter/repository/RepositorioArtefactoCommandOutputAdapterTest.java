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
    void debeDistinguirLaCombinacion_cuandoCambiaAdministradorODescripcion() {
        // Arrange
        var adapter = new RepositorioArtefactoCommandOutputAdapter(repositorioArtefactoCommandRepository, logger);
        var administrador = sembrarAdministrador();
        var otroAdministrador = sembrarAdministrador();
        adapter.registrar(new RepositorioArtefactoEntity(
                UtilUUID.generarNuevoUUID(), "Repo", "Descripcion", administrador));
        entityManager.flush();

        // Act
        var mismaCombinacion = adapter.existePorNombreDescripcionYAdministrador("Repo", "Descripcion", administrador);
        var otroAdmin = adapter.existePorNombreDescripcionYAdministrador("Repo", "Descripcion", otroAdministrador);
        var otraDescripcion = adapter.existePorNombreDescripcionYAdministrador("Repo", "Otra", administrador);

        // Assert
        assertThat(mismaCombinacion).isTrue();
        assertThat(otroAdmin).isFalse();
        assertThat(otraDescripcion).isFalse();
    }
}
