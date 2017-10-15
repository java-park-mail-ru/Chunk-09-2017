--
-- PostgreSQL database dump
--

-- Dumped from database version 9.5.8
-- Dumped by pg_dump version 9.5.8

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


--
-- Name: citext; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS citext WITH SCHEMA public;


--
-- Name: EXTENSION citext; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION citext IS 'data type for case-insensitive character strings';


SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: score; Type: TABLE; Schema: public; Owner: trubnikov
--

CREATE TABLE score (
    id bigint NOT NULL,
    result double precision NOT NULL,
    "time" timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE score OWNER TO trubnikov;

--
-- Name: score_id_seq; Type: SEQUENCE; Schema: public; Owner: trubnikov
--

CREATE SEQUENCE score_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE score_id_seq OWNER TO trubnikov;

--
-- Name: score_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: trubnikov
--

ALTER SEQUENCE score_id_seq OWNED BY score.id;


--
-- Name: users; Type: TABLE; Schema: public; Owner: trubnikov
--

CREATE TABLE users (
    id bigint NOT NULL,
    login citext NOT NULL,
    password character varying(30) NOT NULL,
    email citext NOT NULL
);


ALTER TABLE users OWNER TO trubnikov;

--
-- Name: users_id_seq; Type: SEQUENCE; Schema: public; Owner: trubnikov
--

CREATE SEQUENCE users_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE users_id_seq OWNER TO trubnikov;

--
-- Name: users_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: trubnikov
--

ALTER SEQUENCE users_id_seq OWNED BY users.id;


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: trubnikov
--

ALTER TABLE ONLY score ALTER COLUMN id SET DEFAULT nextval('score_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: trubnikov
--

ALTER TABLE ONLY users ALTER COLUMN id SET DEFAULT nextval('users_id_seq'::regclass);


--
-- Name: users_pkey; Type: CONSTRAINT; Schema: public; Owner: trubnikov
--

ALTER TABLE ONLY users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- Name: users_email_uindex; Type: INDEX; Schema: public; Owner: trubnikov
--

CREATE UNIQUE INDEX users_email_uindex ON users USING btree (email);


--
-- Name: users_username_uindex; Type: INDEX; Schema: public; Owner: trubnikov
--

CREATE UNIQUE INDEX users_username_uindex ON users USING btree (login);


--
-- Name: score_users_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: trubnikov
--

ALTER TABLE ONLY score
    ADD CONSTRAINT score_users_id_fk FOREIGN KEY (id) REFERENCES users(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- PostgreSQL database dump complete
--

