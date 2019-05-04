--
-- PostgreSQL database dump
--

-- Dumped from database version 10.7 (Ubuntu 10.7-1.pgdg16.04+1)
-- Dumped by pg_dump version 10.3

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: public; Type: SCHEMA; Schema: -; Owner: vxqseylefogbpe
--

CREATE SCHEMA "public";


ALTER SCHEMA public OWNER TO vxqseylefogbpe;

--
-- Name: SCHEMA "public"; Type: COMMENT; Schema: -; Owner: vxqseylefogbpe
--

COMMENT ON SCHEMA "public" IS 'standard public schema';


--
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS "plpgsql" WITH SCHEMA "pg_catalog";


--
-- Name: EXTENSION "plpgsql"; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION "plpgsql" IS 'PL/pgSQL procedural language';


SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: items; Type: TABLE; Schema: public; Owner: vxqseylefogbpe
--

CREATE TABLE "public"."items" (
    "id" integer NOT NULL,
    "barcode" character varying(255),
    "category" character varying(255),
    "description" character varying(255),
    "quantity" integer,
    "title" character varying(255)
);


ALTER TABLE public.items OWNER TO vxqseylefogbpe;

--
-- Name: items_id_seq; Type: SEQUENCE; Schema: public; Owner: vxqseylefogbpe
--

CREATE SEQUENCE "public"."items_id_seq"
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.items_id_seq OWNER TO vxqseylefogbpe;

--
-- Name: items_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: vxqseylefogbpe
--

ALTER SEQUENCE "public"."items_id_seq" OWNED BY "public"."items"."id";


--
-- Name: items id; Type: DEFAULT; Schema: public; Owner: vxqseylefogbpe
--

ALTER TABLE ONLY "public"."items" ALTER COLUMN "id" SET DEFAULT "nextval"('"public"."items_id_seq"'::"regclass");


--
-- Data for Name: items; Type: TABLE DATA; Schema: public; Owner: vxqseylefogbpe
--

COPY "public"."items" ("id", "barcode", "category", "description", "quantity", "title") FROM stdin;
135	2580085258		just a juice	3	Orange juice
131	3908545388558	radler	Known for the typical smell of beer and grapefruit and a pleasant combination of sweet and slightly bitter taste. Complete double refreshment.	2	Union Radler
136	36980882544554		Klubska steklenica	3	Heineken Steklenička
137	036000291452		Tvoja narava je zabava	84	Jabolčni Tat
141	036000291453		Laško Nepasterizirano offers a unique freshness, which is why its shelf life is limited to 12 weeks.	94	Laško Nepasterizirano
183	5060154914160		hey man	14	Vagabond
184	55688			13	sok
138	36805259042880		Full flavor, medium bitterness	40	Union Lager
185	123			99	Radenska
130	0854258855687		no description	13	Heineken Pločevinka
132	6336655288855		no description	3	Heineken Draughtkeg
139	671860013624		no description	88	Heineken Ikona
129	937825387446		vedno in povsod	28	Heineken 0.0
134	33336888006	beer	Laško Zlatorog dark offers a characteristic aroma from the original 1941 recipe.	15	Laško Zlatorog Temno
133	22255808445588	🍺	A specific taste which has remained the same over years owing to our unchanged recipe. Full of pride!	0	Laško Zlatorog Svetlo
140	22580041144		Laško Weißbier was inspired by the freshness of the Alps.	87	Laško Weißbier
128	118181858	beer	Laško Malt is the good brother of the Zlatorog family – never a corny sweetheart, always clear-headed.	86	Laško Malt
\.


--
-- Name: items_id_seq; Type: SEQUENCE SET; Schema: public; Owner: vxqseylefogbpe
--

SELECT pg_catalog.setval('"public"."items_id_seq"', 185, true);


--
-- Name: items items_pkey; Type: CONSTRAINT; Schema: public; Owner: vxqseylefogbpe
--

ALTER TABLE ONLY "public"."items"
    ADD CONSTRAINT "items_pkey" PRIMARY KEY ("id");


--
-- PostgreSQL database dump complete
--

