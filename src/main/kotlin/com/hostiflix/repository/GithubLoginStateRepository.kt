package com.hostiflix.repository

import com.hostiflix.entity.GithubLoginState
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface GithubLoginStateRepository : CrudRepository<GithubLoginState, String>