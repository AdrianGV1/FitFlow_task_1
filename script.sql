-- FitFlow heavy seed (~500k registros en completed_activities)
-- Compatible con esquema fitflow_db (MariaDB 10.11+)
-- Autor: ChatGPT (ajustado para anonimo)

SET NAMES utf8mb4;

-- Semilla fija para determinismo
SET @rand_seed := 20250922;
SELECT RAND(@rand_seed);
SET @fixed_ts := 1735689600;          -- 2024-12-31 00:00:00 UTC
SET timestamp = @fixed_ts;

-- ===========================
-- Parámetros de volumen
-- ===========================
SET @N_USERS := 5000;          -- usuarios
SET @N_COACHES := 80;          -- primeros son coaches
SET @ROUTINES_PER_USER := 2;   -- rutinas por usuario
SET @LOGS_PER_ROUTINE := 5;    -- logs por rutina
SET @COMPLETED_PER_LOG := 10;  -- completados por log → 500k
SET @ACTIVITIES_PER_ROUTINE := 3;
SET @N_HABITS := 120;          -- hábitos
SET @N_GUIDES := 40;           -- guías
SET @FAVS_PER_USER := 3;       -- hábitos favoritos

-- ===========================
-- Limpieza inicial
-- ===========================
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

SET FOREIGN_KEY_CHECKS = 1;

-- ===========================
-- Secuencias
-- ===========================
WITH RECURSIVE
s_users AS (SELECT 1 AS seq UNION ALL SELECT seq+1 FROM s_users WHERE seq < @N_USERS),
s_habits AS (SELECT 1 AS seq UNION ALL SELECT seq+1 FROM s_habits WHERE seq < @N_HABITS),
s_guides AS (SELECT 1 AS seq UNION ALL SELECT seq+1 FROM s_guides WHERE seq < @N_GUIDES),
s2 AS (SELECT 1 AS seq UNION ALL SELECT 2),
s3 AS (SELECT 1 AS seq UNION ALL SELECT 2 UNION ALL SELECT 3),
s5 AS (SELECT 1 AS seq UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5),
s10 AS (SELECT 1 AS seq UNION ALL SELECT seq+1 FROM s10 WHERE seq < @COMPLETED_PER_LOG)
SELECT 1;

-- ===========================
-- 1) Roles
-- ===========================
INSERT INTO roles (module, permission)
SELECT module, permission
FROM (
  SELECT 'ACTIVIDADES','AUDITOR' UNION ALL SELECT 'ACTIVIDADES','EDITOR' UNION ALL
  SELECT 'RUTINAS','AUDITOR'     UNION ALL SELECT 'RUTINAS','EDITOR'   UNION ALL
  SELECT 'RECORDATORIOS','AUDITOR' UNION ALL SELECT 'RECORDATORIOS','EDITOR' UNION ALL
  SELECT 'PROGRESO','AUDITOR'    UNION ALL SELECT 'PROGRESO','EDITOR' UNION ALL
  SELECT 'GUIAS','AUDITOR'       UNION ALL SELECT 'GUIAS','EDITOR'
) r;

-- ===========================
-- 2) Users (5k usuarios, 80 coaches)
-- ===========================
WITH
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

-- Coaches
INSERT INTO users (username, email, password, coach_id)
SELECT
  LOWER(CONCAT(fn.name,'.',ln.name,LPAD(u.seq,3,'0'))) AS username,
  LOWER(CONCAT(fn.name,'.',ln.name,LPAD(u.seq,3,'0'),'@fitflow.test')) AS email,
  '$2a$12$kzhFS4BHdP3ACWv3lR2OHOVOsmVk2011LLefmm76eQ1ebJM0UNXfi', -- bcrypt fijo
  NULL
FROM s_users u
JOIN fn ON ((u.seq % fn.tot)+1)=fn.rn
JOIN ln ON ((u.seq % ln.tot)+1)=ln.rn
WHERE u.seq <= @N_COACHES;

-- Restantes
INSERT INTO users (username, email, password, coach_id)
SELECT
  LOWER(CONCAT(fn.name,'.',ln.name,LPAD(u.seq,3,'0'))) AS username,
  LOWER(CONCAT(fn.name,'.',ln.name,LPAD(u.seq,3,'0'),'@fitflow.test')) AS email,
  '$2a$12$kzhFS4BHdP3ACWv3lR2OHOVOsmVk2011LLefmm76eQ1ebJM0UNXfi',
  1+MOD(u.seq-1,@N_COACHES)
