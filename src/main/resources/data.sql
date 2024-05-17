INSERT INTO users (id, name, email, password, created, modified, last_login, is_active) VALUES
          (RANDOM_UUID(), 'Alejandro', 'alejandro@example.com', '$2a$10$.NCgBTN2uGvypuXFBAhhOOom//.rzWnD8Ivo9AgdlhbQseai04lsC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, true),
          (RANDOM_UUID(), 'Carolina', 'carolina@example.com', '$2a$10$.NCgBTN2uGvypuXFBAhhOOom//.rzWnD8Ivo9AgdlhbQseai04lsC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, true),
          (RANDOM_UUID(), 'Eduardo', 'eduardo@example.com', '$2a$10$.NCgBTN2uGvypuXFBAhhOOom//.rzWnD8Ivo9AgdlhbQseai04lsC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, true),
          (RANDOM_UUID(), 'Sofía', 'sofia@example.com', '$2a$10$HiHfB5aeTcXZXhf9Gkujg.QzIFcgP84bWVtSEn7DnR54YJ.RQ27G6', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, true),
          (RANDOM_UUID(), 'Lucía', 'lucia@example.com', '$2a$10$.NCgBTN2uGvypuXFBAhhOOom//.rzWnD8Ivo9AgdlhbQseai04lsC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, true);

INSERT INTO user_phones (number, city_code, country_code, user_id) VALUES
           ('3001234567', 17, 012, (SELECT id FROM users WHERE email = 'alejandro@example.com')),
           ('3107654321', 24, 036, (SELECT id FROM users WHERE email = 'carolina@example.com')),
           ('3209876543', 31, 064, (SELECT id FROM users WHERE email = 'eduardo@example.com')),
           ('3156789012', 42, 854, (SELECT id FROM users WHERE email = 'sofia@example.com')),
           ('3189756322', 42, 854, (SELECT id FROM users WHERE email = 'sofia@example.com')),
           ('3051234567', 55, 148, (SELECT id FROM users WHERE email = 'lucia@example.com'));