DROP DATABASE IF EXISTS book_store;
CREATE DATABASE book_store;
USE book_store;
/*
 * a simple user table
 * id: user id
 * username: username to log in
 * password: hashed password
 */
DROP TABLE IF EXISTS User;
CREATE TABLE User (
  id        INT UNSIGNED                                     NOT NULL AUTO_INCREMENT,
  username  VARCHAR(50)                                      NOT NULL UNIQUE,
  password  CHAR(50)                                         NOT NULL,
  lname     VARCHAR(20)                                      NOT NULL,
  fname     VARCHAR(20)                                      NOT NULL,
  user_type ENUM ('Visitor', 'Customer', 'Partner', 'Admin') NOT NULL DEFAULT 'Visitor',
  PRIMARY KEY (id),
  INDEX (username)
);

/** bid: unique identifier of Book (like ISBN)
* title: title of Book
* price: unit price WHEN ordered
* author: name of authors
* category: as specified
*/
DROP TABLE IF EXISTS Book;
CREATE TABLE Book (
  bid         VARCHAR(20)                                NOT NULL,
  title       VARCHAR(60)                                NOT NULL,
  price       INT                                        NOT NULL,
  category    ENUM ('Science', 'Fiction', 'Engineering') NOT NULL,
  description VARCHAR(255)                               NOT NULL,
  rating      FLOAT                                      NOT NULL DEFAULT 0.0,
  PRIMARY KEY (bid)
);


INSERT INTO Book (bid, title, price, category, description)
VALUES ('b001', 'Little Prince', 20, 'Fiction', 'A fiction story about a little prince.');
INSERT INTO Book (bid, title, price, category, description)
VALUES ('b002', 'Physics', 201, 'Science', 'Introduction to Physics. You will learn the basic Physics knowledge');
INSERT INTO Book (bid, title, price, category, description)
VALUES ('b003', 'Mechanics', 100, 'Engineering', 'Introduction to Mechanics.');
INSERT INTO Book (bid, title, price, category, description)
VALUES ('b004', 'Chemistry', 80, 'Science', 'Learn Chemistry from beginning.');

/* Address
* id: address id
*
*/
DROP TABLE IF EXISTS Address;
CREATE TABLE Address (
  id       INT UNSIGNED NOT NULL AUTO_INCREMENT,
  street   VARCHAR(100) NOT NULL,
  province VARCHAR(20)  NOT NULL,
  country  VARCHAR(20)  NOT NULL,
  zip      VARCHAR(20)  NOT NULL,
  phone    VARCHAR(20),
  PRIMARY KEY (id)
);


INSERT INTO Address (id, street, province, country, zip, phone) VALUES (1, '123 Yonge St', 'ON',
                                                                        'Canada', 'K1E 6T5', '647-123-4567');
INSERT INTO Address (id, street, province, country, zip, phone) VALUES (2, '445 Avenue rd', 'ON',
                                                                        'Canada', 'M1C 6K5', '416-123-8569');
INSERT INTO Address (id, street, province, country, zip, phone) VALUES (3, '789 Keele St.', 'ON',
                                                                        'Canada', 'K3C 9T5', '416-123-9568');


/* Purchase Order
* lname: last name
* fname: first name
* id: purchase order id
* status: status of purchase
*/
DROP TABLE IF EXISTS PO;
CREATE TABLE PO (
  id      INT UNSIGNED                            NOT NULL AUTO_INCREMENT,
  status  ENUM ('ORDERED', 'PROCESSED', 'DENIED') NOT NULL,
  address INT UNSIGNED                            NOT NULL,
  uid     INT UNSIGNED                            NOT NULL,
  PRIMARY KEY (id),
  INDEX (address),
  INDEX (uid),
  FOREIGN KEY (address) REFERENCES Address (id)
    ON DELETE CASCADE,
  FOREIGN KEY (uid) REFERENCES User (id)
    ON DELETE CASCADE
);


/* Items on order
* id : purchase order id
* bid: unique identifier of Book
* price: unit price
*/
DROP TABLE IF EXISTS POItem;
CREATE TABLE POItem (
  id       INT UNSIGNED NOT NULL,
  bid      VARCHAR(20)  NOT NULL,
  price    INT UNSIGNED NOT NULL,
  quantity INT UNSIGNED NOT NULL DEFAULT 1,
  rating   INT UNSIGNED NOT NULL DEFAULT 0,
  review   VARCHAR(255) NOT NULL DEFAULT "",
  PRIMARY KEY (id, bid),
  INDEX (id),
  FOREIGN KEY (id) REFERENCES PO (id)
    ON DELETE CASCADE,
  INDEX (bid),
  FOREIGN KEY (bid) REFERENCES Book (bid)
    ON DELETE CASCADE
);


/* visit to website
* day: date
* bid: unique identifier of Book
* eventtype: status of purchase
*/
DROP TABLE IF EXISTS VisitEvent;
CREATE TABLE VisitEvent (
  day       VARCHAR(8)                        NOT NULL,
  bid       VARCHAR(20)                       NOT NULL REFERENCES Book.bid,
  eventtype ENUM ('VIEW', 'CART', 'PURCHASE') NOT NULL,
  FOREIGN KEY (bid) REFERENCES Book (bid)
);

INSERT INTO VisitEvent (day, bid, eventtype) VALUES ('12202015', 'b001', 'VIEW');
INSERT INTO VisitEvent (day, bid, eventtype) VALUES ('12242015', 'b001', 'CART');
INSERT INTO VisitEvent (day, bid, eventtype) VALUES ('12252015', 'b001', 'PURCHASE');




