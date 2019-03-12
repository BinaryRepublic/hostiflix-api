package com.hostiflix.services

import com.hostiflix.entity.Project
import org.springframework.stereotype.Service

@Service
class ProjectService {

    fun assignProjectToAllBranches(newProject : Project) {
        newProject.branches.forEach { it.project = newProject }
    }

}