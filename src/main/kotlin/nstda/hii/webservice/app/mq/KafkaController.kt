/*
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

package nstda.hii.webservice.app.mq

import java.util.Properties
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.RecordMetadata
import java.util.concurrent.Future

/**
 * ตัวส่งข้อมูลเข้า Kafka
 * แนะนำให้สร้างเป็นแบบ static เพราะว่า ตัวเชื่อมสามารถใช้ด้วยกันได้
 */
class KafkaController<K, V>(private val config: Properties) : ProducerMessageQueue<K, V> {
    private var producer: Producer<K, V>? = null
    override fun send(topic: String, key: K, value: V) {
        val record: ProducerRecord<K, V> = ProducerRecord(topic, key, value)
        val future = producerSend(record)
        val metadata: RecordMetadata = future.get()
    }

    override fun close() {
        producer?.close()
    }

    private fun producerSend(record: ProducerRecord<K, V>): Future<RecordMetadata> {
        var count = 0
        var retry = true
        var output: Future<RecordMetadata>? = null
        while (retry) {
            count++
            try {
                output = producer!!.send(record)
                retry = false
            } catch (ex: Exception) {
                if (count >= 3)
                    throw ex
                close()
                producer = KafkaProducer<K, V>(config)
            }
        }
        return output!!
    }

    companion object {

    }
}