SELECT format('GRANT CONNECT ON DATABASE %I TO sync_lector', current_database()) \gexec
GRANT USAGE ON SCHEMA public TO sync_lector;
REVOKE ALL ON ALL TABLES IN SCHEMA public FROM sync_lector;
SELECT format('GRANT SELECT ON TABLE public.%I TO sync_lector', t)
  FROM unnest(string_to_array(:'tablas', ',')) AS t \gexec
