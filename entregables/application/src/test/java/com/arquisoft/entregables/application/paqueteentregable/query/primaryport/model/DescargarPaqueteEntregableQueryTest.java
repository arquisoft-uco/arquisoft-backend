package com.arquisoft.entregables.application.paqueteentregable.query.primaryport.model;

import com.arquisoft.shared.message.constant.EntregablesFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DescargarPaqueteEntregableQueryTest {

    @Test
    void debeCrear_cuandoDatosValidos() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        var actor = UtilUUID.generarNuevoUUID();

        // Act
        var query = DescargarPaqueteEntregableQuery.crear(
                entregable, actor, List.of(RolConsultaPaqueteEntregable.ASESOR));

        // Assert
        assertThat(query.entregableProyectoGrado()).isEqualTo(entregable);
        assertThat(query.actor()).isEqualTo(actor);
        assertThat(query.roles()).containsExactly(RolConsultaPaqueteEntregable.ASESOR);
    }

    @Test
    void debeLanzarValidacionDeEntrada_cuandoRolesVacios() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        var actor = UtilUUID.generarNuevoUUID();
        var roles = List.<RolConsultaPaqueteEntregable>of();

        // Act & Assert
        assertThatThrownBy(() -> DescargarPaqueteEntregableQuery.crear(entregable, actor, roles))
                .isInstanceOfSatisfying(ApplicationValidationException.class, ex ->
                        assertThat(ex.getValidationResult()
                                .tieneErroresDeCampo(EntregablesFields.PaqueteEntregable.ROLES)).isTrue());
    }
}
