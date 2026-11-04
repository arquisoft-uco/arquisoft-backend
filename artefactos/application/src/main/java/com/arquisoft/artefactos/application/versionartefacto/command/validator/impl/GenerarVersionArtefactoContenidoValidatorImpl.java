package com.arquisoft.artefactos.application.versionartefacto.command.validator.impl;

import com.arquisoft.artefactos.application.versionartefacto.command.validator.GenerarVersionArtefactoContenidoValidator;
import com.arquisoft.artefactos.domain.versionartefacto.GeneracionVersionArtefactoDomain;
import com.arquisoft.artefactos.domain.versionartefacto.model.CargaAnalizada;
import com.arquisoft.artefactos.domain.versionartefacto.model.CoincidenciaTipoArchivo;
import com.arquisoft.artefactos.domain.versionartefacto.model.ConsistenciaCarga;
import com.arquisoft.artefactos.domain.versionartefacto.rules.CargaSinModificarRule;
import com.arquisoft.artefactos.domain.versionartefacto.rules.TipoArchivoCoincideRule;
import com.arquisoft.artefactos.domain.versionartefacto.rules.impl.CargaSinModificarRuleImpl;
import com.arquisoft.artefactos.domain.versionartefacto.rules.impl.TipoArchivoCoincideRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class GenerarVersionArtefactoContenidoValidatorImpl implements GenerarVersionArtefactoContenidoValidator {

    private final CargaSinModificarRule cargaSinModificarRule;
    private final TipoArchivoCoincideRule tipoArchivoCoincideRule;

    public GenerarVersionArtefactoContenidoValidatorImpl() {
        this.cargaSinModificarRule = new CargaSinModificarRuleImpl();
        this.tipoArchivoCoincideRule = new TipoArchivoCoincideRuleImpl();
    }

    @Override
    public void validar(GeneracionVersionArtefactoDomain generacion, CargaAnalizada cargaAnalizada) {
        var carga = cargaAnalizada.carga();
        var analisis = cargaAnalizada.analisis();
        cargaSinModificarRule.validar(
                new ConsistenciaCarga(
                        generacion.getCarga(), carga.getNombreArchivo(), carga.getEtag(), analisis.getEtag()));
        tipoArchivoCoincideRule.validar(
                new CoincidenciaTipoArchivo(carga.getNombreArchivo(), carga.getTipoContenido(), analisis));
    }
}
