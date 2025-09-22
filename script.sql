-- FitFlow heavy seed (~500k completed_activities)
-- Esquema: fitflow_db (MariaDB 10.11+)
-- CTEs locales por sentencia (no globales)

SET NAMES utf8mb4;

-- Aumentar límite de iteraciones recursivas
SET SESSION max_recursive_iterations = 10000;

-- Parámetros
SET @N_USERS := 5000;
SET @N_COACHES := 80;
SET @ROUTINES_PER_USER := 2;
SET @LOGS_PER_ROUTINE := 5;
SET @COMPLETED_PER_LOG := 10;
SET @ACTIVITIES_PER_ROUTINE := 3;
SET @N_HABITS := 120;
SET @N_GUIDES := 40;
SET @FAVS_PER_USER := 3;

START TRANSACTION;
SET FOREIGN_KEY_CHECKS = 0;
SET UNIQUE_CHECKS = 0;

TRUNCATE TABLE completed_activities;
TRUNCATE TABLE progress_log;
TRUNCATE TABLE reminder;
TRUNCATE TABLE routine_activities;
TRUNCATE TABLE routines;
TRUNCATE TABLE guide_recommended_habit;
TRUNCATE TABLE guides;
TRUNCATE TABLE user_habit;
TRUNCATE TABLE user_roles;
TRUNCATE TABLE habits;
TRUNCATE TABLE roles;
TRUNCATE TABLE users;
TRUNCATE TABLE auth_token;

SET FOREIGN_KEY_CHECKS = 1;

-- 1) Roles (simple y re-ejecutable)
INSERT IGNORE INTO roles (module, permission) VALUES
  ('ACTIVIDADES','AUDITOR'),
  ('ACTIVIDADES','EDITOR'),
  ('RUTINAS','AUDITOR'),
  ('RUTINAS','EDITOR'),
  ('RECORDATORIOS','AUDITOR'),
  ('RECORDATORIOS','EDITOR'),
  ('PROGRESO','AUDITOR'),
  ('PROGRESO','EDITOR'),
  ('GUIAS','AUDITOR'),
  ('GUIAS','EDITOR');

-- 2) Users: coaches primero
INSERT INTO users (username, email, password, coach_id)
WITH RECURSIVE u(seq) AS (
  SELECT 1 UNION ALL SELECT seq+1 FROM u WHERE seq < @N_COACHES
),
first_names(name) AS (
  SELECT 'Ariana' UNION ALL SELECT 'Lucas' UNION ALL SELECT 'Maya' UNION ALL
  SELECT 'Santiago' UNION ALL SELECT 'Isabella' UNION ALL SELECT 'Diego' UNION ALL
  SELECT 'Renata' UNION ALL SELECT 'Marco' UNION ALL SELECT 'Emma' UNION ALL
  SELECT 'Tomas' UNION ALL SELECT 'Zoe' UNION ALL SELECT 'Leo' UNION ALL
  SELECT 'Abril' UNION ALL SELECT 'Javier' UNION ALL SELECT 'Elena' UNION ALL
  SELECT 'Noa' UNION ALL SELECT 'Thiago' UNION ALL SELECT 'Ivanna' UNION ALL
  SELECT 'Nicolas' UNION ALL SELECT 'Valeria'
),
last_names(name) AS (
  SELECT 'Cascante' UNION ALL SELECT 'Salas' UNION ALL SELECT 'Espinoza' UNION ALL
  SELECT 'Chaves' UNION ALL SELECT 'Calderon' UNION ALL SELECT 'Pizarro' UNION ALL
  SELECT 'Alpizar' UNION ALL SELECT 'Solis' UNION ALL SELECT 'Leiva' UNION ALL
  SELECT 'Carvajal' UNION ALL SELECT 'Chacon' UNION ALL SELECT 'Arrieta' UNION ALL
  SELECT 'Sequeira' UNION ALL SELECT 'Mendez' UNION ALL SELECT 'Quiros' UNION ALL
  SELECT 'Esquivel' UNION ALL SELECT 'Campos' UNION ALL SELECT 'Gamboa' UNION ALL
  SELECT 'Jimenez' UNION ALL SELECT 'Ulate'
),
fn AS (SELECT name, ROW_NUMBER() OVER () rn, COUNT(*) OVER () tot FROM first_names),
ln AS (SELECT name, ROW_NUMBER() OVER () rn, COUNT(*) OVER () tot FROM last_names)
SELECT
  LOWER(CONCAT(fn.name,'.',ln.name,'.',LPAD(u.seq,4,'0'))),
  LOWER(CONCAT(fn.name,'.',ln.name,'.',LPAD(u.seq,4,'0'),'@fitflow.test')),
  '$2a$12$kzhFS4BHdP3ACWv3lR2OHOVOsmVk2011LLefmm76eQ1ebJM0UNXfi',
  NULL
FROM u
JOIN fn ON ((u.seq % fn.tot)+1)=fn.rn
JOIN ln ON ((u.seq % ln.tot)+1)=ln.rn
ORDER BY u.seq;

