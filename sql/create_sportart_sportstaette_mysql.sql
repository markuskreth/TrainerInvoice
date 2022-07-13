
CREATE TABLE SPORTSTAETTE
(
    id int NOT NULL AUTO_INCREMENT,
    updated timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
    created timestamp NOT NULL DEFAULT current_timestamp(),
    name varchar(50),
    user_id int NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT sportstaette_user_fk FOREIGN KEY (user_id) REFERENCES USERDATA(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE SPORTART
(
    id int NOT NULL AUTO_INCREMENT,
    updated timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
    created timestamp NOT NULL DEFAULT current_timestamp(),
    name varchar(50),
    user_id int NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT sportart_user_fk FOREIGN KEY (user_id) REFERENCES USERDATA(id) ON DELETE CASCADE ON UPDATE CASCADE
);

ALTER TABLE invoice_item 
    ADD COLUMN sportart_id int(11) NULL;
ALTER TABLE invoice_item 
    ADD COLUMN sportstaette_id int(11) NULL;
