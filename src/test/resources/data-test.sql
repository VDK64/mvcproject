INSERT INTO usr(id, account_non_expired, account_non_locked, activation_code, avatar, credentials_non_expired, deposit,
email, enabled, firstname, have_new_bets, have_new_messages, is_online, lastname, password, steam_id, username)
VALUES (1, true, true, null, 'default', true, 1000, 'dkvoznyuk@yandex.ru', true, 'Дмитрий', false, false, false,
'Вознюк', '$2a$10$oDvbHz07EQga4Y8iZ5SI8eWHgUOZK5eOQ4bLmetlQxDx2/sEg7ILK', '76561198799034987', 'vdk64'),
(2, true, true, null, 'default', true, 100, 'user@mail.ru', true, 'Ivan', false, false, false, 'Petrov',
'$2a$10$oDvbHz07EQga4Y8iZ5SI8eWHgUOZK5eOQ4bLmetlQxDx2/sEg7ILK', null, 'user'),
(3, true, true, null, 'default', true, 1000, 'kasha@mail.ru', true, 'Аркадий', false, false, false, 'Ротенберг',
'$2a$10$oDvbHz07EQga4Y8iZ5SI8eWHgUOZK5eOQ4bLmetlQxDx2/sEg7ILK', '76561199004382586', 'kasha111'),
(4, true, true, null, 'default', true, 100, 'antony@yandex.ru', true, 'Антон', false, false, false, 'Васильев',
'$2a$10$oDvbHz07EQga4Y8iZ5SI8eWHgUOZK5eOQ4bLmetlQxDx2/sEg7ILK', null, 'tony64'),
(5, true, true, null, 'default', true, 100, 'vasdas@rambler.ru', true, 'Василий', false, false, false, 'Самойлов',
'$2a$10$oDvbHz07EQga4Y8iZ5SI8eWHgUOZK5eOQ4bLmetlQxDx2/sEg7ILK', null, 'vasiliy228'),
(6, true, true, null, 'default', true, 100, 'dobro@mail.ru', true, 'Петр', false, false, false, 'Добронравов',
'$2a$10$oDvbHz07EQga4Y8iZ5SI8eWHgUOZK5eOQ4bLmetlQxDx2/sEg7ILK', null, 'petro123'),
(7, true, true, null, 'default', true, 100, 'test@test', true, 'test', false, false, false, 'test',
'$2a$10$oDvbHz07EQga4Y8iZ5SI8eWHgUOZK5eOQ4bLmetlQxDx2/sEg7ILK', null, 'testUser');


INSERT INTO role_authorities(authorities_id, authorities) VALUES (1, 'USER'), (1, 'ADMIN'), (2, 'USER'), (3, 'USER'),
(4, 'USER'), (5, 'USER'), (6, 'USER'), (7, 'USER');


INSERT INTO usr_fr(user_id, friend_id) VALUES (1, 3), (1, 5), (1, 6), (3, 1), (4, 1), (5, 1), (3, 7), (7, 3);


INSERT INTO game(id, game_mode, is_opponent_ready, is_user_ready, lobby_name, opponent_steam_id64, password,
server_start_time, status, user_steam_id64) VALUES (1, '1x1', false, false, 'MyLobby', '76561199004382586', 'app',
null, null, '76561198799034987'), (2, '1x1', false, false, 'TestLobby', '123', 'app',
null, null, '76561198799034987'), (3, '1x1', false, false, 'TestLobbyWhichDelete', '123', 'app',
null, null, '76561198799034987');

