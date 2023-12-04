package com.ceassar.data.service

import com.ceassar.data.model.SourceEntity
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

class SourceService(private val database: Database) {
    object Source : Table() {
        val id = integer("id").autoIncrement()
        val name = varchar("name", 60)
        val url = varchar("url", 120)

        override val primaryKey = PrimaryKey(id)
    }


    private fun Query.mapSource() =
        map { SourceEntity(it[Source.name], it[Source.url]) }


    init {
        transaction(database) {
            SchemaUtils.create(Source)
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    suspend fun add(source: SourceEntity): Int {
        return dbQuery {
            Source.insert {
                it[name] = source.name
                it[url] = source.url
            }[Source.id]
        }
    }


    suspend fun update(id: Int, user: Source) {
        dbQuery {
            Source.update({ Source.id eq id }) {
                it[name] = user.name
                it[url] = user.url
            }
        }
    }


    suspend fun getAll() =
        dbQuery {
            Source.selectAll().map {
                "${it[Source.id]}    ${it[Source.name]}     ${it[Source.url]} \t "
            }
        }


}