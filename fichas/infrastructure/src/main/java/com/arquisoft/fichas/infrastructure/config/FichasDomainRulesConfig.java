package com.arquisoft.fichas.infrastructure.config;

import com.arquisoft.fichas.domain.estadoevaluacionficha.rules.EstadoEvaluacionNoDuplicadoRule;
import com.arquisoft.fichas.domain.estadoevaluacionficha.rules.EvaluacionFichaExisteRule;
import com.arquisoft.fichas.domain.estadoevaluacionficha.rules.RepresentantePropietarioEvaluacionRule;
import com.arquisoft.fichas.domain.estadoevaluacionficha.rules.impl.EstadoEvaluacionNoDuplicadoRuleImpl;
import com.arquisoft.fichas.domain.estadoevaluacionficha.rules.impl.EvaluacionFichaExisteRuleImpl;
import com.arquisoft.fichas.domain.estadoevaluacionficha.rules.impl.RepresentantePropietarioEvaluacionRuleImpl;
import com.arquisoft.fichas.domain.estadofichaperfil.rules.EstadoFichaPerfilEnTerminalRule;
import com.arquisoft.fichas.domain.estadofichaperfil.rules.impl.EstadoFichaPerfilEnTerminalRuleImpl;
import com.arquisoft.fichas.domain.estudiante.rules.EstudiantesExistenRule;
import com.arquisoft.fichas.domain.estudiante.rules.impl.EstudiantesExistenRuleImpl;
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
import com.arquisoft.fichas.domain.evaluacionfichaperfil.rules.EvaluacionNoDuplicadaRule;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.rules.RepresentanteComiteExisteRule;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.rules.impl.EvaluacionNoDuplicadaRuleImpl;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.rules.impl.RepresentanteComiteExisteRuleImpl;
import com.arquisoft.fichas.domain.fichaperfil.rules.AsesorFichaDiferenteRule;
import com.arquisoft.fichas.domain.fichaperfil.rules.AsesorFichaExisteRule;
import com.arquisoft.fichas.domain.fichaperfil.rules.FichaPerfilExisteRule;
import com.arquisoft.fichas.domain.fichaperfil.rules.FichaPerfilTituloUnicoRule;
import com.arquisoft.fichas.domain.fichaperfil.rules.impl.AsesorFichaDiferenteRuleImpl;
import com.arquisoft.fichas.domain.fichaperfil.rules.impl.AsesorFichaExisteRuleImpl;
import com.arquisoft.fichas.domain.fichaperfil.rules.impl.FichaPerfilExisteRuleImpl;
import com.arquisoft.fichas.domain.fichaperfil.rules.impl.FichaPerfilTituloUnicoRuleImpl;
import com.arquisoft.fichas.domain.itemfichaperfil.rules.ItemFichaPerfilExisteRule;
import com.arquisoft.fichas.domain.itemfichaperfil.rules.ItemFichaPropiaRule;
import com.arquisoft.fichas.domain.itemfichaperfil.rules.ItemSinRevisionesRule;
import com.arquisoft.fichas.domain.itemfichaperfil.rules.ItemTipoNoDuplicadoRule;
import com.arquisoft.fichas.domain.itemfichaperfil.rules.impl.ItemFichaPerfilExisteRuleImpl;
import com.arquisoft.fichas.domain.itemfichaperfil.rules.impl.ItemFichaPropiaRuleImpl;
import com.arquisoft.fichas.domain.itemfichaperfil.rules.impl.ItemSinRevisionesRuleImpl;
import com.arquisoft.fichas.domain.itemfichaperfil.rules.impl.ItemTipoNoDuplicadoRuleImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FichasDomainRulesConfig {

    @Bean
    public EstudianteFichaPerfilCupoDisponibleRule estudianteFichaPerfilCupoDisponibleRule() {
        return new EstudianteFichaPerfilCupoDisponibleRuleImpl();
    }

    @Bean
    public EstudiantesSinDuplicadosRule estudiantesSinDuplicadosRule() {
        return new EstudiantesSinDuplicadosRuleImpl();
    }

    @Bean
    public EstudiantesNoVinculadosRule estudiantesNoVinculadosRule() {
        return new EstudiantesNoVinculadosRuleImpl();
    }

    @Bean
    public VinculoEstudianteFichaExisteRule vinculoEstudianteFichaExisteRule() {
        return new VinculoEstudianteFichaExisteRuleImpl();
    }

    @Bean
    public EstudiantePropietarioFichaRule estudiantePropietarioFichaRule() {
        return new EstudiantePropietarioFichaRuleImpl();
    }

    @Bean
    public EstudiantesExistenRule estudiantesExistenRule() {
        return new EstudiantesExistenRuleImpl();
    }

    @Bean
    public FichaPerfilExisteRule fichaPerfilExisteRule() {
        return new FichaPerfilExisteRuleImpl();
    }

    @Bean
    public FichaPerfilTituloUnicoRule fichaPerfilTituloUnicoRule() {
        return new FichaPerfilTituloUnicoRuleImpl();
    }

    @Bean
    public AsesorFichaExisteRule asesorFichaExisteRule() {
        return new AsesorFichaExisteRuleImpl();
    }

    @Bean
    public AsesorFichaDiferenteRule asesorFichaDiferenteRule() {
        return new AsesorFichaDiferenteRuleImpl();
    }

    @Bean
    public RepresentanteComiteExisteRule representanteComiteExisteRule() {
        return new RepresentanteComiteExisteRuleImpl();
    }

    @Bean
    public EvaluacionNoDuplicadaRule evaluacionNoDuplicadaRule() {
        return new EvaluacionNoDuplicadaRuleImpl();
    }

    @Bean
    public EvaluacionFichaExisteRule evaluacionFichaExisteRule() {
        return new EvaluacionFichaExisteRuleImpl();
    }

    @Bean
    public RepresentantePropietarioEvaluacionRule representantePropietarioEvaluacionRule() {
        return new RepresentantePropietarioEvaluacionRuleImpl();
    }

    @Bean
    public EstadoEvaluacionNoDuplicadoRule estadoEvaluacionNoDuplicadoRule() {
        return new EstadoEvaluacionNoDuplicadoRuleImpl();
    }

    @Bean
    public ItemFichaPerfilExisteRule itemFichaPerfilExisteRule() {
        return new ItemFichaPerfilExisteRuleImpl();
    }

    @Bean
    public ItemFichaPropiaRule itemFichaPropiaRule() {
        return new ItemFichaPropiaRuleImpl();
    }

    @Bean
    public ItemTipoNoDuplicadoRule itemTipoNoDuplicadoRule() {
        return new ItemTipoNoDuplicadoRuleImpl();
    }

    @Bean
    public ItemSinRevisionesRule itemSinRevisionesRule() {
        return new ItemSinRevisionesRuleImpl();
    }

    @Bean
    public EstadoFichaPerfilEnTerminalRule estadoFichaPerfilEnTerminalRule() {
        return new EstadoFichaPerfilEnTerminalRuleImpl();
    }
}
