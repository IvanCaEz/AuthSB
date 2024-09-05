DROP SEQUENCE IF EXISTS "user_id_seq";
CREATE SEQUENCE "user_id_seq" START WITH 1 INCREMENT BY 50;
DROP TABLE IF EXISTS "users";
CREATE TABLE "users" (
    "id" bigint NOT NULL,
    "username" VARCHAR(255) NOT NULL,
    "email" VARCHAR(255) NOT NULL,
    "image" VARCHAR(512) NOT NULL,
    "password" VARCHAR(255) NOT NULL,
    CONSTRAINT "users_pkey" PRIMARY KEY ("id")
);