package com.hostiflix.repository

import com.hostiflix.entity.Job
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface JobRepository: CrudRepository<Job, String>