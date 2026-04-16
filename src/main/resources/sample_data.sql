-- Sample data for vdqg_db
-- V.League-style demo data for the current schema.
-- Clear existing data or adjust IDs before running.

INSERT INTO seasons (id, season_name, status) VALUES
  (1001, '2025-2026', 'ĐANG DIỄN RA'),
  (1002, '2024-2025', 'KẾT THÚC');

INSERT INTO rounds (id, season_id, round_name, round_order) VALUES
  (2001, 1001, 'Vòng 1', 1),
  (2002, 1001, 'Vòng 2', 2),
  (2003, 1001, 'Vòng 3', 3);

INSERT INTO teams (id, team_name, short_name, logo_url, home_stadium, status) VALUES
  (3001, 'Hà Nội FC', 'HNFC', '/images/hanoi-fc.png', 'Hàng Đẫy', 'HOẠT ĐỘNG'),
  (3002, 'Công An Hà Nội', 'CAHN', '/images/cahn.png', 'Hàng Đẫy', 'HOẠT ĐỘNG'),
  (3003, 'Thép Xanh Nam Định', 'TXND', '/images/nam-dinh.png', 'Thiên Trường', 'HOẠT ĐỘNG'),
  (3004, 'Thể Công Viettel', 'TCVT', '/images/viettel.png', 'Mỹ Đình', 'HOẠT ĐỘNG'),
  (3005, 'Đông Á Thanh Hóa', 'DATH', '/images/thanh-hoa.png', 'Thanh Hóa', 'HOẠT ĐỘNG'),
  (3006, 'Hoàng Anh Gia Lai', 'HAGL', '/images/hagl.png', 'Pleiku', 'HOẠT ĐỘNG');

