package com.hostiflix.repository

import com.hostiflix.entity.Project
import com.hostiflix.entity.ProjectHash
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectRepository : CrudRepository<Project, String> {

    fun findAllByCustomerId(customerId: String): List<Project>

    fun findByIdAndCustomerId(id: String, customerId: String): Project?

    fun existsByIdAndCustomerId(id: String, customerId: String): Boolean

    fun existsByProjectHash(projectHash: ProjectHash): Boolean

    fun findByRepositoryOwnerAndRepositoryName(repositoryOwner: String, repositoryName: String) : Project?
}