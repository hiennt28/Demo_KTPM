-- Migrate legacy awards.season (text) to awards.season_id (FK to seasons.id)
-- Target DB: MySQL
--
-- Suggested order:
-- 1. Start the app once so Hibernate creates the new awards.season_id column.
-- 2. Run the UPDATE below to copy old text values into season_id.
-- 3. Verify the SELECT returns no unresolved rows.
-- 4. Optionally drop the old season column after backup/verification.

START TRANSACTION;

-- Copy old season text into the new foreign key column.
UPDATE awards a
JOIN seasons s
    ON TRIM(a.season) = TRIM(s.season_name)
SET a.season_id = s.id
WHERE a.season IS NOT NULL
  AND a.season <> ''
  AND a.season_id IS NULL;

-- Check rows that still could not be mapped.
SELECT a.id, a.award_name, a.season
FROM awards a
WHERE a.season IS NOT NULL
  AND a.season <> ''
  AND a.season_id IS NULL;

COMMIT;

-- Optional cleanup after verification:
-- ALTER TABLE awards DROP COLUMN season;

