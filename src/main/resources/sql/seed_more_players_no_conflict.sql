-- Additional seed data to run AFTER sample_data.sql
-- Purpose: add more players/contracts without conflicting with existing IDs.
-- Assumes these rows already exist:
--   season 1001
--   teams 3005 (Dong A Thanh Hoa), 3006 (Hoang Anh Gia Lai)

INSERT INTO players (id, full_name, date_of_birth, nationality, preferred_position) VALUES
  (4401, 'Trịnh Xuân Hoàng', '2000-01-25', 'Việt Nam', 'Thủ môn'),
  (4402, 'Nguyễn Thanh Long', '1998-09-14', 'Việt Nam', 'Trung vệ'),
  (4403, 'Lê Văn Thắng', '1997-12-03', 'Việt Nam', 'Hậu vệ phải'),
  (4404, 'Phạm Đình Tùng', '1999-05-11', 'Việt Nam', 'Hậu vệ trái'),
  (4405, 'Doãn Ngọc Tân', '1994-08-20', 'Việt Nam', 'Tiền vệ trung tâm'),
  (4406, 'Nguyễn Thái Sơn', '2003-07-10', 'Việt Nam', 'Tiền vệ trung tâm'),
  (4407, 'Lâm Ti Phông', '1996-02-12', 'Việt Nam', 'Tiền đạo cánh'),
  (4408, 'Rimario Gordon', '1994-07-06', 'Jamaica', 'Tiền đạo'),
  (4409, 'Bruno Cunha', '1997-04-28', 'Brazil', 'Tiền đạo'),
  (4410, 'Trần Quốc Phương', '2001-10-18', 'Việt Nam', 'Tiền vệ cánh'),
  (4411, 'Nguyễn Văn Minh', '2000-03-09', 'Việt Nam', 'Trung vệ'),
  (4412, 'Lê Xuân Hùng', '2002-06-27', 'Việt Nam', 'Tiền đạo'),
  (4501, 'Trần Trung Kiên', '2003-02-09', 'Việt Nam', 'Thủ môn'),
  (4502, 'Jairo Rodrigues', '1992-03-17', 'Brazil', 'Trung vệ'),
  (4503, 'Lê Văn Sơn', '1996-02-20', 'Việt Nam', 'Hậu vệ trái'),
  (4504, 'Vũ Văn Sơn', '2001-04-14', 'Việt Nam', 'Hậu vệ phải'),
  (4505, 'Châu Ngọc Quang', '1996-07-01', 'Việt Nam', 'Tiền vệ trung tâm'),
  (4506, 'Trần Minh Vương', '1995-03-28', 'Việt Nam', 'Tiền vệ tấn công'),
  (4507, 'Nguyễn Quốc Việt', '2003-05-04', 'Việt Nam', 'Tiền đạo'),
  (4508, 'Washington Brandao', '1990-08-18', 'Brazil', 'Tiền đạo'),
  (4509, 'Martin Dzilah', '1998-01-17', 'Ghana', 'Tiền đạo cánh'),
  (4510, 'Nguyễn Đức Việt', '2004-01-01', 'Việt Nam', 'Tiền vệ'),
  (4511, 'Đinh Thanh Bình', '1998-02-24', 'Việt Nam', 'Tiền đạo'),
  (4512, 'Lê Hữu Phước', '2002-09-19', 'Việt Nam', 'Trung vệ');

