INSERT INTO usr(id, account_non_expired, account_non_locked, activation_code, avatar, credentials_non_expired, deposit,
 email, enabled, firstname, have_new_bets, have_new_messages, is_online, lastname, password, steam_id, username)
VALUES (1, true, true, null, 'default', true, 100, 'dkvoznyuk@yandex.ru', true, 'Дмитрий', false, false, false,
'Вознюк', '$2a$10$oDvbHz07EQga4Y8iZ5SI8eWHgUOZK5eOQ4bLmetlQxDx2/sEg7ILK', '76561198799034987', 'vdk64'),
(2, true, true, null, 'default', true, 100, 'user@mail.ru', true, 'Ivan', false, false, false, 'Petrov',
 '$2a$10$oDvbHz07EQga4Y8iZ5SI8eWHgUOZK5eOQ4bLmetlQxDx2/sEg7ILK', null, 'user'),
(3, true, true, null, 'default', true, 550, 'kasha@mail.ru', true, 'Аркадий', false, false, false, 'Ротенберг',
'$2a$10$oDvbHz07EQga4Y8iZ5SI8eWHgUOZK5eOQ4bLmetlQxDx2/sEg7ILK', '76561199004382586', 'kasha111'),
(4, true, true, null, 'default', true, 100, 'antony@yandex.ru', true, 'Антон', false, false, false, 'Васильев',
'$2a$10$oDvbHz07EQga4Y8iZ5SI8eWHgUOZK5eOQ4bLmetlQxDx2/sEg7ILK', null, 'tony64'),
(5, true, true, null, 'default', true, 100, 'vasdas@rambler.ru', true, 'Василий', false, false, false, 'Самойлов',
 '$2a$10$oDvbHz07EQga4Y8iZ5SI8eWHgUOZK5eOQ4bLmetlQxDx2/sEg7ILK', null, 'vasiliy228'),
(6, true, true, null, 'default', true, 100, 'dobro@mail.ru', true, 'Петр', false, false, false, 'Добронравов',
'$2a$10$oDvbHz07EQga4Y8iZ5SI8eWHgUOZK5eOQ4bLmetlQxDx2/sEg7ILK', null, 'petro123');


INSERT INTO role_authorities(authorities_id, authorities) VALUES (1, 'USER'), (1, 'ADMIN'), (2, 'USER'), (3, 'USER'),
(4, 'USER'), (5, 'USER'), (6, 'USER');


INSERT INTO usr_fr(user_id, friend_id) VALUES (1, 3), (1, 5), (1, 6), (3, 1), (4, 1);


INSERT INTO game(id, game_mode, is_opponent_ready, is_user_ready, lobby_name, opponent_steam_id64, password,
server_start_time, status, user_steam_id64) VALUES (1, '1x1', false, false, 'MyLobby', '76561199004382586', 'app',
null, null, '76561198799034987');


INSERT INTO bet(id, is_confirm, is_new, value, who_win, game_id, opponent_id, usr_id)
VALUES (1, false, false, 450, null, 1, 3, 1), (2, false, false, 730, 'vdk64', null, 1, 6),
(3, false, false, 200, 'vdk64', null, 1, 2);


INSERT INTO dlg(id, have_new_messages, last_new_message) VALUES (1, false, 1586208708983),
(2, false, 1586208712983), (3, false, 1586208715983);


INSERT INTO dlg_usr(dlg_id, usr_id) VALUES (1, 1), (1, 3), (2, 6),
(2, 1), (3, 1), (3, 5);


INSERT INTO msg(id, date, from_id, new_message, text, to_id, dlg_id)
VALUES (1, '2020-04-07 01:46:19.617', 1, false, 'Hey, Kasha!', 3, 1),
(2, '2020-04-07 01:46:19.617', 3, false, 'Hello, vkd64!', 1, 1),
(3, '2020-04-07 01:46:19.617', 1, false, 'How are you?', 3, 1),
(4, '2020-04-07 01:46:19.617', 3, false, 'Fine, and you?', 1, 1),
(5, '2020-04-07 01:46:19.617', 1, false, 'Fine thanks', 3, 1),
(6, '2020-04-07 01:46:19.617', 5, false, 'vasiliy228 text', 1, 3),
(7, '2020-04-07 01:46:19.617', 6, false, 'petro123 text', 1, 2);


INSERT INTO shw_status(id, username, visible, dlg_id) VALUES (1, 'vdk64', true, 1),
(2, 'kasha111', true, 1), (3, 'vdk64', true, 2), (4, 'petro123', true, 2),
(5, 'vdk64', true, 3), (6, 'vasiliy228', true, 3);