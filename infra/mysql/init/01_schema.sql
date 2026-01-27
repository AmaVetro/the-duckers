-- =====================================================
-- The Duckers - MySQL Schema
-- File: 01_schema.sql
-- Purpose: Create all transactional tables
-- Author: Vicente
-- Date: 2026-01-21
-- =====================================================


-- -----------------------------------------------------
-- Database
-- -----------------------------------------------------
CREATE DATABASE IF NOT EXISTS the_duckers;
USE the_duckers;


-- -----------------------------------------------------
-- Table: users
-- -----------------------------------------------------
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    email VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,

    first_name VARCHAR(100) NOT NULL,
    last_name_father VARCHAR(100) NOT NULL,
    last_name_mother VARCHAR(100) NOT NULL,

    date_of_birth DATE NULL,
    phone_number VARCHAR(30) NULL,
    address VARCHAR(255) NULL,

    referral_code VARCHAR(50) NOT NULL,
    referred_by_user_id BIGINT NULL,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT uq_users_email UNIQUE (email),
    CONSTRAINT uq_users_referral_code UNIQUE (referral_code),

    CONSTRAINT fk_users_referred_by
        FOREIGN KEY (referred_by_user_id)
        REFERENCES users(id)
        ON DELETE SET NULL
);

-- -----------------------------------------------------
-- Table: referrals
-- -----------------------------------------------------
CREATE TABLE referrals (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    referrer_user_id BIGINT NOT NULL,
    referred_user_id BIGINT NOT NULL,

    bonus_points BIGINT NOT NULL,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_referrals_referrer
        FOREIGN KEY (referrer_user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_referrals_referred
        FOREIGN KEY (referred_user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT uq_referrals_referred_user
        UNIQUE (referred_user_id),

    CONSTRAINT chk_referrals_bonus_non_negative
        CHECK (bonus_points >= 0),

    CONSTRAINT chk_referrals_no_self_referral
        CHECK (referrer_user_id <> referred_user_id)
);

-- -----------------------------------------------------
-- Table: levels
-- -----------------------------------------------------
CREATE TABLE levels (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    name VARCHAR(50) NOT NULL,
    min_points BIGINT NOT NULL,

    CONSTRAINT uq_levels_name UNIQUE (name),
    CONSTRAINT chk_levels_min_points CHECK (min_points >= 0)
);

-- -----------------------------------------------------
-- Table: user_points
-- -----------------------------------------------------
CREATE TABLE user_points (
    user_id BIGINT PRIMARY KEY,

    balance BIGINT NOT NULL,
    total_earned BIGINT NOT NULL,

    updated_at TIMESTAMP NOT NULL
        DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_user_points_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT chk_user_points_balance_non_negative
        CHECK (balance >= 0),

    CONSTRAINT chk_user_points_total_earned_non_negative
        CHECK (total_earned >= 0),

    CONSTRAINT chk_user_points_total_vs_balance
        CHECK (total_earned >= balance)
);

-- -----------------------------------------------------
-- Table: shopping_carts
-- -----------------------------------------------------
CREATE TABLE shopping_carts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    user_id BIGINT NOT NULL,

    status VARCHAR(20) NOT NULL,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT uq_shopping_carts_user UNIQUE (user_id),

    CONSTRAINT fk_shopping_carts_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT chk_shopping_carts_status
        CHECK (status IN ('ACTIVE', 'CHECKED_OUT'))
);

-- -----------------------------------------------------
-- Table: shopping_cart_items
-- -----------------------------------------------------
CREATE TABLE shopping_cart_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    cart_id BIGINT NOT NULL,

    product_id VARCHAR(100) NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    unit_price BIGINT NOT NULL,

    quantity INT NOT NULL,
    subtotal BIGINT NOT NULL,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_cart_items_cart
        FOREIGN KEY (cart_id)
        REFERENCES shopping_carts(id)
        ON DELETE CASCADE,

    CONSTRAINT uq_cart_product UNIQUE (cart_id, product_id),

    CONSTRAINT chk_cart_items_quantity
        CHECK (quantity >= 1),

    CONSTRAINT chk_cart_items_unit_price
        CHECK (unit_price >= 0),

    CONSTRAINT chk_cart_items_subtotal
        CHECK (subtotal >= 0)
);

-- -----------------------------------------------------
-- Table: orders
-- -----------------------------------------------------
CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    user_id BIGINT NOT NULL,

    status VARCHAR(20) NOT NULL,

    subtotal BIGINT NOT NULL,
    discount BIGINT NOT NULL,
    iva BIGINT NOT NULL,
    total BIGINT NOT NULL,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,
    paid_at TIMESTAMP NULL,

    CONSTRAINT fk_orders_user
        FOREIGN KEY (user_id)
        REFERENCES users(id), 

    CONSTRAINT chk_orders_status
        CHECK (status IN ('PENDING', 'PAID', 'CANCELLED')),

    CONSTRAINT chk_orders_subtotal
        CHECK (subtotal >= 0),

    CONSTRAINT chk_orders_discount
        CHECK (discount >= 0),

    CONSTRAINT chk_orders_iva
        CHECK (iva >= 0),

    CONSTRAINT chk_orders_total
        CHECK (total >= 0),

    CONSTRAINT chk_orders_total_formula
        CHECK (total = (subtotal - discount + iva))
);

-- -----------------------------------------------------
-- Table: order_items
-- -----------------------------------------------------
CREATE TABLE order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    order_id BIGINT NOT NULL,

    product_id VARCHAR(100) NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    unit_price BIGINT NOT NULL,

    quantity INT NOT NULL,
    subtotal BIGINT NOT NULL,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_order_items_order
        FOREIGN KEY (order_id)
        REFERENCES orders(id)
        ON DELETE CASCADE,

    CONSTRAINT uq_order_product UNIQUE (order_id, product_id),

    CONSTRAINT chk_order_items_quantity
        CHECK (quantity >= 1),

    CONSTRAINT chk_order_items_unit_price
        CHECK (unit_price >= 0),

    CONSTRAINT chk_order_items_subtotal
        CHECK (subtotal >= 0)
);

-- -----------------------------------------------------
-- Table: point_redemptions
-- -----------------------------------------------------
CREATE TABLE point_redemptions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    user_id BIGINT NOT NULL,
    order_id BIGINT NOT NULL,

    points_used BIGINT NOT NULL,
    discount_amount BIGINT NOT NULL,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_point_redemptions_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_point_redemptions_order
        FOREIGN KEY (order_id)
        REFERENCES orders(id)
        ON DELETE CASCADE,

    CONSTRAINT uq_point_redemptions_order
        UNIQUE (order_id),

    CONSTRAINT chk_point_redemptions_points
        CHECK (points_used > 0),

    CONSTRAINT chk_point_redemptions_discount
        CHECK (discount_amount >= 0)
);



-- -----------------------------------------------------
-- Strategic/Performance Indexes
-- -----------------------------------------------------

-- Orders
CREATE INDEX idx_orders_user_id
ON orders(user_id);

CREATE INDEX idx_orders_status
ON orders(status);

-- Order items
CREATE INDEX idx_order_items_order_id
ON order_items(order_id);

-- Shopping cart items
CREATE INDEX idx_cart_items_cart_id
ON shopping_cart_items(cart_id);