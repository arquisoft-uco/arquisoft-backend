package com.arquisoft.usuarios.infrastructure.usuario.query.secondaryadapter.repository;

import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.usuarios.infrastructure.usuario.command.secondaryadapter.entity.UsuarioJpaEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UsuarioAccesoQueryOutputAdapterTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UsuarioAccesoQueryRepository usuarioAccesoQueryRepository;

    private UsuarioAccesoQueryOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new UsuarioAccesoQueryOutputAdapter(usuarioAccesoQueryRepository);
    }

    @Test
    void debeRetornarUsuario_cuandoExiste() {
        // Arrange
        var usuario = persistirUsuario("1001", "Ana Ramirez", "ACTIVO", null);
        entityManager.flush();

        // Act
        var resultado = adapter.obtenerPorId(usuario);

        // Assert
        assertThat(resultado).hasValueSatisfying(entity -> {
            assertThat(entity.id()).isEqualTo(usuario);
            assertThat(entity.identificador()).isEqualTo("1001");
            assertThat(entity.nombre()).isEqualTo("Ana Ramirez");
            assertThat(entity.email()).isEqualTo("1001@uco.edu.co");
            assertThat(entity.contacto()).isEqualTo("3000000000");
            assertThat(entity.estado()).isEqualTo("ACTIVO");
            assertThat(entity.eliminadoEn()).isNull();
        });
    }

    @Test
    void debeConservarEliminadoEn_cuandoElUsuarioEstaEliminado() {
        // Arrange
        var baja = Instant.parse("2026-09-01T10:00:00Z");
        var usuario = persistirUsuario("1002", "Bruno Diaz", "INACTIVO", baja);
        entityManager.flush();

        // Act
        var resultado = adapter.obtenerPorId(usuario);

        // Assert
        assertThat(resultado).hasValueSatisfying(entity -> {
            assertThat(entity.estado()).isEqualTo("INACTIVO");
            assertThat(entity.eliminadoEn()).isEqualTo(baja);
        });
    }

    @Test
    void debeRetornarVacio_cuandoElUsuarioNoExiste() {
        // Arrange
        persistirUsuario("1003", "Carla Vidal", "ACTIVO", null);
        entityManager.flush();

        // Act
        var resultado = adapter.obtenerPorId(UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(resultado).isEmpty();
    }

    private UUID persistirUsuario(String identificador, String nombre, String estado, Instant eliminadoEn) {
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persist(UsuarioJpaEntity.builder()
                .id(id)
                .identificador(identificador)
                .nombre(nombre)
                .email(identificador + "@uco.edu.co")
                .contacto("3000000000")
                .estadoId(estado)
                .eliminadoEn(eliminadoEn)
                .build());
        return id;
    }
}
