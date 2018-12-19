package io.maslick.barkoder


import com.google.common.base.Predicates
import io.maslick.barkoder.Status.ERROR
import io.maslick.barkoder.Status.OK
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.*
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2


@SpringBootApplication
class BarkoderApplication

fun main(args: Array<String>) {
    runApplication<BarkoderApplication>(*args)
}

enum class Status { ERROR, OK }
data class Response(val status: Status, val errorMessage: String? = null)

@RestController
@CrossOrigin
class BarcoderRestController(val service: IService) {

    @GetMapping("/items")
    fun getAllItems(): List<Item> {
        return service.getAll()
    }

    @GetMapping("/item/{id}")
    fun getItem(@PathVariable id: Int): Item? {
        return service.getOneById(id)
    }

    @GetMapping("/barcode/{barcode}")
    fun getItemByBarcode(@PathVariable barcode: String): Item? {
        return service.getOneByBarcode(barcode)
    }

    @PostMapping("/item")
    fun postItem(@RequestBody item: Item): Response {
        return try {
            if (service.saveOne(item)) Response(OK)
            else Response(ERROR, "item already exists or sth else happened :(")
        } catch (e: Exception) {
            e.printStackTrace()
            Response(ERROR, e.message)
        }
    }

    @PostMapping("/items")
    fun postMultipleItems(@RequestBody items: List<Item>): Response {
        return try {
            if (service.saveMultiple(items)) Response(OK)
            else Response(ERROR, "one of the items already exists")
        } catch (e: Exception) {
            e.printStackTrace()
            Response(ERROR, e.message)
        }
    }

    @PutMapping("/item")
    fun putItem(@RequestBody item: Item): Response {
        return if (service.updateOne(item)) Response(OK)
        else Response(ERROR, "Could not update Item :(")
    }

    @DeleteMapping("/item/{id}")
    fun deleteItem(@PathVariable id: Int): Response {
        return try {
            service.deleteOne(id)
            Response(OK)
        } catch (e: Exception) {
            e.printStackTrace()
            Response(ERROR, e.message)
        }
    }

    @DeleteMapping("/barcode/{barcode}")
    fun deleteItemByBarcode(@PathVariable barcode: String): Response {
        return try {
            service.deleteOneByBarcode(barcode)
            Response(OK)
        } catch (e: Exception) {
            e.printStackTrace()
            Response(ERROR, e.message)
        }
    }
}

interface IService {
    fun getAll(): List<Item>
    fun getOneById(id: Int): Item?
    fun getOneByBarcode(barcode: String): Item?
    fun saveOne(item: Item): Boolean
    fun saveMultiple(items: List<Item>): Boolean
    fun updateOne(item: Item): Boolean
    fun deleteOne(id: Int)
    fun deleteOneByBarcode(barcode: String)
}

@Service
class MyService(val repo: MyRepo): IService {
    override fun getAll() = repo.findAll()
    override fun getOneById(id: Int) = repo.findById(id).orElse(null)
    override fun getOneByBarcode(barcode: String) = repo.findByBarcode(barcode).firstOrNull()

    override fun saveOne(item: Item): Boolean {
        if (item.barcode == null) return false
        if (repo.findByBarcode(item.barcode!!).isNotEmpty()) return false
        repo.save(item)
        return true
    }

    override fun saveMultiple(items: List<Item>): Boolean {
        if (items.any { it.barcode == null }) return false
        if (items.any { repo.findByBarcode(it.barcode!!).isNotEmpty() }) return false
        repo.saveAll(items)
        return true
    }

    override fun updateOne(item: Item): Boolean {
        if (item.id == null || item.barcode == null) return false
        if (!repo.existsById(item.id!!)) return false
        val itemsWithThisBarcode = repo.findByBarcode(item.barcode!!)
        if (itemsWithThisBarcode.isEmpty() || itemsWithThisBarcode.size > 1) return false
        repo.save(item)
        return true
    }

    override fun deleteOne(id: Int) {
        val item = repo.findById(id).orElse(null)
        if (item == null) throw RuntimeException("item not found!")
        else repo.delete(item)
    }

    override fun deleteOneByBarcode(barcode: String) {
        val item = repo.findByBarcode(barcode).firstOrNull()
        if (item == null) throw RuntimeException("item not found!")
        else repo.delete(item)
    }
}

@Configuration
@EnableSwagger2
class SwaggerConfig {
    @Bean
    fun api() = Docket(DocumentationType.SWAGGER_2)
            .select()
            .apis(RequestHandlerSelectors.any())
            .paths(Predicates.not(PathSelectors.regex("/errorMessage")))
            .build()
}