INSERT INTO players (id, full_name, date_of_birth, nationality, preferred_position) VALUES
  (4001, 'Bùi Tấn Trường', '1986-02-19', 'Việt Nam', 'Thủ môn'),
  (4002, 'Đỗ Duy Mạnh', '1996-09-29', 'Việt Nam', 'Trung vệ'),
  (4003, 'Nguyễn Thành Chung', '1997-09-08', 'Việt Nam', 'Trung vệ'),
  (4004, 'Vũ Văn Thanh', '1996-04-14', 'Việt Nam', 'Hậu vệ phải'),
  (4005, 'Đoàn Văn Hậu', '1999-04-19', 'Việt Nam', 'Hậu vệ trái'),
  (4006, 'Nguyễn Quang Hải', '1997-04-12', 'Việt Nam', 'Tiền vệ tấn công'),
  (4007, 'Đỗ Hùng Dũng', '1993-09-08', 'Việt Nam', 'Tiền vệ trung tâm'),
  (4008, 'Nguyễn Văn Quyết', '1991-06-27', 'Việt Nam', 'Tiền đạo'),
  (4009, 'Lucao', '1991-09-10', 'Brazil', 'Tiền đạo'),
  (4010, 'Jeferson Elias', '1995-07-01', 'Brazil', 'Tiền đạo'),
  (4011, 'Nguyễn Hai Long', '2000-08-27', 'Việt Nam', 'Tiền vệ'),
  (4012, 'Phạm Tuấn Hải', '1998-05-19', 'Việt Nam', 'Tiền đạo'),
  (4101, 'Filip Nguyễn', '1992-09-14', 'Cộng hòa Séc', 'Thủ môn'),
  (4102, 'Bùi Hoàng Việt Anh', '1999-01-01', 'Việt Nam', 'Trung vệ'),
  (4103, 'Hồ Tấn Tài', '1997-11-06', 'Việt Nam', 'Hậu vệ phải'),
  (4104, 'Vũ Văn Thanh CAHN', '1996-04-14', 'Việt Nam', 'Hậu vệ phải'),
  (4105, 'Nguyễn Hoàng Đức', '1998-01-11', 'Việt Nam', 'Tiền vệ trung tâm'),
  (4106, 'Nguyễn Quang Hải CAHN', '1997-04-12', 'Việt Nam', 'Tiền vệ tấn công'),
  (4107, 'Leo Artur', '1995-03-26', 'Brazil', 'Tiền đạo cánh'),
  (4108, 'Jhon Cley', '1994-05-10', 'Brazil', 'Tiền đạo'),
  (4109, 'Nguyễn Đình Bắc', '2004-08-15', 'Việt Nam', 'Tiền đạo'),
  (4110, 'Phan Văn Đức', '1996-04-11', 'Việt Nam', 'Tiền đạo cánh'),
  (4111, 'Lê Phạm Thành Long', '1996-06-05', 'Việt Nam', 'Tiền vệ'),
  (4201, 'Trần Nguyên Mạnh', '1991-02-20', 'Việt Nam', 'Thủ môn'),
  (4202, 'Nguyễn Hữu Tuấn', '1992-03-10', 'Việt Nam', 'Trung vệ'),
  (4203, 'Tô Văn Vũ', '1993-10-20', 'Việt Nam', 'Tiền vệ cánh'),
  (4204, 'Rafaelson', '1997-03-30', 'Brazil', 'Tiền đạo'),
  (4205, 'Nguyễn Tuấn Anh', '1995-05-16', 'Việt Nam', 'Tiền vệ trung tâm'),
  (4206, 'Lý Công Hoàng Anh', '1999-02-01', 'Việt Nam', 'Tiền vệ'),
  (4207, 'Hendrio', '1994-05-16', 'Brazil', 'Tiền vệ tấn công'),
  (4208, 'Trần Văn Kiên', '1996-09-08', 'Việt Nam', 'Hậu vệ phải'),
  (4209, 'Đinh Xuân Tiến', '2003-01-13', 'Việt Nam', 'Tiền vệ'),
  (4210, 'Mai Xuân Quyết', '1999-07-15', 'Việt Nam', 'Tiền đạo'),
  (4301, 'Nguyễn Thanh Bình', '2000-11-02', 'Việt Nam', 'Trung vệ'),
  (4302, 'Nguyễn Đức Chiến', '1998-07-06', 'Việt Nam', 'Tiền vệ phòng ngự'),
  (4303, 'Khuất Văn Khang', '2003-05-11', 'Việt Nam', 'Tiền vệ'),
  (4304, 'Bruno Cantanhede', '1993-03-20', 'Brazil', 'Tiền đạo'),
  (4305, 'Pedro Henrique', '1992-12-18', 'Brazil', 'Tiền đạo cánh'),
  (4306, 'Hoàng Đức Viettel', '1998-01-11', 'Việt Nam', 'Tiền vệ trung tâm'),
  (4307, 'Phan Tuấn Tài', '2001-01-07', 'Việt Nam', 'Hậu vệ trái'),
  (4308, 'Bùi Tiến Dũng', '1995-10-02', 'Việt Nam', 'Trung vệ');

INSERT INTO matches (id, round_id, match_date, stadium, status) VALUES
  (5001, 2001, '2026-08-15 19:15:00', 'Hàng Đẫy', 'CHƯA DIỄN RA'),
  (5002, 2002, '2026-08-22 18:00:00', 'Thiên Trường', 'CHƯA DIỄN RA'),
  (5003, 2003, '2026-08-29 19:15:00', 'Mỹ Đình', 'CHƯA DIỄN RA');

INSERT INTO match_details (id, match_id, team_id, role, score, result) VALUES
  (7001, 5001, 3001, 'NHÀ', 0, NULL),
  (7002, 5001, 3002, 'KHÁCH', 0, NULL),
  (7003, 5002, 3003, 'NHÀ', 0, NULL),
  (7004, 5002, 3005, 'KHÁCH', 0, NULL),
  (7005, 5003, 3004, 'NHÀ', 0, NULL),
  (7006, 5003, 3006, 'KHÁCH', 0, NULL);

