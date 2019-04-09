package com.hostiflix.repository

import com.hostiflix.entity.ProjectHash
import org.springframework.data.repository.CrudRepository

interface ProjectHashRepository: CrudRepository<ProjectHash, String>