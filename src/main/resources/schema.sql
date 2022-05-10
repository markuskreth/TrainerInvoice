
CREATE TABLE USER (
  ID INT(11) NOT NULL,
  EMAIL VARCHAR(45) NOT NULL,
  GIVEN_NAME VARCHAR(45) NOT NULL,
  FAMILY_NAME VARCHAR(45) NOT NULL,
  PRINCIPAL_ID VARCHAR(45) NOT NULL,
  UPDATED TIMESTAMP NOT NULL,
  CREATED TIMESTAMP NOT NULL
);

CREATE TABLE ADRESS (
  ID INT(11) NOT NULL,
  ADRESS_TYPE VARCHAR(31) NOT NULL,
  ADRESS1 VARCHAR(255) DEFAULT NULL,
  ADRESS2 VARCHAR(255) DEFAULT NULL,
  ZIP VARCHAR(45) DEFAULT NULL,
  CITY VARCHAR(155) DEFAULT NULL,
  UPDATED TIMESTAMP NOT NULL,
  CREATED TIMESTAMP NOT NULL
);

CREATE TABLE ARTICLE (
  ID INT(11) NOT NULL,
  PRICE DOUBLE NOT NULL,
  TITLE VARCHAR(50) NOT NULL,
  DESCRIPTION VARCHAR(255) DEFAULT NULL,
  USER_ID INT(11) NOT NULL,
  REPORT VARCHAR(45) NOT NULL DEFAULT '/REPORTS/MTV_GROSS_BUCHHOLZ.JRXML',
  UPDATED TIMESTAMP NOT NULL,
  CREATED TIMESTAMP NOT NULL
);

CREATE TABLE BANKING_CONNECTION (
  ID INT(11) NOT NULL,
  OWNER_TYPE VARCHAR(31) NOT NULL,
  BANKNAME VARCHAR(255) DEFAULT NULL,
  BIC VARCHAR(255) DEFAULT NULL,
  IBAN VARCHAR(255) DEFAULT NULL,
  UPDATED TIMESTAMP NOT NULL,
  CREATED TIMESTAMP NOT NULL
);

CREATE TABLE INVOICE (
  ID INT(11) NOT NULL,
  INVOICE_DATE DATETIME NOT NULL,
  INVOICE_ID VARCHAR(150) NOT NULL,
  USER_ID INT(11) NOT NULL,
  SIGN_IMAGE_PATH VARCHAR(255) DEFAULT NULL,
  UPDATED TIMESTAMP NOT NULL,
  CREATED TIMESTAMP NOT NULL
);

CREATE TABLE INVOICE_ITEM (
  ID INT(11) NOT NULL,
  START DATETIME NOT NULL,
  END VARCHAR(45) NOT NULL,
  ARTICLE_ID INT(11) NOT NULL,
  PARTICIPANTS VARCHAR(15) DEFAULT NULL,
  SUM_PRICE DECIMAL(7,2) NOT NULL,
  RECHNUNG_ID INT(11) DEFAULT NULL,
  INVOICE_ID INT(11) DEFAULT NULL,
  UPDATED TIMESTAMP NOT NULL,
  CREATED TIMESTAMP NOT NULL,
  TITLE VARCHAR(100) NOT NULL,
  DESCRIPTION VARCHAR(255) DEFAULT NULL,
  USER_ID INT(11) NOT NULL,
  PRICEPERHOUR DECIMAL(7,2) NOT NULL,
  REPORT VARCHAR(255) DEFAULT NULL
);

