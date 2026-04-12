-- Sample data for vdqg_db
-- Run on a database where the tables already exist.
-- If your DB already has data, adjust IDs or clear the tables first.

INSERT INTO seasons (id, season_name, status) VALUES
  (1001, '2025-2026', 'ĐANG DIỄN RA'),
  (1002, '2024-2025', 'KẾT THÚC');

INSERT INTO rounds (id, season_id, round_name, round_order) VALUES
  (2001, 1001, 'Vòng 1', 1),
  (2002, 1001, 'Vòng 2', 2);

INSERT INTO teams (id, team_name, stadium, coach, logo_url) VALUES
  (3001, 'Hà Nội FC', 'Hàng Đẫy', 'Nguyễn Đức Thắng', '/images/hanoi-fc.png'),
  (3002, 'Công An Hà Nội', 'Hàng Đẫy', 'Alexandre Polking', '/images/cahn.png'),
  (3003, 'Hoàng Anh Gia Lai', 'Pleiku', 'Vũ Tiến Thành', '/images/hagl.png'),
  (3004, 'Becamex Bình Dương', 'Gò Đậu', 'Lê Huỳnh Đức', '/images/becamex.png');

INSERT INTO players (id, full_name, date_of_birth, nationality, position) VALUES
  (4001, 'Nguyễn Văn A', '1998-01-10', 'Việt Nam', 'Thủ môn'),
  (4002, 'Trần Văn B', '1999-03-12', 'Việt Nam', 'Hậu vệ'),
  (4003, 'Lê Văn C', '2000-05-20', 'Việt Nam', 'Hậu vệ'),
  (4004, 'Phạm Văn D', '1997-07-11', 'Việt Nam', 'Hậu vệ'),
  (4005, 'Hoàng Văn E', '1998-09-09', 'Việt Nam', 'Tiền vệ'),
  (4006, 'Đỗ Văn F', '1999-12-01', 'Việt Nam', 'Tiền vệ'),
  (4007, 'Bùi Văn G', '2001-02-14', 'Việt Nam', 'Tiền vệ'),
  (4008, 'Jose Silva', '1996-04-18', 'Brazil', 'Tiền đạo'),
  (4009, 'Nguyễn Văn H', '1998-06-06', 'Việt Nam', 'Tiền đạo'),
  (4010, 'Kim Min Jae', '1997-08-22', 'Hàn Quốc', 'Tiền vệ'),
  (4011, 'Vũ Văn I', '2000-10-02', 'Việt Nam', 'Hậu vệ'),
  (4012, 'Trịnh Văn K', '2002-11-19', 'Việt Nam', 'Tiền đạo'),
  (4013, 'Dương Văn L', '2001-01-25', 'Việt Nam', 'Tiền vệ'),
  (4101, 'Phan Văn M', '1998-02-08', 'Việt Nam', 'Thủ môn'),
  (4102, 'Ngô Văn N', '1999-04-16', 'Việt Nam', 'Hậu vệ'),
  (4103, 'Cao Văn P', '1997-06-30', 'Việt Nam', 'Hậu vệ'),
  (4104, 'Mai Văn Q', '2000-08-13', 'Việt Nam', 'Hậu vệ'),
  (4105, 'Lý Văn R', '1998-10-27', 'Việt Nam', 'Tiền vệ'),
  (4106, 'Trương Văn S', '1999-12-12', 'Việt Nam', 'Tiền vệ'),
  (4107, 'Đặng Văn T', '2001-03-09', 'Việt Nam', 'Tiền vệ'),
  (4108, 'Nguyễn Văn U', '1997-05-21', 'Việt Nam', 'Tiền đạo'),
  (4109, 'Ryu Takahashi', '1996-07-17', 'Nhật Bản', 'Tiền đạo'),
  (4110, 'Đinh Văn V', '1998-09-29', 'Việt Nam', 'Tiền vệ'),
  (4111, 'La Văn X', '2000-11-15', 'Việt Nam', 'Hậu vệ'),
  (4112, 'Đỗ Văn Y', '2001-01-05', 'Việt Nam', 'Tiền đạo'),
  (4113, 'Phạm Văn Z', '2002-04-01', 'Việt Nam', 'Tiền vệ');

