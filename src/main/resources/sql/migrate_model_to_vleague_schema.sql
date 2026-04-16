-- Align legacy schema/data to the current unidirectional V.League model
-- Target DB: MySQL 8+
--
-- Suggested order:
-- 1. Back up the database.
-- 2. Start the app once so Hibernate creates any new tables/columns it can.
-- 3. Run this script manually.
-- 4. Verify the SELECT checks near the bottom.

START TRANSACTION;

-- teams: stadium -> home_stadium, add short_name/status, drop coach
SET @sql = (
  SELECT IF(
    EXISTS (
      SELECT 1
      FROM information_schema.COLUMNS
      WHERE TABLE_SCHEMA = DATABASE()
        AND TABLE_NAME = 'teams'
        AND COLUMN_NAME = 'stadium'
    ),
    'ALTER TABLE teams CHANGE COLUMN stadium home_stadium VARCHAR(255)',
    'SELECT 1'
  )
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (
  SELECT IF(
    EXISTS (
      SELECT 1
      FROM information_schema.COLUMNS
      WHERE TABLE_SCHEMA = DATABASE()
        AND TABLE_NAME = 'teams'
        AND COLUMN_NAME = 'short_name'
    ),
    'SELECT 1',
    'ALTER TABLE teams ADD COLUMN short_name VARCHAR(50) NULL AFTER team_name'
  )
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (
  SELECT IF(
    EXISTS (
      SELECT 1
      FROM information_schema.COLUMNS
      WHERE TABLE_SCHEMA = DATABASE()
        AND TABLE_NAME = 'teams'
        AND COLUMN_NAME = 'status'
    ),
    'SELECT 1',
    'ALTER TABLE teams ADD COLUMN status VARCHAR(50) NULL AFTER home_stadium'
  )
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

UPDATE teams
SET status = COALESCE(NULLIF(status, ''), 'HOẠT ĐỘNG');

UPDATE teams
SET short_name = CASE
  WHEN short_name IS NOT NULL AND short_name <> '' THEN short_name
  WHEN team_name = 'Hà Nội FC' THEN 'HNFC'
  WHEN team_name = 'Công An Hà Nội' THEN 'CAHN'
  WHEN team_name = 'Thép Xanh Nam Định' THEN 'TXNĐ'
  WHEN team_name = 'Thể Công Viettel' THEN 'TCVT'
  WHEN team_name = 'Hoàng Anh Gia Lai' THEN 'HAGL'
  WHEN team_name = 'Becamex Bình Dương' THEN 'BBD'
  WHEN team_name = 'Đông Á Thanh Hóa' THEN 'ĐATH'
  WHEN team_name = 'Hải Phòng FC' THEN 'HPFC'
  ELSE UPPER(LEFT(REPLACE(REPLACE(team_name, ' ', ''), '.', ''), 4))
END;

SET @sql = (
  SELECT IF(
    EXISTS (
      SELECT 1
      FROM information_schema.COLUMNS
      WHERE TABLE_SCHEMA = DATABASE()
        AND TABLE_NAME = 'teams'
        AND COLUMN_NAME = 'coach'
    ),
    'ALTER TABLE teams DROP COLUMN coach',
    'SELECT 1'
  )
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- players: position -> preferred_position
SET @sql = (
  SELECT IF(
    EXISTS (
      SELECT 1
      FROM information_schema.COLUMNS
      WHERE TABLE_SCHEMA = DATABASE()
        AND TABLE_NAME = 'players'
        AND COLUMN_NAME = 'position'
    ),
    'ALTER TABLE players CHANGE COLUMN position preferred_position VARCHAR(255)',
    'SELECT 1'
  )
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- player_contracts: add is_foreign and backfill from nationality
SET @sql = (
  SELECT IF(
    EXISTS (
      SELECT 1
      FROM information_schema.COLUMNS
      WHERE TABLE_SCHEMA = DATABASE()
        AND TABLE_NAME = 'player_contracts'
        AND COLUMN_NAME = 'is_foreign'
    ),
    'SELECT 1',
    'ALTER TABLE player_contracts ADD COLUMN is_foreign BIT NULL AFTER status'
  )
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

UPDATE player_contracts pc
JOIN players p ON p.id = pc.player_id
SET pc.is_foreign = CASE
  WHEN p.nationality IS NOT NULL AND TRIM(p.nationality) <> 'Việt Nam' THEN b'1'
  ELSE b'0'
END
WHERE pc.is_foreign IS NULL;

-- registration_details: registration_type -> role, add playing_position
SET @sql = (
  SELECT IF(
    EXISTS (
      SELECT 1
      FROM information_schema.COLUMNS
      WHERE TABLE_SCHEMA = DATABASE()
        AND TABLE_NAME = 'registration_details'
        AND COLUMN_NAME = 'registration_type'
    ),
    'ALTER TABLE registration_details CHANGE COLUMN registration_type role VARCHAR(30)',
    'SELECT 1'
  )
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (
  SELECT IF(
    EXISTS (
      SELECT 1
      FROM information_schema.COLUMNS
      WHERE TABLE_SCHEMA = DATABASE()
        AND TABLE_NAME = 'registration_details'
        AND COLUMN_NAME = 'playing_position'
    ),
    'SELECT 1',
    'ALTER TABLE registration_details ADD COLUMN playing_position VARCHAR(255) NULL AFTER role'
  )
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

UPDATE registration_details
SET role = CASE
  WHEN role = 'STARTING' THEN 'ĐÁ CHÍNH'
  WHEN role = 'SUBSTITUTE' THEN 'DỰ BỊ'
  ELSE role
END;

UPDATE registration_details rd
JOIN player_contracts pc ON pc.id = rd.player_contract_id
JOIN players p ON p.id = pc.player_id
SET rd.playing_position = p.preferred_position
WHERE rd.playing_position IS NULL;

-- award_payments: add transaction_status
SET @sql = (
  SELECT IF(
    EXISTS (
      SELECT 1
      FROM information_schema.COLUMNS
      WHERE TABLE_SCHEMA = DATABASE()
        AND TABLE_NAME = 'award_payments'
        AND COLUMN_NAME = 'transaction_status'
    ),
    'SELECT 1',
    'ALTER TABLE award_payments ADD COLUMN transaction_status VARCHAR(50) NULL AFTER note'
  )
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

UPDATE award_payments
SET transaction_status = COALESCE(NULLIF(transaction_status, ''), 'THÀNH CÔNG');

-- match_registrations: drop old denormalized columns if they still exist
SET @sql = (
  SELECT IF(
    EXISTS (
      SELECT 1
      FROM information_schema.COLUMNS
      WHERE TABLE_SCHEMA = DATABASE()
        AND TABLE_NAME = 'match_registrations'
        AND COLUMN_NAME = 'starting_player_ids'
    ),
    'ALTER TABLE match_registrations DROP COLUMN starting_player_ids',
    'SELECT 1'
  )
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (
  SELECT IF(
    EXISTS (
      SELECT 1
      FROM information_schema.COLUMNS
      WHERE TABLE_SCHEMA = DATABASE()
        AND TABLE_NAME = 'match_registrations'
        AND COLUMN_NAME = 'substitute_player_ids'
    ),
    'ALTER TABLE match_registrations DROP COLUMN substitute_player_ids',
    'SELECT 1'
  )
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

COMMIT;

-- Verification helpers
SELECT id, team_name, short_name, home_stadium, status
FROM teams
ORDER BY id;

SELECT id, full_name, preferred_position
FROM players
ORDER BY id
LIMIT 20;

SELECT id, player_id, team_id, season_id, is_foreign
FROM player_contracts
ORDER BY id
LIMIT 20;

SELECT id, role, playing_position, player_contract_id
FROM registration_details
ORDER BY id
LIMIT 20;

SELECT id, payment_method, transaction_status
FROM award_payments
ORDER BY id;
