package com.arquisoft.artefactos.infrastructure.proyectogrado.command.secondaryadapter.repository;

import com.arquisoft.artefactos.application.proyectogrado.command.secondaryport.entity.ProyectoGradoEntity;
import com.arquisoft.artefactos.infrastructure.artefacto.command.secondaryadapter.entity.ArtefactoJpaEntity;
import com.arquisoft.artefactos.infrastructure.proyectogrado.command.secondaryadapter.entity.ProyectoGradoJpaEntity;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.ProyectoGradoKey;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@DataJpaTest
class ProyectoGradoCommandOutputAdapterTest {

    @Autowired
    private ProyectoGradoCommandRepository proyectoGradoCommandRepository;

    @Autowired
    private TestEntityManager entityManager;

    private final AppLogger logger = mock(AppLogger.class);

    private ProyectoGradoCommandOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new ProyectoGradoCommandOutputAdapter(proyectoGradoCommandRepository, logger);
    }

    @Test
    void debeGuardarYReportarExistencia_cuandoSePersisteElProyecto() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();

        // Act
        adapter.guardar(new ProyectoGradoEntity(id, "Sistema de gestion"));
        entityManager.flush();
        entityManager.clear();

        // Assert
        assertThat(entityManager.find(ProyectoGradoJpaEntity.class, id).getNombre()).isEqualTo("Sistema de gestion");
        assertThat(adapter.existePorId(id)).isTrue();
        assertThat(adapter.existePorId(UtilUUID.generarNuevoUUID())).isFalse();
        verify(logger).debug(ProyectoGradoKey.LOG_GUARDADO, id);
    }

    @Test
    void debeObtenerPorArtefactoYRetornarVacio_cuandoElArtefactoNoExiste() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var otroProyecto = UtilUUID.generarNuevoUUID();
        entityManager.persist(ProyectoGradoJpaEntity.builder().id(proyecto).nombre("Sistema de gestion").build());
        entityManager.persist(ProyectoGradoJpaEntity.builder().id(otroProyecto).nombre("Otro").build());
        var artefacto = UtilUUID.generarNuevoUUID();
        entityManager.persist(ArtefactoJpaEntity.builder()
                .id(artefacto)
                .proyectoGrado(proyecto)
                .versionRepositorioArtefacto(UtilUUID.generarNuevoUUID())
                .asesor(UtilUUID.generarNuevoUUID())
                .build());
        entityManager.flush();
        entityManager.clear();

        // Act
        var encontrado = adapter.obtenerPorArtefacto(artefacto);
        var ausente = adapter.obtenerPorArtefacto(UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(encontrado).hasValueSatisfying(entity -> {
            assertThat(entity.id()).isEqualTo(proyecto);
            assertThat(entity.nombre()).isEqualTo("Sistema de gestion");
        });
        assertThat(ausente).isEmpty();
    }

    @Test
    void debeObtenerBloqueadoPorArtefacto() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        entityManager.persist(ProyectoGradoJpaEntity.builder().id(proyecto).nombre("Sistema de gestion").build());
        var artefacto = UtilUUID.generarNuevoUUID();
        entityManager.persist(ArtefactoJpaEntity.builder()
                .id(artefacto)
                .proyectoGrado(proyecto)
                .versionRepositorioArtefacto(UtilUUID.generarNuevoUUID())
                .asesor(UtilUUID.generarNuevoUUID())
                .build());
        entityManager.flush();
        entityManager.clear();

        // Act
        var encontrado = adapter.obtenerBloqueadoPorArtefacto(artefacto);
        var ausente = adapter.obtenerBloqueadoPorArtefacto(UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(encontrado).hasValueSatisfying(entity -> assertThat(entity.id()).isEqualTo(proyecto));
        assertThat(ausente).isEmpty();
    }

    @Test
    void debeObtenerPorId_cuandoExiste() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persist(ProyectoGradoJpaEntity.builder().id(id).nombre("Sistema de gestion").build());
        entityManager.flush();
        entityManager.clear();

        // Act
        var encontrado = adapter.obtenerPorId(id);
        var ausente = adapter.obtenerPorId(UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(encontrado).hasValueSatisfying(entity -> {
            assertThat(entity.id()).isEqualTo(id);
            assertThat(entity.nombre()).isEqualTo("Sistema de gestion");
        });
        assertThat(ausente).isEmpty();
    }
}
