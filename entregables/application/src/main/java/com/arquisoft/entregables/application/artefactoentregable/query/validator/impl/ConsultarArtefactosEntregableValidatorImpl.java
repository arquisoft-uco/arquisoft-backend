package com.arquisoft.entregables.application.artefactoentregable.query.validator.impl;

import com.arquisoft.entregables.application.artefactoentregable.query.validator.ConsultarArtefactosEntregableValidator;
import com.arquisoft.entregables.domain.entregableproyectogrado.model.ExistenciaEntregableProyectoGrado;
import com.arquisoft.entregables.domain.entregableproyectogrado.model.PropiedadEntregableProyectoGrado;
import com.arquisoft.entregables.domain.entregableproyectogrado.rules.AsesorPropietarioEntregableProyectoGradoRule;
import com.arquisoft.entregables.domain.entregableproyectogrado.rules.EntregableProyectoGradoExisteRule;
import com.arquisoft.entregables.domain.entregableproyectogrado.rules.impl.AsesorPropietarioEntregableProyectoGradoRuleImpl;
import com.arquisoft.entregables.domain.entregableproyectogrado.rules.impl.EntregableProyectoGradoExisteRuleImpl;
import com.arquisoft.shared.util.UtilUUID;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ConsultarArtefactosEntregableValidatorImpl implements ConsultarArtefactosEntregableValidator {

    private final EntregableProyectoGradoExisteRule entregableProyectoGradoExisteRule;
    private final AsesorPropietarioEntregableProyectoGradoRule asesorPropietarioEntregableProyectoGradoRule;

    public ConsultarArtefactosEntregableValidatorImpl() {
        this.entregableProyectoGradoExisteRule = new EntregableProyectoGradoExisteRuleImpl();
        this.asesorPropietarioEntregableProyectoGradoRule = new AsesorPropietarioEntregableProyectoGradoRuleImpl();
    }

    @Override
    public void validar(UUID entregableProyectoGrado, UUID asesorAsignado, UUID asesorSolicitante) {
        entregableProyectoGradoExisteRule.validar(new ExistenciaEntregableProyectoGrado(
                entregableProyectoGrado, !UtilUUID.esPorDefecto(asesorAsignado)));
        asesorPropietarioEntregableProyectoGradoRule.validar(new PropiedadEntregableProyectoGrado(
                entregableProyectoGrado, asesorAsignado, asesorSolicitante));
    }
}