INSERT INTO player_contracts (
  id, player_id, team_id, season_id, jersey_number, health_status,
  yellow_cards, red_cards, contract_start, contract_end, status, is_foreign
) VALUES
  (6001, 4001, 3001, 1001, 1, 'BÌNH THƯỜNG', 0, 0, '2025-08-01', '2026-07-31', 'HOẠT ĐỘNG', false),
  (6002, 4002, 3001, 1001, 2, 'BÌNH THƯỜNG', 0, 0, '2025-08-01', '2026-07-31', 'HOẠT ĐỘNG', false),
  (6003, 4003, 3001, 1001, 16, 'BÌNH THƯỜNG', 1, 0, '2025-08-01', '2026-07-31', 'HOẠT ĐỘNG', false),
  (6004, 4004, 3001, 1001, 17, 'BÌNH THƯỜNG', 0, 0, '2025-08-01', '2026-07-31', 'HOẠT ĐỘNG', false),
  (6005, 4005, 3001, 1001, 5, 'BÌNH THƯỜNG', 0, 0, '2025-08-01', '2026-07-31', 'HOẠT ĐỘNG', false),
  (6006, 4006, 3001, 1001, 19, 'BÌNH THƯỜNG', 0, 0, '2025-08-01', '2026-07-31', 'HOẠT ĐỘNG', false),
  (6007, 4007, 3001, 1001, 88, 'BÌNH THƯỜNG', 0, 0, '2025-08-01', '2026-07-31', 'HOẠT ĐỘNG', false),
  (6008, 4008, 3001, 1001, 10, 'BÌNH THƯỜNG', 0, 0, '2025-08-01', '2026-07-31', 'HOẠT ĐỘNG', false),
  (6009, 4009, 3001, 1001, 9, 'BÌNH THƯỜNG', 0, 0, '2025-08-01', '2026-07-31', 'HOẠT ĐỘNG', true),
  (6010, 4010, 3001, 1001, 77, 'BÌNH THƯỜNG', 0, 0, '2025-08-01', '2026-07-31', 'HOẠT ĐỘNG', true),
  (6011, 4011, 3001, 1001, 14, 'BÌNH THƯỜNG', 0, 0, '2025-08-01', '2026-07-31', 'HOẠT ĐỘNG', false),
  (6012, 4012, 3001, 1001, 11, 'BÌNH THƯỜNG', 0, 0, '2025-08-01', '2026-07-31', 'HOẠT ĐỘNG', false),
  (6101, 4101, 3002, 1001, 1, 'BÌNH THƯỜNG', 0, 0, '2025-08-01', '2026-07-31', 'HOẠT ĐỘNG', true),
  (6102, 4102, 3002, 1001, 68, 'BÌNH THƯỜNG', 0, 0, '2025-08-01', '2026-07-31', 'HOẠT ĐỘNG', false),
  (6103, 4103, 3002, 1001, 13, 'BÌNH THƯỜNG', 0, 0, '2025-08-01', '2026-07-31', 'HOẠT ĐỘNG', false),
  (6104, 4104, 3002, 1001, 17, 'BÌNH THƯỜNG', 0, 0, '2025-08-01', '2026-07-31', 'HOẠT ĐỘNG', false),
  (6105, 4105, 3002, 1001, 8, 'BÌNH THƯỜNG', 0, 0, '2025-08-01', '2026-07-31', 'HOẠT ĐỘNG', false),
  (6106, 4106, 3002, 1001, 19, 'BÌNH THƯỜNG', 0, 0, '2025-08-01', '2026-07-31', 'HOẠT ĐỘNG', false),
  (6107, 4107, 3002, 1001, 7, 'BÌNH THƯỜNG', 0, 0, '2025-08-01', '2026-07-31', 'HOẠT ĐỘNG', true),
  (6108, 4108, 3002, 1001, 9, 'BÌNH THƯỜNG', 0, 0, '2025-08-01', '2026-07-31', 'HOẠT ĐỘNG', true),
  (6109, 4109, 3002, 1001, 28, 'BÌNH THƯỜNG', 0, 0, '2025-08-01', '2026-07-31', 'HOẠT ĐỘNG', false),
  (6110, 4110, 3002, 1001, 20, 'BÌNH THƯỜNG', 1, 0, '2025-08-01', '2026-07-31', 'HOẠT ĐỘNG', false),
  (6111, 4111, 3002, 1001, 11, 'BÌNH THƯỜNG', 0, 0, '2025-08-01', '2026-07-31', 'HOẠT ĐỘNG', false),
  (6201, 4201, 3003, 1001, 1, 'BÌNH THƯỜNG', 0, 0, '2025-08-01', '2026-07-31', 'HOẠT ĐỘNG', false),
  (6202, 4202, 3003, 1001, 4, 'BÌNH THƯỜNG', 0, 0, '2025-08-01', '2026-07-31', 'HOẠT ĐỘNG', false),
  (6203, 4203, 3003, 1001, 10, 'BÌNH THƯỜNG', 0, 0, '2025-08-01', '2026-07-31', 'HOẠT ĐỘNG', false),
  (6204, 4204, 3003, 1001, 9, 'BÌNH THƯỜNG', 0, 0, '2025-08-01', '2026-07-31', 'HOẠT ĐỘNG', true),
  (6205, 4205, 3003, 1001, 11, 'BÌNH THƯỜNG', 0, 0, '2025-08-01', '2026-07-31', 'HOẠT ĐỘNG', false),
  (6206, 4206, 3003, 1001, 16, 'BÌNH THƯỜNG', 0, 0, '2025-08-01', '2026-07-31', 'HOẠT ĐỘNG', false),
  (6207, 4207, 3003, 1001, 18, 'BÌNH THƯỜNG', 0, 0, '2025-08-01', '2026-07-31', 'HOẠT ĐỘNG', true),
  (6208, 4208, 3003, 1001, 7, 'BÌNH THƯỜNG', 0, 0, '2025-08-01', '2026-07-31', 'HOẠT ĐỘNG', false),
  (6209, 4209, 3003, 1001, 14, 'CHẤN THƯƠNG', 0, 0, '2025-08-01', '2026-07-31', 'HOẠT ĐỘNG', false),
  (6210, 4210, 3003, 1001, 17, 'BÌNH THƯỜNG', 2, 0, '2025-08-01', '2026-07-31', 'HOẠT ĐỘNG', false),
  (6301, 4301, 3004, 1001, 3, 'BÌNH THƯỜNG', 0, 0, '2025-08-01', '2026-07-31', 'HOẠT ĐỘNG', false),
  (6302, 4302, 3004, 1001, 28, 'BÌNH THƯỜNG', 0, 0, '2025-08-01', '2026-07-31', 'HOẠT ĐỘNG', false),
  (6303, 4303, 3004, 1001, 11, 'BÌNH THƯỜNG', 0, 0, '2025-08-01', '2026-07-31', 'HOẠT ĐỘNG', false),
  (6304, 4304, 3004, 1001, 9, 'BÌNH THƯỜNG', 0, 0, '2025-08-01', '2026-07-31', 'HOẠT ĐỘNG', true),
  (6305, 4305, 3004, 1001, 10, 'BÌNH THƯỜNG', 0, 0, '2025-08-01', '2026-07-31', 'HOẠT ĐỘNG', true),
  (6306, 4306, 3004, 1001, 8, 'BÌNH THƯỜNG', 1, 0, '2025-08-01', '2026-07-31', 'HOẠT ĐỘNG', false),
  (6307, 4307, 3004, 1001, 12, 'BÌNH THƯỜNG', 0, 0, '2025-08-01', '2026-07-31', 'HOẠT ĐỘNG', false),
  (6308, 4308, 3004, 1001, 4, 'BÌNH THƯỜNG', 0, 0, '2025-08-01', '2026-07-31', 'HOẠT ĐỘNG', false);

