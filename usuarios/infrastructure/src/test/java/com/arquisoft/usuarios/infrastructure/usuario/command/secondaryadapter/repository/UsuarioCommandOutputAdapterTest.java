package com.arquisoft.usuarios.infrastructure.usuario.command.secondaryadapter.repository;

import com.arquisoft.usuarios.application.usuario.command.secondaryport.entity.UsuarioEntity;
import com.arquisoft.usuarios.infrastructure.usuario.command.secondaryadapter.entity.UsuarioJpaEntity;
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
class UsuarioCommandOutputAdapterTest {

    private static final String EMAIL = "ana.gomez@soyuco.edu.co";
    private static final String ROL = "estudiante";

    @Autowired
    private UsuarioCommandRepository repository;

    private UsuarioCommandOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new UsuarioCommandOutputAdapter(repository);
    }

    @Test
    void debePersistirLaFila_cuandoGuardaUnUsuario() {
        // Arrange
        UUID id = UUID.randomUUID();

        // Act
        adapter.guardar(new UsuarioEntity(id, EMAIL, ROL));

        // Assert
        UsuarioJpaEntity persistido = repository.findById(id).orElseThrow();
        assertThat(persistido.getEmail()).isEqualTo(EMAIL);
        assertThat(persistido.getRol()).isEqualTo(ROL);
    }

    @Test
    void debeReportarExistente_cuandoElEmailYaFuePersistido() {
        // Arrange
        adapter.guardar(new UsuarioEntity(UUID.randomUUID(), EMAIL, ROL));

        // Act & Assert
        assertThat(adapter.existePorEmail(EMAIL)).isTrue();
    }

    @Test
    void debeReportarNoExistente_cuandoElEmailEsNuevo() {
        // Act & Assert
        assertThat(adapter.existePorEmail("nadie@soyuco.edu.co")).isFalse();
    }
}
