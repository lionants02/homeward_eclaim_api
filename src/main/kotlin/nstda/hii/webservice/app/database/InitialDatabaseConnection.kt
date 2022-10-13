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

package nstda.hii.webservice.app.database

import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database

/**
 * ตั้งค่าการเชื่อมต่อ Database
 * หลังจากสร้าง class นี้ หากเรียกใช้งาน library expose https://github.com/JetBrains/Exposed
 * จะถูกให้เชื่อมต่อผ่านการตั้งค่านี้โดยอัตโนมัติ
 */
class InitialDatabaseConnection(connectionString: String, username: String, password: String) {
    private val dataSource = HikariDataSource()

    init {
        // ตั้งค่า database pool ด้วย library Hikari
        // https://github.com/brettwooldridge/HikariCP
        dataSource.jdbcUrl = connectionString
        dataSource.username = username
        dataSource.password = password
        dataSource.maximumPoolSize = 5
        dataSource.idleTimeout = 10000
        dataSource.maxLifetime = 30000
        // ตั้งการการเชื่อมต่อ library expose
        Database.connect(dataSource)
    }
    fun close() {
        dataSource.close()
    }
}
