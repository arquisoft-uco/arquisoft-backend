package com.arquisoft.usuarios.infrastructure.bibliotecario.query.secondaryadapter.repository.mapper;

import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.usuarios.infrastructure.bibliotecario.query.secondaryadapter.repository.BibliotecarioJpaQueryEntity;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BibliotecarioQueryMapperTest {

    @Test
    void debeMapearTodosLosCampos_cuandoConvierteAReadModel() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var entity = BibliotecarioJpaQueryEntity.builder()
                .id(id)
                .identificador("1001")
                .nombre("Ana Ramirez")
                .email("ana.ramirez@uco.edu.co")
                .contacto("3000000000")
                .estado("INACTIVO")
                .vigente(false)
                .build();

        // Act
        var readModel = BibliotecarioQueryMapper.toReadModel(entity);

        // Assert
        assertThat(readModel.id()).isEqualTo(id);
        assertThat(readModel.identificador()).isEqualTo("1001");
        assertThat(readModel.nombre()).isEqualTo("Ana Ramirez");
        assertThat(readModel.email()).isEqualTo("ana.ramirez@uco.edu.co");
        assertThat(readModel.contacto()).isEqualTo("3000000000");
        assertThat(readModel.estado()).isEqualTo("INACTIVO");
        assertThat(readModel.vigente()).isFalse();
    }
}
