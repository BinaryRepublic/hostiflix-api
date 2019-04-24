package com.hostiflix.repository

import com.hostiflix.entity.Project
import com.hostiflix.entity.ProjectHash
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectRepository : CrudRepository<Project, String> {

    // SELECT * FROM project WHERE customer_id=customerI;
    fun findAllByCustomerId(customerId: String): List<Project>

    // SELECT * FROM project WHERE id=id AND customer_i =customerId;
    fun findByIdAndCustomerId(id: String, customerId: String): Project?

    // SELECT CASE WHEN EXISTS ( SELECT * FROM project WHERE id=id AND customer_id=customerID) THEN CAST(1 AS BIT) ELSE CAST(0 AS BIT)END;
    fun existsByIdAndCustomerId(id: String, customerId: String): Boolean

    fun existsByProjectHash(projectHash: ProjectHash): Boolean

    // SELECT * FROM project WHERE repository_owner=repositoryOwner AND repository_name=repositoryName;
    fun findByRepositoryOwnerAndRepositoryName(repositoryOwner: String, repositoryName: String) : Project?
}