INSERT INTO awards (
  id, award_name, award_type, scope, season_id, match_id,
  prize_amount, conditions, status
) VALUES
  (8001, 'Vô địch V.League', 'TẬP THỂ', 'MÙA GIẢI', 1001, NULL, 5000000000, 'Trao cho đội đứng đầu bảng xếp hạng khi mùa giải khép lại.', 'ĐANG ÁP DỤNG'),
  (8002, 'Vua phá lưới V.League', 'CÁ NHÂN', 'MÙA GIẢI', 1001, NULL, 300000000, 'Trao cho cầu thủ ghi nhiều bàn thắng nhất mùa.', 'ĐANG ÁP DỤNG'),
  (8003, 'Cầu thủ xuất sắc nhất trận', 'CÁ NHÂN', 'TRẬN ĐẤU', NULL, 5001, 50000000, 'Trao cho cầu thủ chơi nổi bật nhất trận mở màn tại Hàng Đẫy.', 'ĐANG ÁP DỤNG'),
  (8004, 'Đội thi đấu cống hiến nhất trận', 'TẬP THỂ', 'TRẬN ĐẤU', NULL, 5002, 80000000, 'Trao cho đội có màn trình diễn ấn tượng nhất trận.', 'ĐANG ÁP DỤNG');

INSERT INTO award_results (
  id, award_id, team_id, player_id, bank_account, bank_name, representative, payment_status
) VALUES
  (9001, 8001, 3003, NULL, '1900368686888', 'Techcombank', 'Nguyễn Minh Quân', 'CHƯA THANH TOÁN'),
  (9002, 8002, NULL, 4204, '0123456789', 'Vietcombank', 'Rafaelson', 'ĐÃ THANH TOÁN'),
  (9003, 8003, NULL, 4006, '9988776655', 'BIDV', 'Nguyễn Quang Hải', 'CHƯA THANH TOÁN'),
  (9004, 8004, 3005, NULL, '2233445566', 'Agribank', 'Lê Tiến Thành', 'CHƯA THANH TOÁN');

