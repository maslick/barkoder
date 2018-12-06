package io.maslick.barkoder.db

import io.maslick.barkoder.IService
import io.maslick.barkoder.Item
import io.maslick.barkoder.MyRepo
import io.maslick.barkoder.MyService
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.keycloak.adapters.springboot.KeycloakAutoConfiguration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringRunner


@RunWith(SpringRunner::class)
@DataJpaTest
@ContextConfiguration(classes = [MyTestDbConfig::class])
class DatabaseTest {

    @Autowired lateinit var em: TestEntityManager
    @Autowired lateinit var repo: MyRepo
    @Autowired lateinit var service: IService

    val testItem = Item(null, "title", "category", "description", "1234567890", 1)

    @Before
    fun bedore() {
        repo.deleteAll()
    }

    @Test
    fun addSingleItem() {
        val inserted = em.persistFlushFind(testItem)
        val found = service.getOneById(inserted.id!!)!!

        Assert.assertEquals(inserted.id, found.id)
        Assert.assertEquals("title", found.title)
        Assert.assertEquals("category", found.category)
        Assert.assertEquals("description", found.description)
        Assert.assertEquals("1234567890", found.barcode)
        Assert.assertEquals(1, found.quantity)
    }

    @Test
    fun findByBarcode() {
        val inserted = em.persistFlushFind(testItem)
        val found = service.getOneByBarcode(testItem.barcode!!)
        Assert.assertNotNull(found)
        Assert.assertEquals(inserted, found)
    }

    @Test
    fun updateItem() {
        val result = service.saveOne(testItem)
        Assert.assertTrue(result)

        val inserted = service.getOneByBarcode("1234567890")!!
        inserted.title = "edited"
        service.updateOne(inserted)

        Assert.assertEquals("edited", service.getOneByBarcode("1234567890")!!.title)
    }

    @Test
    fun deleteItem() {
        val inserted = em.persistFlushFind(testItem)
        service.deleteOne(inserted.id!!)
        Assert.assertNull(service.getOneById(inserted.id!!))
    }

    @Test
    fun deleteItemByBarcode() {
        val inserted = em.persistFlushFind(testItem)
        service.deleteOneByBarcode(inserted.barcode!!)
        Assert.assertNull(service.getOneById(inserted.id!!))
    }
}

@TestConfiguration
@ComponentScan
@EnableAutoConfiguration(exclude = [
    KeycloakAutoConfiguration::class,
    WebMvcAutoConfiguration::class
])
class MyTestDbConfig {
    @Bean
    fun service(repo: MyRepo): IService = MyService(repo)
}