-- 2B) Resto de usuarios con coach asignado 1..@N_COACHES
INSERT INTO users (username, email, password, coach_id)
WITH RECURSIVE u(seq) AS (
  SELECT @N_COACHES+1 UNION ALL SELECT seq+1 FROM u WHERE seq < @N_USERS
),
first_names(name) AS (
  SELECT 'Ariana' UNION ALL SELECT 'Lucas' UNION ALL SELECT 'Maya' UNION ALL
  SELECT 'Santiago' UNION ALL SELECT 'Isabella' UNION ALL SELECT 'Diego' UNION ALL
  SELECT 'Renata' UNION ALL SELECT 'Marco' UNION ALL SELECT 'Emma' UNION ALL
  SELECT 'Tomas' UNION ALL SELECT 'Zoe' UNION ALL SELECT 'Leo' UNION ALL
  SELECT 'Abril' UNION ALL SELECT 'Javier' UNION ALL SELECT 'Elena' UNION ALL
  SELECT 'Noa' UNION ALL SELECT 'Thiago' UNION ALL SELECT 'Ivanna' UNION ALL
  SELECT 'Nicolas' UNION ALL SELECT 'Valeria'
),
last_names(name) AS (
  SELECT 'Cascante' UNION ALL SELECT 'Salas' UNION ALL SELECT 'Espinoza' UNION ALL
  SELECT 'Chaves' UNION ALL SELECT 'Calderon' UNION ALL SELECT 'Pizarro' UNION ALL
  SELECT 'Alpizar' UNION ALL SELECT 'Solis' UNION ALL SELECT 'Leiva' UNION ALL
  SELECT 'Carvajal' UNION ALL SELECT 'Chacon' UNION ALL SELECT 'Arrieta' UNION ALL
  SELECT 'Sequeira' UNION ALL SELECT 'Mendez' UNION ALL SELECT 'Quiros' UNION ALL
  SELECT 'Esquivel' UNION ALL SELECT 'Campos' UNION ALL SELECT 'Gamboa' UNION ALL
  SELECT 'Jimenez' UNION ALL SELECT 'Ulate'
),
fn AS (SELECT name, ROW_NUMBER() OVER () rn, COUNT(*) OVER () tot FROM first_names),
ln AS (SELECT name, ROW_NUMBER() OVER () rn, COUNT(*) OVER () tot FROM last_names)
SELECT
  LOWER(CONCAT(fn.name,'.',ln.name,'.',LPAD(u.seq,4,'0'))),
  LOWER(CONCAT(fn.name,'.',ln.name,'.',LPAD(u.seq,4,'0'),'@fitflow.test')),
  '$2a$12$kzhFS4BHdP3ACWv3lR2OHOVOsmVk2011LLefmm76eQ1ebJM0UNXfi',
  1 + MOD(u.seq-1, @N_COACHES)
FROM u
JOIN fn ON ((u.seq % fn.tot)+1)=fn.rn
JOIN ln ON ((u.seq % ln.tot)+1)=ln.rn
ORDER BY u.seq;

-- 2C) user_roles (sin duplicar PK compuesta)
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
JOIN roles r
WHERE MOD(u.id + r.id, 7) = 0;

-- 3) Habits
INSERT INTO habits (name,category,description)
WITH RECURSIVE h(seq) AS (
  SELECT 1 UNION ALL SELECT seq+1 FROM h WHERE seq < @N_HABITS
),
acts(a) AS (SELECT 'Respirar' UNION ALL SELECT 'Caminar' UNION ALL SELECT 'Meditar' UNION ALL SELECT 'Hidratar'),
adjs(a) AS (SELECT 'Vital' UNION ALL SELECT 'Ligero' UNION ALL SELECT 'Sereno' UNION ALL SELECT 'Enfocado'),
cats(c) AS (SELECT 'PHYSICAL' UNION ALL SELECT 'MENTAL' UNION ALL SELECT 'SLEEP' UNION ALL SELECT 'DIET'),
A AS (SELECT a,ROW_NUMBER() OVER() rn,COUNT(*) OVER() tot FROM acts),
B AS (SELECT a,ROW_NUMBER() OVER() rn,COUNT(*) OVER() tot FROM adjs),
C AS (SELECT c,ROW_NUMBER() OVER() rn,COUNT(*) OVER() tot FROM cats)
SELECT CONCAT(b.a,' ',a.a,' ',LPAD(h.seq,3,'0')),
       c.c,
       CONCAT('Habito ',LOWER(b.a),' para ',LOWER(a.a))
FROM h
JOIN A a ON ((h.seq%a.tot)+1)=a.rn
JOIN B b ON ((h.seq%b.tot)+1)=b.rn
JOIN C c ON ((h.seq%c.tot)+1)=c.rn
ORDER BY h.seq;

-- 3B) user_habit: 3 por usuario
INSERT INTO user_habit(user_id,habit_id)
WITH slots AS (SELECT 1 AS slot UNION ALL SELECT 2 UNION ALL SELECT 3),
h_rank AS (SELECT id AS habit_id, ROW_NUMBER() OVER (ORDER BY id) rn, COUNT(*) OVER() tot FROM habits)
SELECT u.id,
       h.habit_id
