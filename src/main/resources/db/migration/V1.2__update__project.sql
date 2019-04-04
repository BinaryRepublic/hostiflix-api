ALTER TABLE project
ADD COLUMN hash varchar(255) NOT NULL,
ADD COLUMN start_code varchar(255),
ADD COLUMN build_code varchar(255),
ADD COLUMN repository_owner varchar(255),
ADD COLUMN repository_name varchar(255),
ADD COLUMN created_at timestamp with time zone NOT NULL;

ALTER TABLE project
RENAME COLUMN repository TO repository_id;

ALTER TABLE project
RENAME COLUMN project_type TO type;