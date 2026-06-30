package com.arquisoft.fichas.infrastructure.estudiantefichaperfil.query.adapter.out.persistence;

import com.arquisoft.fichas.infrastructure.estudiantefichaperfil.persistence.EstudianteFichaPerfilJpaEntity;
import com.arquisoft.fichas.infrastructure.estudiantefichaperfil.persistence.EstudianteFichaPerfilJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=false"
})
class EstudianteFichaPerfilQueryOutputAdapterTest {

    @Autowired
    private EstudianteFichaPerfilJpaRepository jpaRepository;

    private EstudianteFichaPerfilQueryOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new EstudianteFichaPerfilQueryOutputAdapter(jpaRepository);
    }

    @Test
    void debeRetornarTrue_cuandoPropietario() {
        // Arrange
        UUID estudianteId = UUID.randomUUID();
        UUID fichaPerfilId = UUID.randomUUID();

        EstudianteFichaPerfilJpaEntity entity = EstudianteFichaPerfilJpaEntity.builder()
                .id(UUID.randomUUID())
                .estudianteId(estudianteId)
                .fichaPerfilId(fichaPerfilId)
                .build();
        jpaRepository.save(entity);

        // Act
        boolean existe = adapter.existePorEstudianteYFicha(estudianteId, fichaPerfilId);

        // Assert
        assertThat(existe).isTrue();
    }

    @Test
    void debeRetornarFalse_cuandoNoPropietario() {
        // Arrange
        UUID estudianteId = UUID.randomUUID();
        UUID fichaPerfilId = UUID.randomUUID();

        // No se inserta ninguna relación

        // Act
        boolean existe = adapter.existePorEstudianteYFicha(estudianteId, fichaPerfilId);

        // Assert
        assertThat(existe).isFalse();
    }
}
