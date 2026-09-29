package com.arquisoft.usuarios.infrastructure.administrador.query.primaryadapter.web.mapper;

import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.usuarios.application.administrador.query.readmodel.AdministradorReadModel;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AdministradorResponseMapperTest {

    @Test
    void debeMapearTodosLosCampos_cuandoConvierteAResponseDTO() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var readModel = new AdministradorReadModel(id, "1001", "Ana Ramirez",
                "ana.ramirez@uco.edu.co", "3000000000", "ACTIVO", true);

        // Act
        var dto = AdministradorResponseMapper.toResponse(readModel);

        // Assert
        assertThat(dto.id()).isEqualTo(id);
        assertThat(dto.identificador()).isEqualTo("1001");
        assertThat(dto.nombre()).isEqualTo("Ana Ramirez");
        assertThat(dto.email()).isEqualTo("ana.ramirez@uco.edu.co");
        assertThat(dto.contacto()).isEqualTo("3000000000");
        assertThat(dto.estado()).isEqualTo("ACTIVO");
        assertThat(dto.vigente()).isTrue();
    }
}
