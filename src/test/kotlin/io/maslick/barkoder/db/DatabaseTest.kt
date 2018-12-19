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

    val testItem1 = Item(null, "title1", "category1", "description1", "1234567890", 1)
    val testItem2 = Item(null, "title2", "category2", "description2", "0987654321", 2)
    val testItem3 = Item(null, "title3", "category3", "description3", "0000000000", 3)

    @Before
    fun before() {
        repo.deleteAll()
        repo.flush()
    }

    @Test
    fun addSingleItem() {
        val inserted = em.persistFlushFind(testItem1)
        val found = service.getOneById(inserted.id!!)!!

        Assert.assertEquals(inserted.id, found.id)
        Assert.assertEquals(testItem1.title, found.title)
        Assert.assertEquals(testItem1.category, found.category)
        Assert.assertEquals(testItem1.description, found.description)
        Assert.assertEquals(testItem1.barcode, found.barcode)
        Assert.assertEquals(testItem1.quantity, found.quantity)
    }

    @Test
    fun addSameTwoItems() {
        val saved1 = service.saveOne(testItem1)
        Assert.assertTrue(saved1)
        val saved2 = service.saveOne(testItem1)
        Assert.assertFalse(saved2)
    }

    @Test
    fun addItemWithNoBarcode() {
        val saved = service.saveOne(Item())
        Assert.assertFalse(saved)
    }

    @Test
    fun addMultipleItems() {
        val inserted = service.saveMultiple(listOf(testItem1, testItem2))
        Assert.assertTrue(inserted)

        val found1 = service.getOneByBarcode(testItem1.barcode!!)!!
        val found2 = service.getOneByBarcode(testItem2.barcode!!)!!

        Assert.assertEquals(testItem1.title, found1.title)
        Assert.assertEquals(testItem1.category, found1.category)
        Assert.assertEquals(testItem1.description, found1.description)
        Assert.assertEquals(testItem1.barcode, found1.barcode)
        Assert.assertEquals(testItem1.quantity, found1.quantity)

        Assert.assertEquals(testItem2.title, found2.title)
        Assert.assertEquals(testItem2.category, found2.category)
        Assert.assertEquals(testItem2.description, found2.description)
        Assert.assertEquals(testItem2.barcode, found2.barcode)
        Assert.assertEquals(testItem2.quantity, found2.quantity)
    }

    @Test
    fun addSameTwoMultipleItems() {
        val inserted1 = service.saveMultiple(listOf(testItem2, testItem3))
        Assert.assertTrue(inserted1)
        val inserted2 = service.saveMultiple(listOf(testItem1, testItem3))
        Assert.assertFalse(inserted2)
    }

    @Test
    fun addMultipleItemsWithNoBarcode() {
        val saved = service.saveMultiple(listOf(testItem1, testItem2, testItem3, Item()))
        Assert.assertFalse(saved)
    }

    @Test
    fun findByBarcode() {
        val inserted = em.persistFlushFind(testItem1)
        val found = service.getOneByBarcode(testItem1.barcode!!)
        Assert.assertNotNull(found)
        Assert.assertEquals(inserted, found)
    }

    @Test
    fun updateItem() {
        val result = service.saveOne(testItem1)
        Assert.assertTrue(result)

        val inserted = service.getOneByBarcode("1234567890")!!
        inserted.title = "edited"
        Assert.assertTrue(service.updateOne(inserted))
        Assert.assertEquals("edited", service.getOneByBarcode("1234567890")!!.title)
    }

    @Test
    fun updateItemWithExistingBarcode() {
        service.saveOne(Item(title = "1", barcode = "1"))
        service.saveOne(Item(title = "2", barcode = "2"))

        Assert.assertFalse(service.updateOne(Item(title = "2", barcode = "1")))
        Assert.assertEquals(1, repo.findByBarcode("1").size)
        Assert.assertEquals(1, repo.findByBarcode("2").size)
    }

    @Test
    fun deleteItem() {
        val inserted = em.persistFlushFind(testItem1)
        service.deleteOne(inserted.id!!)
        Assert.assertNull(service.getOneById(inserted.id!!))
    }

    @Test
    fun deleteItemByBarcode() {
        val inserted = em.persistFlushFind(testItem1)
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