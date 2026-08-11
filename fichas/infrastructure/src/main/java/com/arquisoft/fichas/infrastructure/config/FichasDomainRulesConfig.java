package com.arquisoft.fichas.infrastructure.config;

import com.arquisoft.fichas.domain.asesorficha.port.out.AsesorFichaOutputPort;
import com.arquisoft.fichas.domain.estadoevaluacionficha.port.out.EstadoEvaluacionFichaOutputPort;
import com.arquisoft.fichas.domain.estadoevaluacionficha.rules.EstadoEvaluacionNoDuplicadoRule;
import com.arquisoft.fichas.domain.estadoevaluacionficha.rules.EvaluacionFichaExisteRule;
import com.arquisoft.fichas.domain.estadoevaluacionficha.rules.RepresentantePropietarioEvaluacionRule;
import com.arquisoft.fichas.domain.estadoevaluacionficha.rules.impl.EstadoEvaluacionNoDuplicadoRuleImpl;
import com.arquisoft.fichas.domain.estadoevaluacionficha.rules.impl.EvaluacionFichaExisteRuleImpl;
import com.arquisoft.fichas.domain.estadoevaluacionficha.rules.impl.RepresentantePropietarioEvaluacionRuleImpl;
import com.arquisoft.fichas.domain.estadofichaperfil.port.out.EstadoFichaPerfilOutputPort;
import com.arquisoft.fichas.domain.estadofichaperfil.rules.EstadoFichaPerfilEnTerminalRule;
import com.arquisoft.fichas.domain.estadofichaperfil.rules.impl.EstadoFichaPerfilEnTerminalRuleImpl;
import com.arquisoft.fichas.domain.estudiante.port.out.EstudianteOutputPort;
import com.arquisoft.fichas.domain.estudiante.rules.EstudiantesExistenRule;
import com.arquisoft.fichas.domain.estudiante.rules.impl.EstudiantesExistenRuleImpl;
import com.arquisoft.fichas.domain.estudiantefichaperfil.port.out.EstudianteFichaPerfilOutputPort;
import com.arquisoft.fichas.domain.estudiantefichaperfil.rules.EstudianteFichaPerfilCupoDisponibleRule;
import com.arquisoft.fichas.domain.estudiantefichaperfil.rules.EstudiantePropietarioFichaRule;
import com.arquisoft.fichas.domain.estudiantefichaperfil.rules.EstudiantesNoVinculadosRule;
import com.arquisoft.fichas.domain.estudiantefichaperfil.rules.EstudiantesSinDuplicadosRule;
import com.arquisoft.fichas.domain.estudiantefichaperfil.rules.VinculoEstudianteFichaExisteRule;
import com.arquisoft.fichas.domain.estudiantefichaperfil.rules.impl.EstudianteFichaPerfilCupoDisponibleRuleImpl;
import com.arquisoft.fichas.domain.estudiantefichaperfil.rules.impl.EstudiantePropietarioFichaRuleImpl;
import com.arquisoft.fichas.domain.estudiantefichaperfil.rules.impl.EstudiantesNoVinculadosRuleImpl;
import com.arquisoft.fichas.domain.estudiantefichaperfil.rules.impl.EstudiantesSinDuplicadosRuleImpl;
import com.arquisoft.fichas.domain.estudiantefichaperfil.rules.impl.VinculoEstudianteFichaExisteRuleImpl;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.port.out.EvaluacionFichaPerfilOutputPort;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.rules.EvaluacionNoDuplicadaRule;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.rules.RepresentanteComiteExisteRule;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.rules.impl.EvaluacionNoDuplicadaRuleImpl;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.rules.impl.RepresentanteComiteExisteRuleImpl;
import com.arquisoft.fichas.domain.fichaperfil.port.out.FichaPerfilOutputPort;
import com.arquisoft.fichas.domain.fichaperfil.rules.AsesorFichaDiferenteRule;
import com.arquisoft.fichas.domain.fichaperfil.rules.AsesorFichaExisteRule;
import com.arquisoft.fichas.domain.fichaperfil.rules.FichaPerfilExisteRule;
import com.arquisoft.fichas.domain.fichaperfil.rules.FichaPerfilTituloDisponibleRule;
import com.arquisoft.fichas.domain.fichaperfil.rules.FichaPerfilTituloUnicoRule;
import com.arquisoft.fichas.domain.fichaperfil.rules.impl.AsesorFichaDiferenteRuleImpl;
import com.arquisoft.fichas.domain.fichaperfil.rules.impl.AsesorFichaExisteRuleImpl;
import com.arquisoft.fichas.domain.fichaperfil.rules.impl.FichaPerfilExisteRuleImpl;
import com.arquisoft.fichas.domain.fichaperfil.rules.impl.FichaPerfilTituloDisponibleRuleImpl;
import com.arquisoft.fichas.domain.fichaperfil.rules.impl.FichaPerfilTituloUnicoRuleImpl;
import com.arquisoft.fichas.domain.itemfichaperfil.port.out.ItemFichaPerfilOutputPort;
import com.arquisoft.fichas.domain.itemfichaperfil.rules.ItemFichaPropiaRule;
import com.arquisoft.fichas.domain.itemfichaperfil.rules.ItemSinRevisionesRule;
import com.arquisoft.fichas.domain.itemfichaperfil.rules.ItemTipoNoDuplicadoRule;
import com.arquisoft.fichas.domain.itemfichaperfil.rules.impl.ItemFichaPropiaRuleImpl;
import com.arquisoft.fichas.domain.itemfichaperfil.rules.impl.ItemSinRevisionesRuleImpl;
import com.arquisoft.fichas.domain.itemfichaperfil.rules.impl.ItemTipoNoDuplicadoRuleImpl;
import com.arquisoft.fichas.domain.representantecomite.port.out.RepresentanteComiteOutputPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FichasDomainRulesConfig {

    @Bean
    public EstudianteFichaPerfilCupoDisponibleRule estudianteFichaPerfilCupoDisponibleRule(
            EstudianteFichaPerfilOutputPort estudianteFichaPerfilOutputPort) {
        return new EstudianteFichaPerfilCupoDisponibleRuleImpl(estudianteFichaPerfilOutputPort);
    }

    @Bean
    public EstudiantesSinDuplicadosRule estudiantesSinDuplicadosRule() {
        return new EstudiantesSinDuplicadosRuleImpl();
    }

    @Bean
    public EstudiantesNoVinculadosRule estudiantesNoVinculadosRule(
            EstudianteFichaPerfilOutputPort estudianteFichaPerfilOutputPort) {
        return new EstudiantesNoVinculadosRuleImpl(estudianteFichaPerfilOutputPort);
    }

    @Bean
    public VinculoEstudianteFichaExisteRule vinculoEstudianteFichaExisteRule(
            EstudianteFichaPerfilOutputPort estudianteFichaPerfilOutputPort) {
        return new VinculoEstudianteFichaExisteRuleImpl(estudianteFichaPerfilOutputPort);
    }

    @Bean
    public EstudiantePropietarioFichaRule estudiantePropietarioFichaRule(
            EstudianteFichaPerfilOutputPort estudianteFichaPerfilOutputPort) {
        return new EstudiantePropietarioFichaRuleImpl(estudianteFichaPerfilOutputPort);
    }

    @Bean
    public EstudiantesExistenRule estudiantesExistenRule(EstudianteOutputPort estudianteOutputPort) {
        return new EstudiantesExistenRuleImpl(estudianteOutputPort);
    }

    @Bean
    public FichaPerfilExisteRule fichaPerfilExisteRule(FichaPerfilOutputPort fichaPerfilOutputPort) {
        return new FichaPerfilExisteRuleImpl(fichaPerfilOutputPort);
    }

    @Bean
    public FichaPerfilTituloUnicoRule fichaPerfilTituloUnicoRule(FichaPerfilOutputPort fichaPerfilOutputPort) {
        return new FichaPerfilTituloUnicoRuleImpl(fichaPerfilOutputPort);
    }

    @Bean
    public FichaPerfilTituloDisponibleRule fichaPerfilTituloDisponibleRule(
            FichaPerfilOutputPort fichaPerfilOutputPort) {
        return new FichaPerfilTituloDisponibleRuleImpl(fichaPerfilOutputPort);
    }

    @Bean
    public AsesorFichaExisteRule asesorFichaExisteRule(AsesorFichaOutputPort asesorFichaOutputPort) {
        return new AsesorFichaExisteRuleImpl(asesorFichaOutputPort);
    }

    @Bean
    public AsesorFichaDiferenteRule asesorFichaDiferenteRule() {
        return new AsesorFichaDiferenteRuleImpl();
    }

    @Bean
    public RepresentanteComiteExisteRule representanteComiteExisteRule(
            RepresentanteComiteOutputPort representanteComiteOutputPort) {
        return new RepresentanteComiteExisteRuleImpl(representanteComiteOutputPort);
    }

    @Bean
    public EvaluacionNoDuplicadaRule evaluacionNoDuplicadaRule(
            EvaluacionFichaPerfilOutputPort evaluacionFichaPerfilOutputPort) {
        return new EvaluacionNoDuplicadaRuleImpl(evaluacionFichaPerfilOutputPort);
    }

    @Bean
    public EvaluacionFichaExisteRule evaluacionFichaExisteRule(
            EvaluacionFichaPerfilOutputPort evaluacionFichaPerfilOutputPort) {
        return new EvaluacionFichaExisteRuleImpl(evaluacionFichaPerfilOutputPort);
    }

    @Bean
    public RepresentantePropietarioEvaluacionRule representantePropietarioEvaluacionRule(
            EvaluacionFichaPerfilOutputPort evaluacionFichaPerfilOutputPort) {
        return new RepresentantePropietarioEvaluacionRuleImpl(evaluacionFichaPerfilOutputPort);
    }

    @Bean
    public EstadoEvaluacionNoDuplicadoRule estadoEvaluacionNoDuplicadoRule(
            EstadoEvaluacionFichaOutputPort estadoEvaluacionFichaOutputPort) {
        return new EstadoEvaluacionNoDuplicadoRuleImpl(estadoEvaluacionFichaOutputPort);
    }

    @Bean
    public ItemFichaPropiaRule itemFichaPropiaRule(EstudianteFichaPerfilOutputPort estudianteFichaPerfilOutputPort) {
        return new ItemFichaPropiaRuleImpl(estudianteFichaPerfilOutputPort);
    }

    @Bean
    public ItemTipoNoDuplicadoRule itemTipoNoDuplicadoRule(
            ItemFichaPerfilOutputPort itemFichaPerfilOutputPort) {
        return new ItemTipoNoDuplicadoRuleImpl(itemFichaPerfilOutputPort);
    }

    @Bean
    public ItemSinRevisionesRule itemSinRevisionesRule() {
        return new ItemSinRevisionesRuleImpl();
    }

    @Bean
    public EstadoFichaPerfilEnTerminalRule estadoFichaPerfilEnTerminalRule(
            EstadoFichaPerfilOutputPort estadoFichaPerfilOutputPort) {
        return new EstadoFichaPerfilEnTerminalRuleImpl(estadoFichaPerfilOutputPort);
    }
}
