-- create database

CREATE DATABASE unit_techno;

\c unit_techno

CREATE SCHEMA car_entry_control;

CREATE USER car_entry_admin WITH password 'squd';

ALTER USER car_entry_admin WITH SUPERUSER;

GRANT USAGE ON SCHEMA car_entry_control TO car_entry_admin;

ALTER SCHEMA car_entry_control OWNER TO car_entry_admin;
