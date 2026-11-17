package com.arquisoft.usuarios.infrastructure.usuario.query.primaryadapter.web.mapper;

import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.usuarios.application.usuario.query.readmodel.EstadoPerfilReadModel;
import com.arquisoft.usuarios.application.usuario.query.readmodel.PerfilUsuarioReadModel;
import com.arquisoft.usuarios.application.usuario.query.readmodel.RolPerfilReadModel;
import com.arquisoft.usuarios.infrastructure.usuario.query.primaryadapter.web.dto.PerfilUsuarioResponseDTO.ElementoPerfilDTO;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PerfilUsuarioResponseMapperTest {

    @Test
    void debeMapearTodosLosCampos_cuandoElReadModelEstaCompleto() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var readModel = new PerfilUsuarioReadModel(id, "1001", "Ana Ramirez", "ana@uco.edu.co", "573001112233",
                new EstadoPerfilReadModel("ACTIVO", "Activo"),
                List.of(new RolPerfilReadModel("estudiante", "Estudiante"),
                        new RolPerfilReadModel("asesor", "Asesor")));

        // Act
        var dto = PerfilUsuarioResponseMapper.toResponse(readModel);

        // Assert
        assertThat(dto.id()).isEqualTo(id);
        assertThat(dto.identificador()).isEqualTo("1001");
        assertThat(dto.nombre()).isEqualTo("Ana Ramirez");
        assertThat(dto.email()).isEqualTo("ana@uco.edu.co");
        assertThat(dto.contacto()).isEqualTo("573001112233");
        assertThat(dto.estado()).isEqualTo(new ElementoPerfilDTO("ACTIVO", "Activo"));
        assertThat(dto.roles()).containsExactly(
                new ElementoPerfilDTO("estudiante", "Estudiante"), new ElementoPerfilDTO("asesor", "Asesor"));
    }
}
