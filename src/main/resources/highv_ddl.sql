CREATE TABLE product
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,
    name          TEXT                              NOT NULL,
    description   TEXT                              NOT NULL,
    price         INT                               NOT NULL,
    product_image TEXT                              NOT NULL,
    favorite      INT                               NOT NULL,
    created_at    TIMESTAMP                         NOT NULL,
    updated_at    TIMESTAMP                         NOT NULL,
    quantity      INT                               NOT NULL,
    is_sold_out   BOOLEAN                           NOT NULL,
    deleted_at    TIMESTAMP,
    is_deleted    BOOLEAN                         NOT NULL,

    shop_id       BIGINT                            NOT NULL,
    category_id   BIGINT                            NOT NULL
);

CREATE TABLE category
(
    id   BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,
    name TEXT                              NOT NULL
);


CREATE TABLE cart_item
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,
    product_name TEXT                              NOT NULL,
    price        INT                               NOT NULL,
    quantity     INT                               NOT NULL,
    deleted_at   TIMESTAMP,
    is_deleted   BOOLEAN                           NOT NULL,

    buyer_id     BIGINT                            NOT NULL,
    order_id     BIGINT,
    product_id   BIGINT                            NOT NULL
);

CREATE TABLE coupon
(
    id             BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,
    discount_rate  FLOAT,
    discount_price BIGINT,
    expired_at     TIMESTAMP                         NOT NULL,
    created_at     TIMESTAMP                         NOT NULL,
    deleted_at    TIMESTAMP,
    is_deleted    BOOLEAN                         NOT NULL,

    product_id     BIGINT                            NOT NULL
);

CREATE TABLE coupon_to_buyer
(
    quantity  INT    NOT NULL,

    coupon_id BIGINT NOT NULL,
    buyer_id  BIGINT NOT NULL
);

CREATE TABLE shop
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,
    name        TEXT                              NOT NULL,
    description TEXT                              NOT NULL,
    shop_image  TEXT                              NOT NULL,
    rate        FLOAT                             NOT NULL,

    seller_id   BIGINT                            NOT NULL
);

CREATE TABLE seller
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,
    nickname      TEXT                              NOT NULL,
    password      TEXT                              NOT NULL,
    email         TEXT                              NOT NULL,
    profile_image TEXT                              NOT NULL,
    phone_number  TEXT                              NOT NULL,
    address       TEXT                              NOT NULL
);


CREATE TABLE sales_history
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,
    price        INT                               NOT NULL,
    reg_dt       TIMESTAMP                         NOT NULL,
    buyer_name   TEXT                              NOT NULL,

    order_id     BIGINT                            NOT NULL,
    seller_id    BIGINT                            NOT NULL
);

CREATE TABLE buyer_history
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,
    seller_name  TEXT                              NOT NULL,

    order_id     BIGINT                            NOT NULL,
    buyer_id     BIGINT                            NOT NULL
);

CREATE TABLE products_order
(
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,
    buyer_id           BIGINT                            NOT NULL,
    status_code        TEXT                              NOT NULL,
    paid_yn            BOOLEAN                           NOT NULL,
    pay_dt             TIMESTAMP                         NOT NULL,
    total_price        INT                               NOT NULL,
    delivery_start_at  TIMESTAMP                         NOT NULL,
    delivery_end_at    TIMESTAMP                         NOT NULL,
    cancel_yn          BOOLEAN                           NOT NULL,
    cancel_dt          TIMESTAMP                         NOT NULL,
    cancel_desc        TEXT                              NOT NULL,
    refund_yn          BOOLEAN                           NOT NULL,
    refund_dt          TIMESTAMP                         NOT NULL,
    refund_desc        TEXT                              NOT NULL,
    refund_reject_yn   BOOLEAN                           NOT NULL,
    refund_reject_dt   TIMESTAMP                         NOT NULL,
    refund_reject_desc TEXT                              NOT NULL,
    reg_dt             TIMESTAMP                         NOT NULL,
    deleted_at    TIMESTAMP,
    is_deleted    BOOLEAN                         NOT NULL
);

CREATE TABLE buyer
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,
    nickname      TEXT                              NOT NULL,
    password      TEXT                              NOT NULL,
    email         TEXT                              NOT NULL,
    profile_image TEXT                              NOT NULL,
    phone_number  TEXT                              NOT NULL,
    address       TEXT                              NOT NULL
);

CREATE TABLE comment
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,
    content    TEXT                              NOT NULL,
    image      TEXT                              NOT NULL,
    rate       FLOAT                             NOT NULL,
    deleted_at    TIMESTAMP                    ,
    is_deleted    BOOLEAN                        NOT NULL,

    buyer_id   BIGINT                            NOT NULL,
    product_id BIGINT                            NOT NULL
);

CREATE TABLE black_list(
    id BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,
    nickname TEXT NOT NULL,
    email TEXT NOT NULL
);

CREATE TABLE favorite(
    product_id BIGINT NOT NULL,
    buyer_id BIGINT NOT NULL
);


ALTER TABLE sales_history ADD CONSTRAINT fk_sales_history_seller_id
FOREIGN KEY (seller_id)
REFERENCES seller (id);


ALTER TABLE sales_history ADD CONSTRAINT fk_sales_history_products_order_id
FOREIGN KEY (order_id)
REFERENCES products_order (id);

ALTER TABLE buyer_history ADD CONSTRAINT fk_buyer_history_seller_id
FOREIGN KEY (buyer_id)
REFERENCES buyer (id);


ALTER TABLE buyer_history ADD CONSTRAINT fk_buyer_history_products_order_id
FOREIGN KEY (order_id)
REFERENCES products_order (id);

ALTER TABLE coupon_to_buyer ADD CONSTRAINT fk_coupon_to_buyer_buyer_id
FOREIGN KEY (buyer_id)
REFERENCES buyer (id);

ALTER TABLE coupon_to_buyer ADD CONSTRAINT fk_coupon_to_buyer_coupon_id
FOREIGN KEY (coupon_id)
REFERENCES coupon (id);

ALTER TABLE coupon ADD CONSTRAINT fk_coupon_product_id
FOREIGN KEY (product_id)
REFERENCES product (id);


ALTER TABLE product ADD CONSTRAINT fk_product_shop_id
FOREIGN KEY (shop_id)
REFERENCES shop (id);

ALTER TABLE product ADD CONSTRAINT fk_product_category_id
FOREIGN KEY (category_id)
REFERENCES category (id);

ALTER TABLE cart_item ADD CONSTRAINT fk_cart_item_product_id
FOREIGN KEY (product_id)
REFERENCES product (id);

ALTER TABLE cart_item ADD CONSTRAINT fk_cart_item_buyer_id
FOREIGN KEY (buyer_id)
REFERENCES buyer (id);

ALTER TABLE cart_item ADD CONSTRAINT fk_cart_item_order_id
FOREIGN KEY (order_id)
REFERENCES products_order (id);

ALTER TABLE favorite ADD CONSTRAINT fk_favorite_product_id
    FOREIGN KEY (product_id)
        REFERENCES product (id);

ALTER TABLE favorite ADD CONSTRAINT fk_favorite_buyer_id
    FOREIGN KEY (buyer_id)
        REFERENCES buyer (id);

ALTER TABLE comment ADD CONSTRAINT fk_comment_buyer_id
FOREIGN KEY (buyer_id)
REFERENCES buyer (id);

