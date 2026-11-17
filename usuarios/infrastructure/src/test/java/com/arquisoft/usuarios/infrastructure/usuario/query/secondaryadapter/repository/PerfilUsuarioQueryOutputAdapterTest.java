package com.arquisoft.usuarios.infrastructure.usuario.query.secondaryadapter.repository;

import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.usuarios.application.usuario.query.criteria.PerfilUsuarioCriteria;
import com.arquisoft.usuarios.application.usuario.query.readmodel.RolPerfilReadModel;
import com.arquisoft.usuarios.infrastructure.asesor.command.secondaryadapter.entity.AsesorJpaEntity;
import com.arquisoft.usuarios.infrastructure.estudiante.command.secondaryadapter.entity.EstudianteJpaEntity;
import com.arquisoft.usuarios.infrastructure.jurado.command.secondaryadapter.entity.JuradoJpaEntity;
import com.arquisoft.usuarios.infrastructure.usuario.command.secondaryadapter.entity.UsuarioJpaEntity;
import com.arquisoft.usuarios.infrastructure.usuario.exception.PerfilUsuarioNoDisponibleException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.jdbc.Sql;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

@DataJpaTest
@Sql(scripts = "/sql/estadousuario/esquema_estado_usuario.sql")
class PerfilUsuarioQueryOutputAdapterTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PerfilUsuarioQueryRepository perfilUsuarioQueryRepository;

    private PerfilUsuarioQueryOutputAdapter adapter;

    private UUID conRoles;
    private UUID inactivoSinRoles;
    private UUID otro;

    @BeforeEach
    void setUp() {
        adapter = new PerfilUsuarioQueryOutputAdapter(perfilUsuarioQueryRepository);
        var baja = Instant.parse("2026-09-01T10:00:00Z");
        persistirEstado("ACTIVO", "Activo");
        persistirEstado("INACTIVO", "Inactivo");

        conRoles = persistirUsuario("1001", "Ana Ramirez", "ACTIVO");
        entityManager.persist(EstudianteJpaEntity.builder().usuarioId(conRoles).build());
        entityManager.persist(AsesorJpaEntity.builder().usuarioId(conRoles).build());
        entityManager.persist(JuradoJpaEntity.builder().usuarioId(conRoles).eliminadoEn(baja).build());

        inactivoSinRoles = persistirUsuario("1002", "Bruno Diaz", "INACTIVO");

        otro = persistirUsuario("1003", "Carla Vidal", "ACTIVO");
        entityManager.persist(JuradoJpaEntity.builder().usuarioId(otro).build());

        entityManager.flush();
    }

    @Test
    void debeDevolverDatosEstadoYSoloLosRolesVigentes_cuandoTieneRolesYUnoDadoDeBaja() {
        // Act
        var perfil = adapter.consultar(new PerfilUsuarioCriteria(conRoles));

        // Assert
        assertThat(perfil.id()).isEqualTo(conRoles);
        assertThat(perfil.identificador()).isEqualTo("1001");
        assertThat(perfil.nombre()).isEqualTo("Ana Ramirez");
        assertThat(perfil.email()).isEqualTo("1001@uco.edu.co");
        assertThat(perfil.contacto()).isEqualTo("3000000000");
        assertThat(perfil.estado().id()).isEqualTo("ACTIVO");
        assertThat(perfil.estado().nombre()).isEqualTo("Activo");
        assertThat(perfil.roles())
                .extracting(RolPerfilReadModel::id, RolPerfilReadModel::nombre)
                .containsExactly(tuple("estudiante", "Estudiante"), tuple("asesor", "Asesor"));
    }

    @Test
    void debeDevolverEstadoInactivoYListaVacia_cuandoElUsuarioEstaInactivoYSinRoles() {
        // Act
        var perfil = adapter.consultar(new PerfilUsuarioCriteria(inactivoSinRoles));

        // Assert
        assertThat(perfil.estado().id()).isEqualTo("INACTIVO");
        assertThat(perfil.estado().nombre()).isEqualTo("Inactivo");
        assertThat(perfil.roles()).isEmpty();
    }

    @Test
    void debeDevolverSoloElPerfilPedido_cuandoExistenOtrosUsuarios() {
        // Act
        var perfil = adapter.consultar(new PerfilUsuarioCriteria(otro));

        // Assert
        assertThat(perfil.id()).isEqualTo(otro);
        assertThat(perfil.roles()).extracting(RolPerfilReadModel::id).containsExactly("jurado");
    }

    @Test
    void debeLanzarNoDisponible_cuandoNoExisteLaFila() {
        // Arrange
        var inexistente = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> adapter.consultar(new PerfilUsuarioCriteria(inexistente)))
                .isInstanceOfSatisfying(PerfilUsuarioNoDisponibleException.class, ex ->
                        assertThat(ex.getCodigoError()).isEqualTo(UsuariosCodes.Usuario.PERFIL_NO_DISPONIBLE));
    }

    private void persistirEstado(String id, String nombre) {
        entityManager.getEntityManager()
                .createNativeQuery("INSERT INTO estado_usuario (id, nombre, descripcion) VALUES (?1, ?2, ?3)")
                .setParameter(1, id)
                .setParameter(2, nombre)
                .setParameter(3, "descripcion " + nombre)
                .executeUpdate();
    }

    private UUID persistirUsuario(String identificador, String nombre, String estado) {
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persist(UsuarioJpaEntity.builder()
                .id(id)
                .identificador(identificador)
                .nombre(nombre)
                .email(identificador + "@uco.edu.co")
                .contacto("3000000000")
                .estadoId(estado)
                .build());
        return id;
    }
}
