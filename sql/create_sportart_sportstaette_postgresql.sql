
CREATE TABLE public.SPORTSTAETTE
(
    id BIGSERIAL PRIMARY KEY,
    updated timestamp without time zone,
    created timestamp without time zone,
    name character varying(50),
    user_id bigint NOT NULL,
    CONSTRAINT sportstaette_user_fk FOREIGN KEY (user_id) REFERENCES public.USERDATA(id)
);


CREATE TABLE public.SPORTART
(
    id BIGSERIAL PRIMARY KEY,
    updated timestamp without time zone,
    created timestamp without time zone,
    name character varying(50),
    user_id bigint NOT NULL,
    CONSTRAINT sportart_user_fk FOREIGN KEY (user_id) REFERENCES public.USERDATA(id)
);

ALTER TABLE public.invoice_item 
    ADD COLUMN sportart_id bigint NULL;
ALTER TABLE public.invoice_item 
    ADD COLUMN sportstaette_id bigint NULL;

ALTER TABLE IF EXISTS public.SPORTSTAETTE
    OWNER TO trainer;
ALTER TABLE IF EXISTS public.SPORTART
    OWNER TO trainer;
    