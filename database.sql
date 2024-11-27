-- Bảng User
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,           -- ID tự động tăng
    email VARCHAR(100) UNIQUE NOT NULL,             -- Email duy nhất, dùng để đăng nhập
    username VARCHAR(50) UNIQUE NOT NULL,           -- Tên người dùng duy nhất
    password VARCHAR(255) NOT NULL,                 -- Mật khẩu đã mã hóa
    profile_image VARCHAR(255),                     -- Ảnh đại diện (tùy chọn)
    country VARCHAR(50),                            -- Quốc gia (tùy chọn)
    date_of_birth DATE,                             -- Ngày sinh (tùy chọn)
    is_active BOOLEAN DEFAULT TRUE,                 -- Trạng thái tài khoản (true = kích hoạt)
    role_id BIGINT,                                 -- Liên kết đến bảng roles
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Thời gian tạo tài khoản (mặc định là hiện tại)
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP -- Thời gian cập nhật
);

-- Thêm khóa ngoại cho role_id
ALTER TABLE users
ADD CONSTRAINT fk_role_id
FOREIGN KEY (role_id) REFERENCES roles(id)
ON DELETE SET NULL; -- Nếu role bị xóa, role_id sẽ bị đặt về NULL

ALTER TABLE users
ADD COLUMN public_image_id VARCHAR(255);

-- Bảng Role
CREATE TABLE roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,      -- ID của vai trò
    name VARCHAR(50) NOT NULL UNIQUE           -- Tên của vai trò (ví dụ: ROLE_ADMIN, ROLE_USER)
);

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

CREATE TABLE album_genre (
    album_id BIGINT NOT NULL,
    genre_id BIGINT NOT NULL,
    PRIMARY KEY (album_id, genre_id),
    FOREIGN KEY (album_id) REFERENCES album(id) ON DELETE CASCADE,
    FOREIGN KEY (genre_id) REFERENCES genre(id) ON DELETE CASCADE
);

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


CREATE TABLE song_playlist (
    playlist_id BIGINT NOT NULL,
    song_id BIGINT NOT NULL,
    PRIMARY KEY (playlist_id, song_id),
    FOREIGN KEY (playlist_id) REFERENCES playlist(id) ON DELETE CASCADE,
    FOREIGN KEY (song_id) REFERENCES song(id) ON DELETE CASCADE
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