INSERT INTO player_contracts (
  id, player_id, team_id, season_id, jersey_number, health_status,
  yellow_cards, red_cards, contract_start, contract_end, status, is_foreign
) VALUES
  (6401, 4401, 3005, 1001, 1, 'BÌNH THƯỜNG', 0, 0, '2025-08-01', '2026-07-31', 'HOẠT ĐỘNG', false),
  (6402, 4402, 3005, 1001, 3, 'BÌNH THƯỜNG', 0, 0, '2025-08-01', '2026-07-31', 'HOẠT ĐỘNG', false),
  (6403, 4403, 3005, 1001, 2, 'BÌNH THƯỜNG', 1, 0, '2025-08-01', '2026-07-31', 'HOẠT ĐỘNG', false),
  (6404, 4404, 3005, 1001, 12, 'BÌNH THƯỜNG', 0, 0, '2025-08-01', '2026-07-31', 'HOẠT ĐỘNG', false),
  (6405, 4405, 3005, 1001, 34, 'BÌNH THƯỜNG', 1, 0, '2025-08-01', '2026-07-31', 'HOẠT ĐỘNG', false),
  (6406, 4406, 3005, 1001, 8, 'BÌNH THƯỜNG', 0, 0, '2025-08-01', '2026-07-31', 'HOẠT ĐỘNG', false),
  (6407, 4407, 3005, 1001, 17, 'BÌNH THƯỜNG', 0, 0, '2025-08-01', '2026-07-31', 'HOẠT ĐỘNG', false),
  (6408, 4408, 3005, 1001, 9, 'BÌNH THƯỜNG', 0, 0, '2025-08-01', '2026-07-31', 'HOẠT ĐỘNG', true),
  (6409, 4409, 3005, 1001, 10, 'BÌNH THƯỜNG', 0, 0, '2025-08-01', '2026-07-31', 'HOẠT ĐỘNG', true),
  (6410, 4410, 3005, 1001, 19, 'BÌNH THƯỜNG', 0, 0, '2025-08-01', '2026-07-31', 'HOẠT ĐỘNG', false),
  (6411, 4411, 3005, 1001, 15, 'BÌNH THƯỜNG', 0, 0, '2025-08-01', '2026-07-31', 'HOẠT ĐỘNG', false),
  (6412, 4412, 3005, 1001, 11, 'BÌNH THƯỜNG', 0, 0, '2025-08-01', '2026-07-31', 'HOẠT ĐỘNG', false),
  (6501, 4501, 3006, 1001, 1, 'BÌNH THƯỜNG', 0, 0, '2025-08-01', '2026-07-31', 'HOẠT ĐỘNG', false),
  (6502, 4502, 3006, 1001, 3, 'BÌNH THƯỜNG', 0, 0, '2025-08-01', '2026-07-31', 'HOẠT ĐỘNG', true),
  (6503, 4503, 3006, 1001, 7, 'BÌNH THƯỜNG', 0, 0, '2025-08-01', '2026-07-31', 'HOẠT ĐỘNG', false),
  (6504, 4504, 3006, 1001, 2, 'BÌNH THƯỜNG', 0, 0, '2025-08-01', '2026-07-31', 'HOẠT ĐỘNG', false),
  (6505, 4505, 3006, 1001, 8, 'BÌNH THƯỜNG', 1, 0, '2025-08-01', '2026-07-31', 'HOẠT ĐỘNG', false),
  (6506, 4506, 3006, 1001, 10, 'BÌNH THƯỜNG', 0, 0, '2025-08-01', '2026-07-31', 'HOẠT ĐỘNG', false),
  (6507, 4507, 3006, 1001, 9, 'BÌNH THƯỜNG', 0, 0, '2025-08-01', '2026-07-31', 'HOẠT ĐỘNG', false),
  (6508, 4508, 3006, 1001, 11, 'BÌNH THƯỜNG', 0, 0, '2025-08-01', '2026-07-31', 'HOẠT ĐỘNG', true),
  (6509, 4509, 3006, 1001, 22, 'BÌNH THƯỜNG', 0, 0, '2025-08-01', '2026-07-31', 'HOẠT ĐỘNG', true),
  (6510, 4510, 3006, 1001, 86, 'BÌNH THƯỜNG', 0, 0, '2025-08-01', '2026-07-31', 'HOẠT ĐỘNG', false),
  (6511, 4511, 3006, 1001, 19, 'BÌNH THƯỜNG', 0, 0, '2025-08-01', '2026-07-31', 'HOẠT ĐỘNG', false),
  (6512, 4512, 3006, 1001, 4, 'BÌNH THƯỜNG', 0, 0, '2025-08-01', '2026-07-31', 'HOẠT ĐỘNG', false);

-- Optional verification
SELECT team_id, COUNT(*) AS contract_count
FROM player_contracts
WHERE id BETWEEN 6401 AND 6512
GROUP BY team_id
ORDER BY team_id;
