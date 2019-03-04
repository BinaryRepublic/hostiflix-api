package com.hostiflix.repository

import com.hostiflix.entity.State
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface StateRepository : CrudRepository<State, String>