package com.hostiflix.repository

import com.hostiflix.entity.Project
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectRepository : CrudRepository<Project, String> {

    fun findByCustomerId(customerId : String): List<Project>
}