INSERT INTO award_payments (
  id, award_result_id, payment_method, amount, paid_at, note, transaction_status
) VALUES
  (9501, 9002, 'CHUYỂN KHOẢN', 300000000, '2026-04-05 10:15:00', 'Đã chuyển khoản thành công cho cầu thủ.', 'THÀNH CÔNG');

INSERT INTO match_registrations (id, match_detail_id, registered_at) VALUES
  (9801, 7001, '2026-08-14 16:00:00');

INSERT INTO registration_details (id, match_registration_id, player_contract_id, role, playing_position) VALUES
  (9901, 9801, 6001, 'ĐÁ CHÍNH', 'Thủ môn'),
  (9902, 9801, 6002, 'ĐÁ CHÍNH', 'Trung vệ'),
  (9903, 9801, 6003, 'ĐÁ CHÍNH', 'Trung vệ'),
  (9904, 9801, 6004, 'ĐÁ CHÍNH', 'Hậu vệ phải'),
  (9905, 9801, 6005, 'ĐÁ CHÍNH', 'Hậu vệ trái'),
  (9906, 9801, 6006, 'ĐÁ CHÍNH', 'Tiền vệ tấn công'),
  (9907, 9801, 6007, 'ĐÁ CHÍNH', 'Tiền vệ trung tâm'),
  (9908, 9801, 6008, 'ĐÁ CHÍNH', 'Tiền đạo'),
  (9909, 9801, 6009, 'ĐÁ CHÍNH', 'Tiền đạo'),
  (9910, 9801, 6010, 'ĐÁ CHÍNH', 'Tiền đạo'),
  (9911, 9801, 6011, 'ĐÁ CHÍNH', 'Tiền vệ'),
  (9912, 9801, 6012, 'DỰ BỊ', 'Tiền đạo');
