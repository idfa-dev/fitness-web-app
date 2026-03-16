// Table schema for ClientTable

package com.example.database

import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.core.ReferenceOption

object Clients : IntIdTable("clients") {
    val user = reference("user_id", Users, ReferenceOption.CASCADE)
    val pt = reference("pt_id", PTs, ReferenceOption.CASCADE)
}