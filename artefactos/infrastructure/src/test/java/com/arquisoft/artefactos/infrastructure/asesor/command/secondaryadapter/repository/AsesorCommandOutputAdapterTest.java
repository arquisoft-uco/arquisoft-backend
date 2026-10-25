package com.arquisoft.artefactos.infrastructure.asesor.command.secondaryadapter.repository;

import com.arquisoft.artefactos.infrastructure.asesor.command.secondaryadapter.entity.AsesorJpaEntity;
import com.arquisoft.artefactos.infrastructure.revisionasesor.command.secondaryadapter.entity.RevisionAsesorJpaEntity;
import com.arquisoft.artefactos.infrastructure.revisionasesor.support.ArtefactoTestEntity;
import com.arquisoft.artefactos.infrastructure.revisionasesor.support.VersionArtefactoTestEntity;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class AsesorCommandOutputAdapterTest {

    @Autowired
    private AsesorCommandRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    private AsesorCommandOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new AsesorCommandOutputAdapter(repository);
    }

    @Test
    void debeObtenerAsesorDelArtefactoDeLaRevision_cuandoExiste() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        var otroAsesor = UtilUUID.generarNuevoUUID();
        var artefacto = UtilUUID.generarNuevoUUID();
        var version = UtilUUID.generarNuevoUUID();
        var revision = UtilUUID.generarNuevoUUID();
        entityManager.persist(AsesorJpaEntity.builder().id(asesor).nombre("Ana").email("ana@uco.edu.co").build());
        entityManager.persist(AsesorJpaEntity.builder().id(otroAsesor).nombre("Otro").email("otro@uco.edu.co").build());
        entityManager.persist(new ArtefactoTestEntity(artefacto, asesor));
        entityManager.persist(new VersionArtefactoTestEntity(version, artefacto, 1));
        entityManager.persist(RevisionAsesorJpaEntity.builder()
                .id(revision).versionArtefacto(version).estado("PENDIENTE").build());
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.obtenerPorRevisionAsesor(revision);

        // Assert
        assertThat(resultado).hasValueSatisfying(entity -> {
            assertThat(entity.id()).isEqualTo(asesor);
            assertThat(entity.nombre()).isEqualTo("Ana");
            assertThat(entity.email()).isEqualTo("ana@uco.edu.co");
        });
    }

    @Test
    void debeRetornarVacio_cuandoLaRevisionNoExiste() {
        // Act
        var resultado = adapter.obtenerPorRevisionAsesor(UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(resultado).isEmpty();
    }
}