INSERT INTO matches (id, round_id, match_date, stadium, status) VALUES
  (5001, 2001, '2026-04-20 19:15:00', 'Hàng Đẫy', 'CHƯA DIỄN RA'),
  (5002, 2002, '2026-04-27 18:00:00', 'Hàng Đẫy', 'CHƯA DIỄN RA');

INSERT INTO match_details (id, match_id, team_id, role, score, result) VALUES
  (7001, 5001, 3001, 'NHÀ', 0, NULL),
  (7002, 5001, 3002, 'KHÁCH', 0, NULL),
  (7003, 5002, 3001, 'NHÀ', 0, NULL),
  (7004, 5002, 3003, 'KHÁCH', 0, NULL);

INSERT INTO player_contracts (
  id, player_id, team_id, season_id, jersey_number, health_status,
  yellow_cards, red_cards, contract_start, contract_end, status
) VALUES
  (6001, 4001, 3001, 1001, 1,  'BÌNH THƯỜNG', 0, 0, '2025-08-01', '2026-07-31', 'HOẠT ĐỘNG'),
  (6002, 4002, 3001, 1001, 2,  'BÌNH THƯỜNG', 0, 0, '2025-08-01', '2026-07-31', 'HOẠT ĐỘNG'),
  (6003, 4003, 3001, 1001, 3,  'BÌNH THƯỜNG', 1, 0, '2025-08-01', '2026-07-31', 'HOẠT ĐỘNG'),
  (6004, 4004, 3001, 1001, 4,  'BÌNH THƯỜNG', 0, 0, '2025-08-01', '2026-07-31', 'HOẠT ĐỘNG'),
  (6005, 4005, 3001, 1001, 6,  'BÌNH THƯỜNG', 0, 0, '2025-08-01', '2026-07-31', 'HOẠT ĐỘNG'),
  (6006, 4006, 3001, 1001, 8,  'BÌNH THƯỜNG', 0, 0, '2025-08-01', '2026-07-31', 'HOẠT ĐỘNG'),
  (6007, 4007, 3001, 1001, 10, 'BÌNH THƯỜNG', 0, 0, '2025-08-01', '2026-07-31', 'HOẠT ĐỘNG'),
  (6008, 4008, 3001, 1001, 9,  'BÌNH THƯỜNG', 0, 0, '2025-08-01', '2026-07-31', 'HOẠT ĐỘNG'),
  (6009, 4009, 3001, 1001, 11, 'BÌNH THƯỜNG', 0, 0, '2025-08-01', '2026-07-31', 'HOẠT ĐỘNG'),
  (6010, 4010, 3001, 1001, 7,  'BÌNH THƯỜNG', 0, 0, '2025-08-01', '2026-07-31', 'HOẠT ĐỘNG'),
  (6011, 4011, 3001, 1001, 14, 'BÌNH THƯỜNG', 0, 0, '2025-08-01', '2026-07-31', 'HOẠT ĐỘNG'),
  (6012, 4012, 3001, 1001, 17, 'CHẤN THƯƠNG', 0, 0, '2025-08-01', '2026-07-31', 'HOẠT ĐỘNG'),
  (6013, 4013, 3001, 1001, 18, 'BÌNH THƯỜNG', 2, 0, '2025-08-01', '2026-07-31', 'HOẠT ĐỘNG'),
  (6101, 4101, 3002, 1001, 1,  'BÌNH THƯỜNG', 0, 0, '2025-08-01', '2026-07-31', 'HOẠT ĐỘNG'),
  (6102, 4102, 3002, 1001, 2,  'BÌNH THƯỜNG', 0, 0, '2025-08-01', '2026-07-31', 'HOẠT ĐỘNG'),
  (6103, 4103, 3002, 1001, 3,  'BÌNH THƯỜNG', 0, 0, '2025-08-01', '2026-07-31', 'HOẠT ĐỘNG'),
  (6104, 4104, 3002, 1001, 4,  'BÌNH THƯỜNG', 0, 0, '2025-08-01', '2026-07-31', 'HOẠT ĐỘNG'),
  (6105, 4105, 3002, 1001, 6,  'BÌNH THƯỜNG', 1, 0, '2025-08-01', '2026-07-31', 'HOẠT ĐỘNG'),
  (6106, 4106, 3002, 1001, 8,  'BÌNH THƯỜNG', 0, 0, '2025-08-01', '2026-07-31', 'HOẠT ĐỘNG'),
  (6107, 4107, 3002, 1001, 10, 'BÌNH THƯỜNG', 0, 0, '2025-08-01', '2026-07-31', 'HOẠT ĐỘNG'),
  (6108, 4108, 3002, 1001, 9,  'BÌNH THƯỜNG', 0, 0, '2025-08-01', '2026-07-31', 'HOẠT ĐỘNG'),
  (6109, 4109, 3002, 1001, 11, 'BÌNH THƯỜNG', 0, 0, '2025-08-01', '2026-07-31', 'HOẠT ĐỘNG'),
  (6110, 4110, 3002, 1001, 7,  'BÌNH THƯỜNG', 0, 0, '2025-08-01', '2026-07-31', 'HOẠT ĐỘNG'),
  (6111, 4111, 3002, 1001, 14, 'BÌNH THƯỜNG', 0, 0, '2025-08-01', '2026-07-31', 'HOẠT ĐỘNG'),
  (6112, 4112, 3002, 1001, 17, 'CHẤN THƯƠNG', 0, 0, '2025-08-01', '2026-07-31', 'HOẠT ĐỘNG'),
  (6113, 4113, 3002, 1001, 18, 'BÌNH THƯỜNG', 0, 0, '2025-08-01', '2026-07-31', 'HOẠT ĐỘNG');

