package com.arquisoft.entregables.application.artefacto.command.validator.impl;

import com.arquisoft.entregables.application.artefacto.command.validator.RegistrarVersionCerradaArtefactoValidator;
import com.arquisoft.entregables.domain.artefacto.RegistroVersionCerradaArtefactoDomain;
import com.arquisoft.entregables.domain.artefacto.model.ExistenciaArchivoOrigenArtefacto;
import com.arquisoft.entregables.domain.artefacto.rules.ArchivoOrigenArtefactoExisteRule;
import com.arquisoft.entregables.domain.artefacto.rules.impl.ArchivoOrigenArtefactoExisteRuleImpl;
import com.arquisoft.entregables.domain.proyectogrado.ProyectoGradoDomain;
import com.arquisoft.entregables.domain.proyectogrado.model.ExistenciaProyectoGrado;
import com.arquisoft.entregables.domain.proyectogrado.rules.ProyectoGradoExisteRule;
import com.arquisoft.entregables.domain.proyectogrado.rules.impl.ProyectoGradoExisteRuleImpl;
import com.arquisoft.shared.util.UtilTexto;
import org.springframework.stereotype.Component;

@Component
public class RegistrarVersionCerradaArtefactoValidatorImpl implements RegistrarVersionCerradaArtefactoValidator {

    private final ProyectoGradoExisteRule proyectoGradoExisteRule;
    private final ArchivoOrigenArtefactoExisteRule archivoOrigenArtefactoExisteRule;

    public RegistrarVersionCerradaArtefactoValidatorImpl() {
        this.proyectoGradoExisteRule = new ProyectoGradoExisteRuleImpl();
        this.archivoOrigenArtefactoExisteRule = new ArchivoOrigenArtefactoExisteRuleImpl();
    }

    @Override
    public void validar(RegistroVersionCerradaArtefactoDomain registro, ProyectoGradoDomain proyecto,
                        String etagOrigen) {
        var artefacto = registro.getArtefacto();
        proyectoGradoExisteRule.validar(new ExistenciaProyectoGrado(artefacto.getProyectoGrado(), !proyecto.esVacio()));
        archivoOrigenArtefactoExisteRule.validar(new ExistenciaArchivoOrigenArtefacto(
                artefacto.getId(), artefacto.getNombreDocumento(), registro.getClaveOrigen(),
                !UtilTexto.esVacioONulo(etagOrigen)));
    }
}
