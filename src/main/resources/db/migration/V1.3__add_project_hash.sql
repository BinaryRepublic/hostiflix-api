CREATE TABLE project_hash (
  id varchar(3) NOT NULL,
  created_at timestamp with time zone NOT NULL,
  PRIMARY KEY (id)
);

INSERT INTO project_hash (id, created_at)
SELECT hash, created_at
FROM project;

ALTER TABLE project
ADD COLUMN project_hash_id varchar(3);

UPDATE project SET project_hash_id=hash;

ALTER TABLE project
ALTER COLUMN project_hash_id SET NOT NULL;

ALTER TABLE project
DROP COLUMN hash;

ALTER TABLE project
ADD CONSTRAINT fk_project_project_hash_id FOREIGN KEY (project_hash_id) REFERENCES project_hash(id);