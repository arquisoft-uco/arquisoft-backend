package com.arquisoft.usuarios.infrastructure.usuario.query.primaryadapter.web.mapper;

import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.usuarios.application.usuario.query.readmodel.UsuarioReadModel;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UsuarioResponseMapperTest {

    @Test
    void debePropagarEsAdministrador_cuandoConvierteAResponseDTO() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var readModel = new UsuarioReadModel(id, "1007", "Gina Paz", "gina.paz@uco.edu.co",
                "3000000000", "ACTIVO", true, false, true, false, false, false, true);

        // Act
        var dto = UsuarioResponseMapper.toResponse(readModel);

        // Assert
        assertThat(dto.id()).isEqualTo(id);
        assertThat(dto.identificador()).isEqualTo("1007");
        assertThat(dto.vigente()).isTrue();
        assertThat(dto.esAsesor()).isTrue();
        assertThat(dto.esRepresentanteComite()).isFalse();
        assertThat(dto.esAdministrador()).isTrue();
    }
}
