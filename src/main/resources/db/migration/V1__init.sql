CREATE TABLE customer (
  id varchar(36) NOT NULL,
  first_name varchar(255),
  last_name varchar(255),
  email varchar(255),
  github_username varchar(255),
  github_id varchar(255),
  PRIMARY KEY (id)
);

CREATE TABLE project (
  id varchar(255) NOT NULL,
  customer_id varchar(36) NOT NULL,
  name varchar(255),
  repository varchar(255),
  project_type varchar(255),
  location varchar(255),
  PRIMARY KEY (id),
  CONSTRAINT fk_project_customer_id FOREIGN KEY (customer_id) REFERENCES customer(id)
);

CREATE TABLE branch (
  id varchar(255) NOT NULL,
  project_id varchar(255) NOT NULL,
  name varchar(255),
  PRIMARY KEY (id),
  CONSTRAINT fk_branch_project_id FOREIGN KEY (project_id) REFERENCES project(id)
);