/*
 *
 * MIT License
 *
 * Copyright (c) 2022 NSTDA
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package nstda.hii.webservice.app.database.dsl.test

import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

class DslFunction {
    fun insert(name: String): Long {
        return transaction {
            addLogger(org.jetbrains.exposed.sql.Slf4jSqlDebugLogger)
            SchemaUtils.create(TestDsl)

            val testInsert: EntityID<Long> = TestDsl.insert {
                it[this.name] = name
            } get TestDsl.id

            println("Id of insert ${testInsert.value}")
            testInsert.value
        }
    }

    fun queryById(id: Long): String {
        return transaction {
            TestDsl.select {
                TestDsl.id eq id
            }.map {
                TestDsl.getResult(it)
            }.first()
        }
    }

    fun deleteById(id: Long) {
        transaction {
            TestDsl.deleteWhere {
                TestDsl.id eq id
            }
        }
    }

    fun updateById(id: Long, name: String) {
        transaction {
            TestDsl.update({
                TestDsl.id eq id
            }) {
                it[this.name] = name
            }
        }
    }
}