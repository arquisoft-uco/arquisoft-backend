package com.arquisoft.evaluaciones.infrastructure.categoriaitemcuantitativoasesor.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.infrastructure.categoriaitemcuantitativoasesor.command.secondaryadapter.entity.CategoriaItemCuantitativoAsesorJpaEntity;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(CategoriaItemCuantitativoAsesorCommandOutputAdapter.class)
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=false"
})
class CategoriaItemCuantitativoAsesorCommandOutputAdapterTest {

    @Autowired
    private CategoriaItemCuantitativoAsesorCommandOutputAdapter adapter;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void debeDevolverExistencia_cuandoConsultaPorId() {
        // Arrange
        var existente = UtilUUID.generarNuevoUUID();
        entityManager.persist(CategoriaItemCuantitativoAsesorJpaEntity.builder()
                .id(existente).nombre("Seguimiento").descripcion("Descripcion").build());
        entityManager.flush();

        // Act
        var existe = adapter.existePorId(existente);
        var noExiste = adapter.existePorId(UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(existe).isTrue();
        assertThat(noExiste).isFalse();
    }
}
