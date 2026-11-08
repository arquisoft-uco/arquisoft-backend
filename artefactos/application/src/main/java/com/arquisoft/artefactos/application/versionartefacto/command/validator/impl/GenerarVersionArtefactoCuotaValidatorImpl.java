package com.arquisoft.artefactos.application.versionartefacto.command.validator.impl;

import com.arquisoft.artefactos.application.versionartefacto.command.validator.GenerarVersionArtefactoCuotaValidator;
import com.arquisoft.artefactos.domain.artefacto.ArtefactoDomain;
import com.arquisoft.artefactos.domain.proyectogrado.model.ExistenciaProyectoGrado;
import com.arquisoft.artefactos.domain.proyectogrado.rules.ProyectoGradoExisteRule;
import com.arquisoft.artefactos.domain.proyectogrado.rules.impl.ProyectoGradoExisteRuleImpl;
import com.arquisoft.artefactos.domain.versionartefacto.CargaPendienteDomain;
import com.arquisoft.artefactos.domain.versionartefacto.model.AlmacenamientoProyectoGrado;
import com.arquisoft.artefactos.domain.versionartefacto.model.CuotaProyectoGrado;
import com.arquisoft.artefactos.domain.versionartefacto.rules.CuotaProyectoGradoNoExcedidaRule;
import com.arquisoft.artefactos.domain.versionartefacto.rules.impl.CuotaProyectoGradoNoExcedidaRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class GenerarVersionArtefactoCuotaValidatorImpl implements GenerarVersionArtefactoCuotaValidator {

    private final ProyectoGradoExisteRule proyectoGradoExisteRule;
    private final CuotaProyectoGradoNoExcedidaRule cuotaProyectoGradoNoExcedidaRule;

    public GenerarVersionArtefactoCuotaValidatorImpl() {
        this.proyectoGradoExisteRule = new ProyectoGradoExisteRuleImpl();
        this.cuotaProyectoGradoNoExcedidaRule = new CuotaProyectoGradoNoExcedidaRuleImpl();
    }

    @Override
    public void validar(ArtefactoDomain artefacto, CargaPendienteDomain carga, AlmacenamientoProyectoGrado almacenamiento) {
        var proyecto = almacenamiento.proyectoGrado();
        proyectoGradoExisteRule.validar(new ExistenciaProyectoGrado(artefacto.getProyectoGrado(), !proyecto.esVacio()));
        cuotaProyectoGradoNoExcedidaRule.validar(new CuotaProyectoGrado(
                artefacto.getProyectoGrado(), proyecto.getNombre(), carga.getNombreArchivo(),
                almacenamiento.cuotaBytes(), almacenamiento.usoBytes(), carga.getTamanoBytes()));
    }
}
