package com.arquisoft.usuarios.infrastructure.usuario.query.secondaryadapter.repository.mapper;

import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.usuarios.infrastructure.usuario.query.secondaryadapter.repository.UsuarioJpaQueryEntity;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UsuarioQueryMapperTest {

    @Test
    void debePropagarEsAdministrador_cuandoConvierteAReadModel() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var entity = UsuarioJpaQueryEntity.builder()
                .id(id)
                .identificador("1007")
                .nombre("Gina Paz")
                .email("gina.paz@uco.edu.co")
                .contacto("3000000000")
                .estado("ACTIVO")
                .vigente(true)
                .esAsesor(true)
                .esAdministrador(true)
                .build();

        // Act
        var readModel = UsuarioQueryMapper.toReadModel(entity);

        // Assert
        assertThat(readModel.id()).isEqualTo(id);
        assertThat(readModel.identificador()).isEqualTo("1007");
        assertThat(readModel.vigente()).isTrue();
        assertThat(readModel.esAsesor()).isTrue();
        assertThat(readModel.esEstudiante()).isFalse();
        assertThat(readModel.esAdministrador()).isTrue();
    }

    @Test
    void debeMapearEsAdministradorFalse_cuandoLaEntidadNoEsAdministrador() {
        // Arrange
        var entity = UsuarioJpaQueryEntity.builder()
                .id(UtilUUID.generarNuevoUUID())
                .estado("ACTIVO")
                .vigente(true)
                .esAdministrador(false)
                .build();

        // Act
        var readModel = UsuarioQueryMapper.toReadModel(entity);

        // Assert
        assertThat(readModel.esAdministrador()).isFalse();
    }

    @Test
    void debePropagarEsBibliotecario_cuandoConvierteAReadModel() {
        // Arrange
        var entity = UsuarioJpaQueryEntity.builder()
                .id(UtilUUID.generarNuevoUUID())
                .estado("ACTIVO")
                .vigente(true)
                .esAdministrador(false)
                .esBibliotecario(true)
                .build();

        // Act
        var readModel = UsuarioQueryMapper.toReadModel(entity);

        // Assert
        assertThat(readModel.esBibliotecario()).isTrue();
        assertThat(readModel.esAdministrador()).isFalse();
    }
}