INSERT INTO awards (
  id, award_name, award_type, scope, season_id, match_id,
  prize_amount, conditions, status, created_at, updated_at
) VALUES
  (
    8001, 'Vô địch mùa giải', 'TẬP THỂ', 'MÙA GIẢI', 1001, NULL,
    5000000000, 'Trao cho đội có thành tích cao nhất khi mùa giải kết thúc.',
    'ĐANG ÁP DỤNG', '2026-04-01 09:00:00', '2026-04-01 09:00:00'
  ),
  (
    8002, 'Cầu thủ xuất sắc nhất mùa', 'CÁ NHÂN', 'MÙA GIẢI', 1001, NULL,
    300000000, 'Trao cho cầu thủ có đóng góp nổi bật nhất trong mùa giải.',
    'ĐANG ÁP DỤNG', '2026-04-01 09:30:00', '2026-04-01 09:30:00'
  );

INSERT INTO award_results (
  id, award_id, team_id, player_contract_id,
  bank_account, bank_name, representative, payment_status
) VALUES
  (
    9001, 8001, 3001, NULL,
    '1900368686888', 'Techcombank', 'Nguyễn Minh Quân', 'CHƯA THANH TOÁN'
  ),
  (
    9002, 8002, NULL, 6008,
    '0123456789', 'Vietcombank', 'Jose Silva', 'ĐÃ THANH TOÁN'
  );

INSERT INTO award_payments (
  id, award_result_id, payment_method, amount, paid_at, note
) VALUES
  (
    9501, 9002, 'CHUYỂN KHOẢN', 300000000,
    '2026-04-05 10:15:00', 'Đã chuyển khoản thành công cho cầu thủ.'
  );

INSERT INTO match_registrations (
  id, match_detail_id, starting_player_ids, substitute_player_ids, registered_at
) VALUES
  (
    9801, 7003,
    '6001,6002,6003,6004,6005,6006,6007,6008,6009,6010,6011',
    '6012,6013',
    '2026-04-15 14:00:00'
  );