INSERT INTO bet(id, is_confirm, is_new, value, who_win, game_id, opponent_id, usr_id)
VALUES (1, false, false, 450, null, 1, 3, 1), (2, false, false, 730, 'vdk64', null, 1, 6),
(3, false, false, 200, 'vdk64', null, 1, 2), (4, false, false, 1, 'testUser', null, 2, 7),
(5, false, false, 2, 'testUser', null, 2, 7), (6, false, false, 3, 'testUser', null, 2, 7),
(7, false, false, 4, 'testUser', null, 2, 7), (8, false, false, 5, 'testUser', null, 2, 7),
(9, false, false, 6, 'testUser', null, 2, 7), (10, false, false, 7, 'testUser', null, 2, 7),
(11, false, false, 8, 'testUser', null, 2, 7), (12, false, false, 9, 'testUser', null, 2, 7),
(13, false, false, 10, 'testUser', null, 2, 7), (14, false, false, 11, 'testUser', null, 2, 7),
(15, false, false, 12, 'testUser', null, 2, 7), (16, false, false, 13, 'testUser', null, 2, 7),
(17, false, false, 14, 'testUser', null, 2, 7), (18, false, false, 15, 'testUser', null, 2, 7),
(19, false, false, 16, 'testUser', null, 2, 7), (20, false, false, 17, 'testUser', null, 2, 7),
(21, false, false, 18, 'testUser', null, 2, 7), (22, false, false, 19, 'testUser', null, 2, 7),
(23, false, false, 20, 'testUser', null, 2, 7), (24, false, false, 21, 'testUser', null, 2, 7),
(25, false, false, 22, 'testUser', null, 2, 7), (26, false, false, 23, 'testUser', null, 2, 7),
(27, false, false, 24, 'testUser', null, 2, 7), (28, false, false, 25, 'testUser', null, 2, 7),
(29, false, false, 26, 'testUser', null, 2, 7), (30, false, false, 27, 'testUser', null, 2, 7),
(31, false, false, 28, 'testUser', null, 2, 7),
-------------------------------------------------------------------------------------------------------------------
(32, false, false, 1, 'testUser', null, 7, 5), (33, false, false, 2, 'testUser', null, 7, 5),
(34, false, false, 3, 'testUser', null, 7, 5), (35, false, false, 4, 'testUser', null, 7, 5),
(36, false, false, 5, 'testUser', null, 7, 5), (37, false, false, 6, 'testUser', null, 7, 5),
(38, false, false, 7, 'testUser', null, 7, 5), (39, false, false, 8, 'testUser', null, 7, 5),
(40, false, false, 9, 'testUser', null, 7, 5), (41, false, false, 10, 'testUser', null, 7, 5),
(42, false, false, 11, 'testUser', null, 7, 5), (43, false, false, 12, 'testUser', null, 7, 5),
(44, false, false, 13, 'testUser', null, 7, 5), (45, false, false, 14, 'testUser', null, 7, 5),
(46, false, false, 15, 'testUser', null, 7, 5), (47, false, false, 16, 'testUser', null, 7, 5),
(48, false, false, 17, 'testUser', null, 7, 5), (49, false, false, 18, 'testUser', null, 7, 5),
(50, false, false, 19, 'testUser', null, 7, 5), (51, false, false, 20, 'testUser', null, 7, 5),
(52, false, false, 21, 'testUser', null, 7, 5), (53, false, false, 22, 'testUser', null, 7, 5),
(54, false, false, 23, 'testUser', null, 7, 5), (55, false, false, 24, 'testUser', null, 7, 5),
(56, false, false, 25, 'testUser', null, 7, 5), (57, false, false, 26, 'testUser', null, 7, 5),
(58, false, false, 27, 'testUser', null, 7, 5), (59, false, false, 28, 'testUser', null, 7, 5),
(60, false, false, 29, 'testUser', null, 7, 5), (61, false, false, 30, 'testUser', null, 7, 5),
(62, false, false, 31, 'testUser', null, 7, 5), (63, false, false, 32, 'testUser', null, 7, 5),
(64, false, false, 200, 'testUser', null, 7, 1), (65, false, false, 200, null, 3, 6, 1);



INSERT INTO dlg(id, have_new_messages, last_new_message) VALUES (1, false, 1586208708983),
(2, false, 1586208712983);


INSERT INTO dlg_usr(dlg_id, usr_id) VALUES (1, 1), (1, 3), (2, 6),
(2, 1);


INSERT INTO msg(id, date, from_id, new_message, text, to_id, dlg_id)
VALUES (1, '2020-04-07 01:46:19.617', 1, false, 'Hey, Kasha!', 3, 1),
(2, '2020-04-07 01:46:19.617', 3, false, 'Hello, vkd64!', 1, 1),
(3, '2020-04-07 01:46:19.617', 1, false, 'How are you?', 3, 1),
(4, '2020-04-07 01:46:19.617', 3, false, 'Fine, and you?', 1, 1),
(5, '2020-04-07 01:46:19.617', 1, false, 'Fine thanks', 3, 1),
(6, '2020-04-07 01:46:19.617', 6, false, 'petro123 text', 1, 2);


INSERT INTO shw_status(id, username, visible, dlg_id) VALUES (1, 'vdk64', true, 1),
(2, 'kasha111', true, 1), (3, 'vdk64', true, 2), (4, 'petro123', true, 2);