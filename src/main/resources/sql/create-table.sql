/*CREATE SCHEMA IF NOT EXISTS TESTDB;*/
/*SET SCHEMA TESTDB;*/


CREATE TABLE IF NOT EXISTS SPRING_SESSION (
	PRIMARY_ID CHAR(36) NOT NULL,
	SESSION_ID CHAR(36) NOT NULL,
	CREATION_TIME BIGINT NOT NULL,
	LAST_ACCESS_TIME BIGINT NOT NULL,
	MAX_INACTIVE_INTERVAL INT NOT NULL,
	EXPIRY_TIME BIGINT NOT NULL,
	PRINCIPAL_NAME VARCHAR(100),
	CONSTRAINT SPRING_SESSION_PK PRIMARY KEY (PRIMARY_ID)
);

CREATE UNIQUE INDEX IF NOT EXISTS SPRING_SESSION_IX1 ON SPRING_SESSION (SESSION_ID);
CREATE INDEX IF NOT EXISTS SPRING_SESSION_IX2 ON SPRING_SESSION (EXPIRY_TIME);
CREATE INDEX IF NOT EXISTS SPRING_SESSION_IX3 ON SPRING_SESSION (PRINCIPAL_NAME);

CREATE TABLE IF NOT EXISTS SPRING_SESSION_ATTRIBUTES (
	SESSION_PRIMARY_ID CHAR(36) NOT NULL,
	ATTRIBUTE_NAME VARCHAR(200) NOT NULL,
	ATTRIBUTE_BYTES BLOB NOT NULL,
	CONSTRAINT SPRING_SESSION_ATTRIBUTES_PK PRIMARY KEY (SESSION_PRIMARY_ID, ATTRIBUTE_NAME),
	CONSTRAINT SPRING_SESSION_ATTRIBUTES_FK FOREIGN KEY (SESSION_PRIMARY_ID) REFERENCES SPRING_SESSION(PRIMARY_ID) ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS PRODUCT (
  product_id INT AUTO_INCREMENT,
  product_name VARCHAR(250) NOT NULL,
  price DECIMAL NOT NULL,
  quantity INT NOT NULL,
  description VARCHAR(500) NOT NULL,
  image VARCHAR(250) NOT NULL,
  CONSTRAINT PRODUCT_PK PRIMARY KEY (product_name)
);

CREATE TABLE IF NOT EXISTS ORDER_CONFIRMATION (
    order_id INT AUTO_INCREMENT,
    cart_list BLOB NOT NULL,
    bill DECIMAL NOT NULL,
    created_ts DATE NOT NULL
);


