package com.arquisoft.usuarios.infrastructure.administrador.query.secondaryadapter.repository.mapper;

import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.usuarios.infrastructure.administrador.query.secondaryadapter.repository.AdministradorJpaQueryEntity;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AdministradorQueryMapperTest {

    @Test
    void debeMapearTodosLosCampos_cuandoConvierteAReadModel() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var entity = AdministradorJpaQueryEntity.builder()
                .id(id)
                .identificador("1001")
                .nombre("Ana Ramirez")
                .email("ana.ramirez@uco.edu.co")
                .contacto("3000000000")
                .estado("ACTIVO")
                .vigente(true)
                .build();

        // Act
        var readModel = AdministradorQueryMapper.toReadModel(entity);

        // Assert
        assertThat(readModel.id()).isEqualTo(id);
        assertThat(readModel.identificador()).isEqualTo("1001");
        assertThat(readModel.nombre()).isEqualTo("Ana Ramirez");
        assertThat(readModel.email()).isEqualTo("ana.ramirez@uco.edu.co");
        assertThat(readModel.contacto()).isEqualTo("3000000000");
        assertThat(readModel.estado()).isEqualTo("ACTIVO");
        assertThat(readModel.vigente()).isTrue();
    }
}
