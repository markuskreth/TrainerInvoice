CREATE DATABASE trainer_abrechnungen
    WITH
    ENCODING = 'UTF8'
    LC_COLLATE = 'German_Germany.1252'
    LC_CTYPE = 'German_Germany.1252'
    CONNECTION LIMIT = -1;

CREATE TABLE public.USERDATA
(
    id BIGSERIAL PRIMARY KEY,
    principal_id character varying(255) NOT NULL,
    email character varying(255) NOT NULL,
    family_name character varying(255) NOT NULL,
    given_name character varying(255) NOT NULL,
    updated timestamp without time zone,
    created timestamp without time zone
);

CREATE TABLE public.adress
(
    user_id bigint NOT NULL,
    adress1 character varying(255) NOT NULL,
    adress2 character varying(255),
    updated timestamp without time zone,
    city character varying(155),
    created timestamp without time zone,
    zip character varying(45),
    CONSTRAINT adress_pkey PRIMARY KEY (user_id),
    CONSTRAINT adress_user_fk FOREIGN KEY (user_id) REFERENCES public.USERDATA(id)
);

CREATE TABLE public.article
(
    id BIGSERIAL PRIMARY KEY,
    updated timestamp without time zone,
    created timestamp without time zone,
    description character varying(255),
    price numeric(19,2),
    report_ressource character varying(255),
    title character varying(50) NOT NULL,
    user_id bigint NOT NULL,
    CONSTRAINT article_user_fk FOREIGN KEY (user_id) REFERENCES public.USERDATA(id)
);

CREATE TABLE public.banking_connection
(
    user_id bigint NOT NULL,
    bank_name character varying(150) NOT NULL,
    bic character varying(150),
    updated timestamp without time zone,
    created timestamp without time zone,
    iban character varying(150) NOT NULL,
    CONSTRAINT banking_connection_pkey PRIMARY KEY (user_id),
    CONSTRAINT banking_connection_user_fk FOREIGN KEY (user_id) REFERENCES public.USERDATA(id)
);

CREATE TABLE public.invoice
(
    id BIGSERIAL PRIMARY KEY,
    updated timestamp without time zone,
    created timestamp without time zone,
    invoice_date timestamp without time zone,
    invoiceid character varying(150) NOT NULL,
    report_ressource character varying(255),
    sign_image_path character varying(255),
    user_id bigint NOT NULL,
    CONSTRAINT invoice_user_fk FOREIGN KEY (user_id) REFERENCES public.USERDATA(id),
	CONSTRAINT invoiceid_unique UNIQUE (user_id, invoiceid)
);

CREATE TABLE public.invoice_item
(
    id BIGSERIAL PRIMARY KEY,
    description character varying(255) NULL,
    start_time timestamp without time zone,
    end_time timestamp without time zone,
    participants character varying(15),
    price_per_hour numeric(19,2),
    sum_price numeric(19,2),
    article_id bigint NOT NULL,
    invoice_id bigint NULL,
    created timestamp without time zone,
    updated timestamp without time zone,
    CONSTRAINT invoice_item_article_fk FOREIGN KEY (article_id) REFERENCES public.article(id),
    CONSTRAINT invoice_item_invoice_fk FOREIGN KEY (invoice_id) REFERENCES public.invoice(id)
);

ALTER TABLE IF EXISTS public.USERDATA
    OWNER TO trainer;
ALTER TABLE IF EXISTS public.adress
    OWNER TO trainer;
ALTER TABLE IF EXISTS public.article
    OWNER TO trainer;
ALTER TABLE IF EXISTS public.banking_connection
    OWNER TO trainer;
ALTER TABLE IF EXISTS public.invoice
    OWNER TO trainer;
ALTER TABLE IF EXISTS public.invoice_item
    OWNER TO trainer;