FROM users u
CROSS JOIN slots s
JOIN h_rank h ON h.rn = (((u.id-1)*@FAVS_PER_USER + s.slot - 1) % h.tot) + 1;

-- 4) Guides
INSERT INTO guides(title,content,category)
WITH RECURSIVE g(seq) AS (
  SELECT 1 UNION ALL SELECT seq+1 FROM g WHERE seq < @N_GUIDES
)
SELECT CONCAT('Guia ',g.seq),
       CONCAT('Contenido guia ',g.seq),
       ELT(1+MOD(g.seq,4),'PHYSICAL','MENTAL','SLEEP','DIET')
FROM g;

-- guide_recommended_habit: 2 por guía
INSERT INTO guide_recommended_habit(guide_id,habit_id)
WITH g_rank AS (SELECT id AS guide_id, ROW_NUMBER() OVER (ORDER BY id) rn FROM guides),
     h_rank AS (SELECT id AS habit_id, ROW_NUMBER() OVER (ORDER BY id) rn, COUNT(*) OVER() tot FROM habits),
     picks AS (SELECT 1 AS slot UNION ALL SELECT 2)
SELECT gr.guide_id, h.habit_id
FROM g_rank gr
CROSS JOIN picks p
JOIN h_rank h ON h.rn = (((gr.guide_id-1)*2 + p.slot - 1) % h.tot) + 1;

-- 5) Routines: 2 por usuario + bitmask días (varbinary)
INSERT INTO routines(user_id,title,days_of_week)
WITH slots AS (SELECT 1 AS slot UNION ALL SELECT 2)
SELECT u.id,
       CONCAT(ELT(1 + MOD(u.id+slot,5),'Impulso','Balance','Foco','Recarga','Evolucion'),
              ' ', ELT(1 + MOD(u.id+slot,4),'Activa','Serena','Dinamica','Integral'),
              ' ', LPAD(slot,2,'0')),
       UNHEX(LPAD(HEX(
         (1 << (1 + MOD(u.id+slot,7))) |
         (1 << (1 + MOD(u.id+slot+2,7))) |
         (1 << (1 + MOD(u.id+slot+4,7)))
       ), 2, '0'))
FROM users u
JOIN slots;

-- 5B) routine_activities: 3 por rutina
INSERT INTO routine_activities(routine_id,habit_id,duration,notes)
WITH slots AS (SELECT 1 AS slot UNION ALL SELECT 2 UNION ALL SELECT 3),
h_rank AS (SELECT id AS habit_id, ROW_NUMBER() OVER (ORDER BY id) rn, COUNT(*) OVER() tot FROM habits)
SELECT r.id,
       h.habit_id,
       12 + (s.slot * 6) + MOD(r.id,5)*2,
       CONCAT('Nota act ',r.id,'-',h.habit_id,' | ref-', LPAD(CONV(UUID_SHORT(),10,32), 6, '0'))
FROM routines r
JOIN slots s
JOIN h_rank h ON h.rn = (((r.id-1)*@ACTIVITIES_PER_ROUTINE + s.slot - 1) % h.tot) + 1
ORDER BY r.id, s.slot;

-- 6) progress_log: 5 por rutina (columna log_date)
INSERT INTO progress_log(user_id,routine_id,log_date)
WITH slots AS (SELECT 1 AS slot UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5)
SELECT r.user_id, r.id,
       DATE_ADD('2024-11-01', INTERVAL (r.id*2 + s.slot*3) DAY)
FROM routines r
JOIN slots s
ORDER BY r.id, s.slot;

-- 7) completed_activities: ~500k
INSERT INTO completed_activities(habit_id,progress_log_id,completed_at,notes)
WITH RECURSIVE slots AS (
  SELECT 1 AS slot
  UNION ALL SELECT slot+1 FROM slots WHERE slot < @COMPLETED_PER_LOG
),
h_rank AS (SELECT id AS habit_id, ROW_NUMBER() OVER (ORDER BY id) rn, COUNT(*) OVER() tot FROM habits)
SELECT
  h.habit_id,
  pl.id,
  TIMESTAMPADD(MINUTE, 25*s.slot + MOD(pl.id, 13)*2, 
    TIMESTAMP(DATE(pl.log_date), '06:05:00')),
  CONCAT('Completado ',pl.id,'-',h.habit_id,'-',s.slot)
FROM progress_log pl
JOIN slots s
JOIN h_rank h ON h.rn = (((pl.id-1)*@COMPLETED_PER_LOG + s.slot - 1) % h.tot) + 1
ORDER BY pl.id, s.slot;

-- 8) reminder
INSERT INTO reminder(user_id,habit_id,frequency,message,time)
SELECT u.id,h.id,
       ELT(1 + MOD(u.id,2), 'DAILY','WEEKLY'),
       CONCAT('Recordatorio de ',h.name),
       TIMESTAMP(DATE('2024-11-05'),MAKETIME(7 + MOD(u.id,2), 30, 0))
FROM users u
JOIN habits h ON h.id<=10
WHERE u.id<=500;

SET UNIQUE_CHECKS = 1;
COMMIT;