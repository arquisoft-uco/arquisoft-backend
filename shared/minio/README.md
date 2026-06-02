# Estructura final recomendada para el key o path de los archivos
```
arquisoft-artefactos
└── artefacto/
    └── {artefactoId}/
        └── {versionArtefactoId}.{ext}
```

```
-- Lo que guarda tu BD del contexto
storage_key VARCHAR(255)  
-- Valor: "artefacto/uuid-artefacto/uuid-version-artefacto.pdf"
```