package com.arquisoft.artefactos.infrastructure.estudianteartefacto.command.secondaryadapter.repository;

import com.arquisoft.artefactos.infrastructure.estudianteartefacto.command.secondaryadapter.entity.EstudianteArtefactoJpaEntity;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class EstudianteArtefactoCommandOutputAdapterTest {

    @Autowired
    private EstudianteArtefactoCommandRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void debeRetornarVerdadero_cuandoElEstudianteEstaVinculadoAlArtefacto() {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();
        var estudiante = UtilUUID.generarNuevoUUID();
        entityManager.persist(EstudianteArtefactoJpaEntity.builder()
                .id(UtilUUID.generarNuevoUUID()).artefacto(artefacto).estudiante(estudiante).build());
        entityManager.flush();
        var adapter = new EstudianteArtefactoCommandOutputAdapter(repository);

        // Act & Assert
        assertThat(adapter.existePorArtefactoYEstudiante(artefacto, estudiante)).isTrue();
    }

    @Test
    void debeRetornarFalso_cuandoElVinculoEsDeOtroEstudianteOArtefacto() {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();
        var estudiante = UtilUUID.generarNuevoUUID();
        entityManager.persist(EstudianteArtefactoJpaEntity.builder()
                .id(UtilUUID.generarNuevoUUID()).artefacto(artefacto).estudiante(estudiante).build());
        entityManager.flush();
        var adapter = new EstudianteArtefactoCommandOutputAdapter(repository);

        // Act & Assert
        assertThat(adapter.existePorArtefactoYEstudiante(artefacto, UtilUUID.generarNuevoUUID())).isFalse();
        assertThat(adapter.existePorArtefactoYEstudiante(UtilUUID.generarNuevoUUID(), estudiante)).isFalse();
    }
}
