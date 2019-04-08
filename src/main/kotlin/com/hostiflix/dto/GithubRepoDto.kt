package com.hostiflix.dto

import com.fasterxml.jackson.annotation.JsonSetter

class GithubRepoDto {
    lateinit var id : String

    @JsonSetter("full_name")
    lateinit var fullName : String

    @JsonSetter("default_branch")
    lateinit var defaultBranch : String
}