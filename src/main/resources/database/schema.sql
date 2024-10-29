CREATE TABLE IF NOT EXISTS users
(
    id         INTEGER PRIMARY KEY AUTOINCREMENT,
    username   VARCHAR(255) NOT NULL,
    password   VARCHAR(255) NOT NULL,
    currency   INTEGER   DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS cards
(
    id             INTEGER PRIMARY KEY AUTOINCREMENT,
    name           VARCHAR(255) NOT NULL,
    rarity         VARCHAR(50)  NOT NULL,
    hp             INTEGER,
    dmg            INTEGER,
    collection     VARCHAR(255),
    release_number INTEGER
);

CREATE TABLE IF NOT EXISTS pack_types
(
    id               INTEGER PRIMARY KEY AUTOINCREMENT,
    name             VARCHAR(255) NOT NULL,
    collection       VARCHAR(255) NOT NULL,
    price            INTEGER      NOT NULL,
    cards_per_pack   INTEGER      NOT NULL,
    common_chance    REAL,
    uncommon_chance  REAL,
    rare_chance      REAL,
    epic_chance      REAL,
    legendary_chance REAL,
    mythic_chance    REAL
);

CREATE TABLE IF NOT EXISTS user_collection
(
    id             INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id        INTEGER NOT NULL,
    card_id        INTEGER NOT NULL,
    quantity       INTEGER   DEFAULT 1,
    first_acquired TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (card_id) REFERENCES cards (id)
);

CREATE TABLE IF NOT EXISTS marketplace_listing
(
    id         INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id    INTEGER NOT NULL,
    card_id    INTEGER NOT NULL,
    price      INTEGER NOT NULL,
    status     VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (card_id) REFERENCES cards (id)
);

CREATE TABLE IF NOT EXISTS trades
(
    id         INTEGER PRIMARY KEY AUTOINCREMENT,
    user1_id   INTEGER NOT NULL,
    user2_id   INTEGER NOT NULL,
    status     VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user1_id) REFERENCES users (id),
    FOREIGN KEY (user2_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS trade_cards
(
    id       INTEGER PRIMARY KEY AUTOINCREMENT,
    trade_id INTEGER NOT NULL,
    user_id  INTEGER NOT NULL,
    card_id  INTEGER NOT NULL,
    quantity INTEGER DEFAULT 1,
    FOREIGN KEY (trade_id) REFERENCES trades (id),
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (card_id) REFERENCES cards (id)
);

CREATE TABLE IF NOT EXISTS user_packs
(
    id           INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id      INTEGER NOT NULL,
    pack_type_id INTEGER NOT NULL,
    quantity     INTEGER   DEFAULT 1,
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (pack_type_id) REFERENCES pack_types (id)
);