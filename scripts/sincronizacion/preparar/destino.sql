CREATE EXTENSION IF NOT EXISTS postgres_fdw;
CREATE SCHEMA IF NOT EXISTS sync;
GRANT USAGE ON SCHEMA sync TO arquisoft_user;

SELECT format('DROP SCHEMA IF EXISTS %I CASCADE', 'sync_' || split_part(o, '|', 1))
  FROM unnest(string_to_array(:'origenes', ';')) AS o \gexec

SELECT format('DROP SERVER IF EXISTS %I CASCADE', 'sync_' || split_part(o, '|', 1))
  FROM unnest(string_to_array(:'origenes', ';')) AS o \gexec

SELECT format('CREATE SERVER %I FOREIGN DATA WRAPPER postgres_fdw OPTIONS (host %L, port %L, dbname %L)',
              'sync_' || split_part(o, '|', 1), :'sync_host', :'sync_port', split_part(o, '|', 2))
  FROM unnest(string_to_array(:'origenes', ';')) AS o \gexec

SELECT format('CREATE USER MAPPING FOR arquisoft_user SERVER %I OPTIONS (user %L, password %L)',
              'sync_' || split_part(o, '|', 1), 'sync_lector', :'sync_password')
  FROM unnest(string_to_array(:'origenes', ';')) AS o \gexec

SELECT format('CREATE USER MAPPING FOR CURRENT_USER SERVER %I OPTIONS (user %L, password %L)',
              'sync_' || split_part(o, '|', 1), 'sync_lector', :'sync_password')
  FROM unnest(string_to_array(:'origenes', ';')) AS o \gexec

SELECT format('CREATE SCHEMA %I', 'sync_' || split_part(o, '|', 1))
  FROM unnest(string_to_array(:'origenes', ';')) AS o \gexec

SELECT format('IMPORT FOREIGN SCHEMA public LIMIT TO (%s) FROM SERVER %I INTO %I',
              (SELECT string_agg(format('%I', t), ', ') FROM unnest(string_to_array(split_part(o, '|', 3), ',')) AS t),
              'sync_' || split_part(o, '|', 1), 'sync_' || split_part(o, '|', 1))
  FROM unnest(string_to_array(:'origenes', ';')) AS o \gexec

SELECT format('DROP USER MAPPING FOR CURRENT_USER SERVER %I', 'sync_' || split_part(o, '|', 1))
  FROM unnest(string_to_array(:'origenes', ';')) AS o \gexec

SELECT format('GRANT USAGE ON SCHEMA %I TO arquisoft_user', 'sync_' || split_part(o, '|', 1))
  FROM unnest(string_to_array(:'origenes', ';')) AS o \gexec

SELECT format('GRANT SELECT ON ALL TABLES IN SCHEMA %I TO arquisoft_user', 'sync_' || split_part(o, '|', 1))
  FROM unnest(string_to_array(:'origenes', ';')) AS o \gexec