FROM s_users u
JOIN fn ON ((u.seq % fn.tot)+1)=fn.rn
JOIN ln ON ((u.seq % ln.tot)+1)=ln.rn
WHERE u.seq > @N_COACHES;

-- Roles x usuario
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
JOIN roles r ON MOD(u.id+r.id,2)=0;

-- ===========================
-- 3) Habits + user_habit
-- ===========================
WITH acts(a) AS (SELECT 'Respirar' UNION ALL SELECT 'Caminar' UNION ALL SELECT 'Meditar' UNION ALL SELECT 'Hidratar'),
adjs(a) AS (SELECT 'Vital' UNION ALL SELECT 'Ligero' UNION ALL SELECT 'Sereno' UNION ALL SELECT 'Enfocado'),
cats(c) AS (SELECT 'PHYSICAL' UNION ALL SELECT 'MENTAL' UNION ALL SELECT 'SLEEP' UNION ALL SELECT 'DIET'),
A AS (SELECT a,ROW_NUMBER() OVER() rn,COUNT(*) OVER() tot FROM acts),
B AS (SELECT a,ROW_NUMBER() OVER() rn,COUNT(*) OVER() tot FROM adjs),
C AS (SELECT c,ROW_NUMBER() OVER() rn,COUNT(*) OVER() tot FROM cats)

INSERT INTO habits (name,category,description)
SELECT CONCAT(b.a,' ',a.a,' ',LPAD(h.seq,3,'0')),
       c.c,
       CONCAT('Habito ',LOWER(b.a),' para ',LOWER(a.a))
FROM s_habits h
JOIN A a ON ((h.seq%a.tot)+1)=a.rn
JOIN B b ON ((h.seq%b.tot)+1)=b.rn
JOIN C c ON ((h.seq%c.tot)+1)=c.rn;

-- favoritos
INSERT INTO user_habit(user_id,habit_id)
SELECT u.id,h.id
FROM users u
JOIN habits h ON MOD(u.id+h.id,@FAVS_PER_USER)=0;

-- ===========================
-- 4) Guides + recommended habits
-- ===========================
INSERT INTO guides(title,content,category)
SELECT CONCAT('Guia ',g.seq),
       CONCAT('Contenido guia ',g.seq),
       ELT(1+MOD(g.seq,4),'PHYSICAL','MENTAL','SLEEP','DIET')
FROM s_guides g;

INSERT INTO guide_recommended_habit(guide_id,habit_id)
SELECT g.id,h.id
FROM guides g
JOIN habits h ON MOD(g.id+h.id,7)=0;

-- ===========================
-- 5) Routines + activities
-- ===========================
INSERT INTO routines(user_id,title,days_of_week)
SELECT u.id,
       CONCAT('Rutina ',u.id),
       x'7F'
FROM users u
JOIN s2 s ON 1;

INSERT INTO routine_activities(routine_id,habit_id,duration,notes)
SELECT r.id,h.id,
       15+MOD(r.id+h.id,30),
       CONCAT('Nota act ',r.id,'-',h.id)
FROM routines r
JOIN s3 s ON 1
JOIN habits h ON h.id<=@N_HABITS AND MOD(h.id+r.id+s.seq,@N_HABITS)=0;

-- ===========================
-- 6) Progress logs
-- ===========================
INSERT INTO progress_log(user_id,routine_id,log_date)
SELECT r.user_id,r.id,
       DATE_ADD('2024-11-01',INTERVAL (r.id*2+seq*3) DAY)
FROM routines r
JOIN s5 seq ON 1;

-- ===========================
-- 7) Completed activities (~500k)
-- ===========================
INSERT INTO completed_activities(habit_id,progress_log_id,completed_at,notes)
SELECT h.id,pl.id,
       TIMESTAMPADD(MINUTE,30*seq,pl.log_date),
       CONCAT('Completado ',pl.id,'-',h.id,'-',seq)
FROM progress_log pl
JOIN s10 seq ON 1
JOIN habits h ON MOD(h.id+pl.id+seq,17)=0;

-- ===========================
-- 8) Reminders
-- ===========================
INSERT INTO reminder(user_id,habit_id,frequency,message,time)
SELECT u.id,h.id,
       'DAILY',
       CONCAT('Recordatorio de ',h.name),
       TIMESTAMP(DATE('2024-11-05'),MAKETIME(7,30,0))
FROM users u
JOIN habits h ON h.id<=10
WHERE u.id<=500;

-- ===========================
-- Commit
-- ===========================
SET UNIQUE_CHECKS=1;
COMMIT;