---------- USERS TABLE ----------
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,            
    email VARCHAR(100) UNIQUE NOT NULL,             
    username VARCHAR(50) UNIQUE NOT NULL,           
    password VARCHAR(255) NOT NULL,                 
    profile_image VARCHAR(255),                     
    country VARCHAR(50),                            
    date_of_birth DATE,                             
    is_active BOOLEAN DEFAULT TRUE,                 
    role_id BIGINT,                                 
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, 
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP 
);


ALTER TABLE users
ADD CONSTRAINT fk_role_id
FOREIGN KEY (role_id) REFERENCES roles(id)
ON DELETE SET NULL;

ALTER TABLE users
ADD COLUMN public_image_id VARCHAR(255);

ALTER TABLE users
ADD COLUMN artist_name VARCHAR(255) DEFAULT NULL,
ADD COLUMN biography TEXT DEFAULT NULL;

---------- ROLES TABLE ----------
CREATE TABLE roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,      
    name VARCHAR(50) NOT NULL UNIQUE           
);

---------- TOKENS TABLE ----------
CREATE TABLE tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(255),
    refresh_token VARCHAR(255),
    token_type VARCHAR(50),
    expiration_date VARCHAR(255),
    refresh_expiration_date VARCHAR(255),
    is_mobile TINYINT(1),
    revoked BOOLEAN DEFAULT false,
    expired BOOLEAN DEFAULT false,
    user_id BIGINT,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE genre (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);


CREATE TABLE album (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    artist_id BIGINT NOT NULL,
    release_date VARCHAR(255),
    cover_url VARCHAR(255),
    status ENUM('DRAFT', 'PENDING', 'APPROVED', 'REJECTED') DEFAULT 'DRAFT',
    created_at VARCHAR(255),
    updated_at VARCHAR(255),
    FOREIGN KEY (artist_id) REFERENCES users(id) ON DELETE CASCADE
);

ALTER TABLE album
ADD COLUMN genre_id BIGINT NOT NULL,
ADD CONSTRAINT fk_album_genre
FOREIGN KEY (genre_id) REFERENCES genre(id) ON DELETE CASCADE;

ALTER TABLE albums
MODIFY COLUMN created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
MODIFY COLUMN updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
MODIFY COLUMN release_date DATE;

CREATE TABLE playlist (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    user_id BIGINT NOT NULL,
    is_public BOOLEAN DEFAULT TRUE,
    status ENUM('DRAFT', 'PENDING', 'APPROVED', 'REJECTED') DEFAULT 'DRAFT',
    created_at VARCHAR(255),
    updated_at VARCHAR(255),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
ALTER TABLE playlist
ADD COLUMN cover_url VARCHAR(255) AFTER name;
ALTER TABLE playlist
ADD COLUMN description TEXT AFTER name;
ALTER TABLE playlist
ADD COLUMN genre_id BIGINT AFTER description,
ADD CONSTRAINT fk_genre_id
FOREIGN KEY (genre_id) REFERENCES genre(id) ON DELETE SET NULL;
ALTER TABLE playlists MODIFY COLUMN status VARCHAR(50) NOT NULL DEFAULT 'DRAFT';

CREATE TABLE song_playlist (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    song_id BIGINT NOT NULL,
    playlist_id BIGINT NOT NULL,
    UNIQUE (song_id, playlist_id),
    FOREIGN KEY (song_id) REFERENCES songs(id) ON DELETE CASCADE,
    FOREIGN KEY (playlist_id) REFERENCES playlists(id) ON DELETE CASCADE
);


CREATE TABLE song_genre (
    song_id BIGINT NOT NULL,
    genre_id BIGINT NOT NULL,
    PRIMARY KEY (song_id, genre_id),
    FOREIGN KEY (song_id) REFERENCES song(id) ON DELETE CASCADE,
    FOREIGN KEY (genre_id) REFERENCES genre(id) ON DELETE CASCADE
);

CREATE TABLE song (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    artist_id BIGINT NOT NULL,
    album_id BIGINT,
    duration INT,
    secure_url VARCHAR(255) NOT NULL,
    public_id VARCHAR(255) NOT NULL,
    status ENUM('DRAFT', 'PENDING', 'APPROVED', 'REJECTED') DEFAULT 'DRAFT',
    description TEXT,
    release_date VARCHAR(255),
    created_at VARCHAR(255),
    FOREIGN KEY (artist_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (album_id) REFERENCES album(id) ON DELETE SET NULL
);

ALTER TABLE songs
MODIFY COLUMN created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
MODIFY COLUMN updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
MODIFY COLUMN release_date DATE;
ADD COLUMN deleted_at DATETIME AFTER updated_at;

-- CREATE TABLE report (
--     report_id BIGINT AUTO_INCREMENT PRIMARY KEY,
--     user_id BIGINT NOT NULL,
--     song_id BIGINT,
--     album_id BIGINT,
--     report_reason TEXT,
--     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
--     FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
--     FOREIGN KEY (song_id) REFERENCES song(song_id) ON DELETE CASCADE,
--     FOREIGN KEY (album_id) REFERENCES album(album_id) ON DELETE CASCADE
-- );


-- CREATE TABLE review (
--     review_id BIGINT AUTO_INCREMENT PRIMARY KEY,
--     user_id BIGINT NOT NULL,
--     song_id BIGINT,
--     album_id BIGINT,
--     rating INT NOT NULL CHECK (rating BETWEEN 1 AND 5),
--     review_text TEXT,
--     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
--     FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
--     FOREIGN KEY (song_id) REFERENCES song(song_id) ON DELETE CASCADE,
--     FOREIGN KEY (album_id) REFERENCES album(album_id) ON DELETE CASCADE
-- );
