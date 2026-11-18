package com.arquisoft.entregables.application.estadoentregableproyectogrado.query.validator.impl;

import com.arquisoft.entregables.application.estadoentregableproyectogrado.query.validator.ConsultarEstadosEntregableProyectoGradoValidator;
import com.arquisoft.entregables.domain.asesorproyectogrado.model.VigenciaAsesorProyectoGrado;
import com.arquisoft.entregables.domain.asesorproyectogrado.rules.AsesorAsignadoProyectoGradoRule;
import com.arquisoft.entregables.domain.asesorproyectogrado.rules.impl.AsesorAsignadoProyectoGradoRuleImpl;
import com.arquisoft.entregables.domain.entregableproyectogrado.model.ExistenciaEntregableProyectoGrado;
import com.arquisoft.entregables.domain.entregableproyectogrado.rules.EntregableProyectoGradoExisteRule;
import com.arquisoft.entregables.domain.entregableproyectogrado.rules.impl.EntregableProyectoGradoExisteRuleImpl;
import com.arquisoft.entregables.domain.entregableproyectogrado.EntregableProyectoGradoDomain;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ConsultarEstadosEntregableProyectoGradoValidatorImpl
        implements ConsultarEstadosEntregableProyectoGradoValidator {

    private final EntregableProyectoGradoExisteRule entregableProyectoGradoExisteRule;
    private final AsesorAsignadoProyectoGradoRule asesorAsignadoProyectoGradoRule;

    public ConsultarEstadosEntregableProyectoGradoValidatorImpl() {
        this.entregableProyectoGradoExisteRule = new EntregableProyectoGradoExisteRuleImpl();
        this.asesorAsignadoProyectoGradoRule = new AsesorAsignadoProyectoGradoRuleImpl();
    }

    @Override
    public void validar(UUID entregableProyectoGrado, EntregableProyectoGradoDomain entregable,
                        UUID asesorVigente, UUID asesorSolicitante) {
        entregableProyectoGradoExisteRule.validar(new ExistenciaEntregableProyectoGrado(
                entregableProyectoGrado, !entregable.esVacio()));
        asesorAsignadoProyectoGradoRule.validar(new VigenciaAsesorProyectoGrado(
                entregable.getProyectoGrado(), asesorSolicitante, asesorVigente));
    }
}
