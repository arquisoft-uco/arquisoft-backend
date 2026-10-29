package com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.validator.impl;

import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.validator.SubirVersionRepositorioArtefactoContenidoValidator;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.AnalisisArchivoDomain;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.CargaPendienteDomain;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.SubidaVersionRepositorioArtefactoDomain;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.model.CoincidenciaTipoArchivo;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.model.ConsistenciaCarga;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.rules.CargaSinModificarRule;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.rules.TipoArchivoCoincideRule;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.rules.impl.CargaSinModificarRuleImpl;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.rules.impl.TipoArchivoCoincideRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class SubirVersionRepositorioArtefactoContenidoValidatorImpl
        implements SubirVersionRepositorioArtefactoContenidoValidator {

    private final CargaSinModificarRule cargaSinModificarRule;
    private final TipoArchivoCoincideRule tipoArchivoCoincideRule;

    public SubirVersionRepositorioArtefactoContenidoValidatorImpl() {
        this.cargaSinModificarRule = new CargaSinModificarRuleImpl();
        this.tipoArchivoCoincideRule = new TipoArchivoCoincideRuleImpl();
    }

    @Override
    public void validar(SubidaVersionRepositorioArtefactoDomain subida, CargaPendienteDomain carga,
                        AnalisisArchivoDomain analisis) {
        cargaSinModificarRule.validar(new ConsistenciaCarga(
                subida.getCarga(), carga.getNombreArchivo(), carga.getEtag(), analisis.getEtag()));
        tipoArchivoCoincideRule.validar(
                new CoincidenciaTipoArchivo(carga.getNombreArchivo(), carga.getTipoContenido(), analisis));
    }
}
