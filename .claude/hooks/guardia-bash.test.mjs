import assert from 'node:assert/strict';
import { describe, it } from 'node:test';
import { evaluarComando } from './guardia-bash.mjs';

const enRama = (rama) => () => rama;

const PROHIBIDOS = [
  ['merge de un PR', 'gh pr merge 12 --squash', 'feature/HU-1-x'],
  ['merge por la API', 'gh api -X PUT repos/o/r/pulls/5/merge', 'feature/HU-1-x'],
  ['aprobación de un PR', 'gh pr review 3 --approve', 'feature/HU-1-x'],
  [
    'aprobación por la API (POST explícito)',
    'gh api -X POST repos/o/r/pulls/5/reviews -f event=APPROVE',
    'feature/HU-1-x',
  ],
  [
    'aprobación por la API (campo implícito)',
    'gh api repos/o/r/pulls/5/reviews -f event=APPROVE',
    'feature/HU-1-x',
  ],
  [
    'merge por GraphQL',
    'gh api graphql -f query=\'mutation { mergePullRequest(input: {pullRequestId: "x"}) { clientMutationId } }\'',
    'feature/HU-1-x',
  ],
  [
    'aprobación por GraphQL',
    "gh api graphql -f query='mutation { addPullRequestReview(input: {event: APPROVE}) { clientMutationId } }'",
    'feature/HU-1-x',
  ],
  ['push forzado', 'git push --force origin feature/HU-1-x', 'feature/HU-1-x'],
  ['push forzado corto', 'git push -f', 'feature/HU-1-x'],
  ['push forzado combinado', 'git push -fu origin feature/HU-1-x', 'feature/HU-1-x'],
  [
    'push con force-with-lease',
    'git push --force-with-lease origin feature/HU-1-x',
    'feature/HU-1-x',
  ],
  ['push forzado por refspec', 'git push origin +feature/HU-1-x', 'feature/HU-1-x'],
  ['borrar una rama remota', 'git push origin --delete feature/HU-1-x', 'feature/HU-1-x'],
  ['borrar con refspec vacío', 'git push origin :feature/HU-1-x', 'feature/HU-1-x'],
  ['push a main', 'git push origin main', 'feature/HU-1-x'],
  ['push a develop', 'git push -u origin develop', 'feature/HU-1-x'],
  ['push de HEAD a main', 'git push origin HEAD:main', 'feature/HU-1-x'],
  ['push desde develop sin destino', 'git push', 'develop'],
  ['push de HEAD desde main', 'git push -u origin HEAD', 'main'],
  ['commit sobre main', 'git commit -m "feat: x"', 'main'],
  ['commit sobre develop', 'git commit -m "feat: x"', 'develop'],
  ['add forzado', 'git add -f fichas/domain/src/main/java/A.java', 'feature/HU-1-x'],
  ['add de .env', 'git add .env', 'feature/HU-1-x'],
  ['add de un .env.properties', 'git add .env.dev.properties', 'feature/HU-1-x'],
  ['add de una llave', 'git add certs/jwt.key', 'feature/HU-1-x'],
  ['add de build', 'git add fichas/infrastructure/build/libs/app.jar', 'feature/HU-1-x'],
  ['add de .gradle', 'git add .gradle/', 'feature/HU-1-x'],
  ['add de logs', 'git add logs/app.log', 'feature/HU-1-x'],
  ['add de .workspace', 'git add .workspace/h-plan/PLAN-HU-1.md', 'feature/HU-1-x'],
  ['add de la config local', 'git add .claude/settings.local.json', 'feature/HU-1-x'],
  ['orden encadenada', 'cd /tmp/x && git push --force', 'feature/HU-1-x'],
  ['orden dentro de bash -c', 'bash -c "git push --force origin x"', 'feature/HU-1-x'],
  ['push con -C sobre main', 'git -C /tmp/repo push origin main', 'feature/HU-1-x'],
  ['push forzado con operador de PowerShell', '& git push --force', 'feature/HU-1-x'],
  ['push forzado tras ; en PowerShell', 'git status; git push -f', 'feature/HU-1-x'],
];

const PERMITIDOS = [
  ['push de la rama de trabajo', 'git push -u origin feature/HU-1-x', 'feature/HU-1-x'],
  ['push de HEAD desde una rama de trabajo', 'git push origin HEAD', 'feature/HU-1-x'],
  ['push con --follow-tags', 'git push --follow-tags origin feature/HU-1-x', 'feature/HU-1-x'],
  ['push con -C y variables sin resolver', 'git -C "$TMP" push -u origin "$RAMA"', 'develop'],
  ['commit en una rama de trabajo', 'git commit -m "feat(x): y"', 'feature/HU-1-x'],
  ['commit con rama desconocida', 'git commit -m "feat(x): y"', null],
  [
    'add de código, migración y .env.example',
    'git add fichas/infrastructure/src/main/resources/db/migration/fichas/V20261006120000__x.sql .env.example',
    'feature/HU-1-x',
  ],
  [
    'add de un paquete Java llamado build',
    'git add shared/util/src/main/java/com/arquisoft/build/Util.java',
    'feature/HU-1-x',
  ],
  [
    'crear un PR',
    'gh pr create --base develop --head feature/HU-1-x --title "feat: x"',
    'feature/HU-1-x',
  ],
  ['ver un PR', 'gh pr view 5 --json state', 'feature/HU-1-x'],
  ['comentar un PR', 'gh pr comment 5 --body "listo para revisión"', 'feature/HU-1-x'],
  [
    'listar las revisiones por la API (lectura)',
    'gh api repos/o/r/pulls/5/reviews',
    'feature/HU-1-x',
  ],
  [
    'publicar en arquisoft-docs por la Contents API',
    'gh api "repos/arquisoft-uco/arquisoft-docs/contents/docs/hus/planes/PLAN-HU-1.md" --method PUT --input /tmp/body.json',
    'feature/HU-1-x',
  ],
  ['un comando ajeno', './gradlew -p fichas check', 'develop'],
  ['leer la rama', 'git branch --show-current', 'develop'],
  [
    'un mensaje de commit que menciona el merge',
    'git commit -m "$(cat <<\'EOF\'\nfix(agentes): bloquear gh pr merge y git push --force\n\ngh pr merge ya no se ejecuta desde el agente\nEOF\n)"',
    'feature/HU-1-x',
  ],
  [
    'un here-string de PowerShell que menciona el merge',
    "git commit -m @'\nfix(agentes): bloquear gh pr merge\n'@",
    'feature/HU-1-x',
  ],
];

describe('guardia-bash', () => {
  for (const [nombre, comando, rama] of PROHIBIDOS) {
    it(`rechaza ${nombre}`, () => {
      // Act
      const veredicto = evaluarComando(comando, enRama(rama));

      // Assert
      assert.equal(veredicto?.decision, 'deny');
      assert.ok(veredicto.razon.length > 0);
    });
  }

  for (const [nombre, comando, rama] of PERMITIDOS) {
    it(`permite ${nombre}`, () => {
      // Act
      const veredicto = evaluarComando(comando, enRama(rama));

      // Assert
      assert.equal(veredicto, null);
    });
  }
});
