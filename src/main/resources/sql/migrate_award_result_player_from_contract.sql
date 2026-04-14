-- Migrate award_results from player_contract_id -> player_id
-- Run this after the player_id column has been added to award_results.

UPDATE award_results ar
JOIN player_contracts pc ON ar.player_contract_id = pc.id
SET ar.player_id = pc.player_id
WHERE ar.player_contract_id IS NOT NULL
  AND ar.player_id IS NULL;
