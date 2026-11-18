package com.arquisoft.usuarios.infrastructure.usuario.query.secondaryadapter.repository.mapper;

import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.usuarios.application.usuario.query.readmodel.RolPerfilReadModel;
import com.arquisoft.usuarios.infrastructure.usuario.query.secondaryadapter.repository.PerfilUsuarioJpaQueryEntity;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

class PerfilUsuarioQueryMapperTest {

    @Test
    void debeArmarLosOchoRolesEnOrdenFijoConEtiqueta_cuandoTodosEstanVigentes() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var entity = PerfilUsuarioJpaQueryEntity.builder()
                .id(id)
                .identificador("1001")
                .nombre("Ana Ramirez")
                .email("ana@uco.edu.co")
                .contacto("573001112233")
                .estado("ACTIVO")
                .estadoNombre("Activo")
                .esEstudiante(true)
                .esAsesor(true)
                .esAsesorFicha(true)
                .esCoordinador(true)
                .esRepresentanteComite(true)
                .esAdministrador(true)
                .esBibliotecario(true)
                .esJurado(true)
                .build();

        // Act
        var readModel = PerfilUsuarioQueryMapper.toReadModel(entity);

        // Assert
        assertThat(readModel.id()).isEqualTo(id);
        assertThat(readModel.identificador()).isEqualTo("1001");
        assertThat(readModel.nombre()).isEqualTo("Ana Ramirez");
        assertThat(readModel.email()).isEqualTo("ana@uco.edu.co");
        assertThat(readModel.contacto()).isEqualTo("573001112233");
        assertThat(readModel.estado().id()).isEqualTo("ACTIVO");
        assertThat(readModel.estado().nombre()).isEqualTo("Activo");
        assertThat(readModel.roles())
                .extracting(RolPerfilReadModel::id, RolPerfilReadModel::nombre)
                .containsExactly(
                        tuple("estudiante", "Estudiante"),
                        tuple("asesor", "Asesor"),
                        tuple("asesor-ficha", "Asesor de ficha"),
                        tuple("coordinador", "Coordinador"),
                        tuple("representante-comite", "Representante del comité"),
                        tuple("administrador", "Administrador"),
                        tuple("bibliotecario", "Bibliotecario"),
                        tuple("jurado", "Jurado"));
    }

    @Test
    void debeIncluirSoloLosRolesVigentes_cuandoTieneAlgunos() {
        // Arrange
        var entity = PerfilUsuarioJpaQueryEntity.builder()
                .id(UtilUUID.generarNuevoUUID())
                .estado("INACTIVO")
                .estadoNombre("Inactivo")
                .esJurado(true)
                .esAsesor(true)
                .build();

        // Act
        var readModel = PerfilUsuarioQueryMapper.toReadModel(entity);

        // Assert
        assertThat(readModel.estado().id()).isEqualTo("INACTIVO");
        assertThat(readModel.estado().nombre()).isEqualTo("Inactivo");
        assertThat(readModel.roles()).extracting(RolPerfilReadModel::id).containsExactly("asesor", "jurado");
    }

    @Test
    void debeDevolverListaVaciaInmutable_cuandoNoTieneRoles() {
        // Arrange
        var entity = PerfilUsuarioJpaQueryEntity.builder()
                .id(UtilUUID.generarNuevoUUID())
                .estado("ACTIVO")
                .estadoNombre("Activo")
                .build();

        // Act
        var readModel = PerfilUsuarioQueryMapper.toReadModel(entity);

        // Assert
        assertThat(readModel.roles()).isEmpty();
        assertThat(readModel.roles()).isUnmodifiable();
    }
}
