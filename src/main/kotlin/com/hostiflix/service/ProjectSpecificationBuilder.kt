package com.hostiflix.service

import com.hostiflix.dto.ProjectFilterDto
import com.hostiflix.entity.Project
import org.springframework.data.jpa.domain.Specification
import java.util.ArrayList
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Root

class ProjectSpecificationBuilder {

    private val specifications = ArrayList<Specification<Project>>()

    fun withCustomerId(customerId: String): ProjectSpecificationBuilder {
        specifications.add(Specification { root, _, criteriaBuilder -> criteriaBuilder.equal(root.get<Any>("customerId"), customerId) })
        return this
    }

    fun withFilter(filter: ProjectFilterDto): ProjectSpecificationBuilder {
        if (filter.id.isNotEmpty()) {
            // SE_03 interface implementation - SAM
            specifications.add(Specification { root, _, _ ->
                root.get<Any>("id").`in`(filter.id)
            })

//            specifications.add(object : Specification<Project> {
//                override fun toPredicate(root: Root<Project>, query: CriteriaQuery<*>, criteriaBuilder: CriteriaBuilder): Predicate? {
//                    return root.get<Any>("id").`in`(filter.id)
//                }
//            })
        }
        if (filter.name.isNotEmpty()) {
            specifications.add(Specification { root, _, _ -> root.get<Any>("name").`in`(filter.name) })
        }

        return this
    }

    fun build(): Specification<Project> {
        val it = specifications.iterator()
        var result = it.next()
        while (it.hasNext()) {
            result = Specification.where(result).and(it.next())
        }
        return result
    }
}
