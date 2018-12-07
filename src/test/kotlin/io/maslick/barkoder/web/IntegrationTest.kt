package io.maslick.barkoder.web

import io.maslick.barkoder.*
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.keycloak.adapters.springboot.KeycloakAutoConfiguration
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.context.annotation.ComponentScan
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringRunner
import kotlin.reflect.KClass


@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ContextConfiguration(classes = [MyTestConfig::class])
class IntegrationTest {

    @LocalServerPort private var port: Int? = null
    @MockBean lateinit var repo: MyRepo
    @MockBean lateinit var service: IService

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    val host = "http://localhost"

    @Test
    fun testGetAll() {
        kogda(service.getAll()).thenReturn(listOf(Item(id = 1), Item(id = 2)))

        val items = restTemplate.exchange(
                "http://localhost:$port/items",
                HttpMethod.GET,
                null,
                object : ParameterizedTypeReference<List<Item>>() {}
        ).body!!
        Assert.assertEquals(2, items.size)
        Assert.assertEquals(1, items.first().id)
        Assert.assertEquals(2, items.last().id)
    }

    @Test
    fun testGetOne() {
        kogda(service.getOneById(any())).thenReturn(Item(id=123))
        val item = restTemplate.getForEntity("$host:$port/item/123", Item::class.java).body!!
        Assert.assertEquals(123, item.id)
    }

    @Test
    fun testGetNull() {
        kogda(service.getOneById(any())).thenReturn(null)

        val item = restTemplate.getForEntity("$host:$port/item/123", Item::class.java).body
        Assert.assertNull(item)
    }

    @Test
    fun testGetBarcode() {
        kogda(service.getOneByBarcode(any())).thenReturn(Item(id=456, barcode = "123456"))
        val item = restTemplate.getForEntity("$host:$port/barcode/123456", Item::class.java).body!!
        Assert.assertEquals(456, item.id)
        Assert.assertEquals("123456", item.barcode)
    }

    @Test
    fun testGetNullBarcode() {
        kogda(service.getOneByBarcode(any())).thenReturn(null)
        val item = restTemplate.getForEntity("$host:$port/barcode/123456", Item::class.java).body
        Assert.assertNull(item)
    }

    @Test
    fun testPostOk() {
        kogda(service.saveOne(any())).thenReturn(true)
        val resp = restTemplate.postForEntity("$host:$port/item", Item(), Response::class.java).body!!
        Assert.assertEquals(Status.OK, resp.status)
        Assert.assertNull(resp.errorMessage)
    }

    @Test
    fun testPostAlreadyExists() {
        kogda(service.saveOne(any())).thenReturn(false)
        val resp = restTemplate.postForEntity("$host:$port/item", Item(), Response::class.java).body!!
        Assert.assertEquals(Status.ERROR, resp.status)
        Assert.assertNotNull(resp.errorMessage)
    }

    @Test
    fun testPostMultipleOk() {
        kogda(service.saveMultiple(any())).thenReturn(true)
        val resp = restTemplate.postForEntity("$host:$port/items", listOf<Item>(), Response::class.java).body!!
        Assert.assertEquals(Status.OK, resp.status)
        Assert.assertNull(resp.errorMessage)
    }

    @Test
    fun testPostMultipleAlreadyExists() {
        kogda(service.saveMultiple(any())).thenReturn(false)
        val resp = restTemplate.postForEntity("$host:$port/items", listOf<Item>(), Response::class.java).body!!
        Assert.assertEquals(Status.ERROR, resp.status)
        Assert.assertNotNull(resp.errorMessage)
    }

    @Test
    fun testPostWithException() {
        kogda(service.saveOne(any())).then {
            println("saving to database...")
            throw RuntimeException("database is down!!!")
        }
        val resp = restTemplate.postForEntity("$host:$port/item", Item(), Response::class.java).body!!
        Assert.assertEquals(Status.ERROR, resp.status)
        Assert.assertNotNull(resp.errorMessage)
    }

    @Test
    fun testUpdateItemOk() {
        kogda(service.updateOne(any())).thenReturn(true)
        val resp = restTemplate.exchange(
                "$host:$port/item",
                HttpMethod.PUT,
                HttpEntity(Item(123)),
                object : ParameterizedTypeReference<Response>() {}
        ).body!!
        Assert.assertEquals(Status.OK, resp.status)
        Assert.assertNull(resp.errorMessage)
    }

    @Test
    fun testUpdateItemError() {
        kogda(service.updateOne(any())).thenReturn(false)
        val resp = restTemplate.exchange(
                "$host:$port/item",
                HttpMethod.PUT,
                HttpEntity(Item(123)),
                object : ParameterizedTypeReference<Response>() {}
        ).body!!
        Assert.assertEquals(Status.ERROR, resp.status)
        Assert.assertNotNull(resp.errorMessage)
    }

    @Test
    fun testDeleteItemOk() {
        kogda(service.deleteOne(any())).then { println("deleting item...") }
        val resp = restTemplate.exchange(
                "$host:$port/item/123",
                HttpMethod.DELETE,
                null,
                object : ParameterizedTypeReference<Response>() {}
        )
        Assert.assertEquals(HttpStatus.OK, resp.statusCode)
        Assert.assertEquals(Status.OK, resp.body!!.status)
        Assert.assertNull(resp.body!!.errorMessage)
    }

    @Test
    fun testDeleteItemNotInDb() {
        kogda(service.deleteOne(any())).then {
            println("deleting item...")
            throw RuntimeException("Error removing item :(")
        }
        val resp = restTemplate.exchange(
                "$host:$port/item/123",
                HttpMethod.DELETE,
                null,
                object : ParameterizedTypeReference<Response>() {}
        )
        Assert.assertEquals(HttpStatus.OK, resp.statusCode)
        Assert.assertEquals(Status.ERROR, resp.body!!.status)
        Assert.assertNotNull(resp.body!!.errorMessage)
    }

    @Test
    fun testDeleteItemByBarcodeOk() {
        kogda(service.deleteOneByBarcode(any())).then { println("deleting item by barcode...") }
        val resp = restTemplate.exchange(
                "$host:$port/barcode/123",
                HttpMethod.DELETE,
                null,
                object : ParameterizedTypeReference<Response>() {}
        )
        Assert.assertEquals(HttpStatus.OK, resp.statusCode)
        Assert.assertEquals(Status.OK, resp.body!!.status)
        Assert.assertNull(resp.body!!.errorMessage)
    }

    @Test
    fun deleteItemByBarcodeNotInDb() {
        kogda(service.deleteOneByBarcode(any())).then {
            println("deleting item by barcode...")
            throw RuntimeException("Error removing item :(")
        }
        val resp = restTemplate.exchange(
                "$host:$port/barcode/123",
                HttpMethod.DELETE,
                null,
                object : ParameterizedTypeReference<Response>() {}
        )
        Assert.assertEquals(HttpStatus.OK, resp.statusCode)
        Assert.assertEquals(Status.ERROR, resp.body!!.status)
        Assert.assertNotNull(resp.body!!.errorMessage)
    }

}

// Helper methods
fun <R> kogda(methodCall: R) = Mockito.`when`(methodCall)
inline fun <reified T : Any> any(): T = Mockito.any(T::class.java) ?: createInstance(T::class)
fun <T : Any> createInstance(kClass: KClass<T>): T = castNull()
@Suppress("UNCHECKED_CAST") private fun <T> castNull(): T = null as T


@TestConfiguration
@ComponentScan
@EnableAutoConfiguration(exclude = [DataSourceAutoConfiguration::class, KeycloakAutoConfiguration::class])
class MyTestConfig


