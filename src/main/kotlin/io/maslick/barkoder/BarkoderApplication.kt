package io.maslick.barkoder


import com.google.common.base.Predicates
import io.maslick.barkoder.Status.*
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE
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
data class Response(val item: Item?, val status: Status, val errorMessage: String? = null)

@RestController
@CrossOrigin
class BarcoderRestController(val service: IService) {

    @GetMapping(value = ["/items"], produces = [APPLICATION_JSON_UTF8_VALUE])
    fun getAllItems(): List<Item> {
        return service.getAll()
    }

    @GetMapping(value = ["/item/{id}"], produces = [APPLICATION_JSON_UTF8_VALUE])
    fun getItem(@PathVariable id: Int): Item? {
        return service.getOneById(id)
    }

    @GetMapping(value = ["/barcode/{barcode}"], produces = [APPLICATION_JSON_UTF8_VALUE])
    fun getItemByBarcode(@PathVariable barcode: String): Item? {
        return service.getOneByBarcode(barcode)
    }

    @PostMapping(value = ["/item"], produces = [APPLICATION_JSON_UTF8_VALUE])
    fun postItem(@RequestBody item: Item): Response {
        return try {
            if (service.saveOne(item)) Response(item, OK)
            else Response(item, ERROR, "item already exists or sth else happened :(")
        } catch (e: Exception) {
            e.printStackTrace()
            Response(item, ERROR, e.message)
        }
    }

    @PutMapping(value = ["/item"], produces = [APPLICATION_JSON_UTF8_VALUE])
    fun putItem(@RequestBody item: Item): Response {
        return if (service.updateOne(item)) Response(item, OK)
        else Response(item, ERROR, "Could not update Item :(")
    }

    @DeleteMapping(value = ["/item/{id}"], produces = [APPLICATION_JSON_UTF8_VALUE])
    fun deleteItem(@PathVariable id: Int): Response {
        return try {
            service.deleteOne(id)
            Response(null, OK)
        } catch (e: Exception) {
            e.printStackTrace()
            Response(null, ERROR, e.message)
        }
    }

    @DeleteMapping(value = ["/barcode/{barcode}"], produces = [APPLICATION_JSON_UTF8_VALUE])
    fun deleteItemByBarcode(@PathVariable barcode: String): Response {
        return try {
            service.deleteOneByBarcode(barcode)
            Response(null, OK)
        } catch (e: Exception) {
            e.printStackTrace()
            Response(null, ERROR, e.message)
        }
    }
}

interface IService {
    fun getAll(): List<Item>
    fun getOneById(id: Int): Item?
    fun getOneByBarcode(barcode: String): Item?
    fun saveOne(item: Item): Boolean
    fun updateOne(item: Item): Boolean
    fun deleteOne(id: Int)
    fun deleteOneByBarcode(barcode: String)
}

@Service
class MyService(val repo: MyRepo): IService {
    override fun getAll() = repo.findAll()
    override fun getOneById(id: Int) = repo.findById(id).orElse(null)
    override fun getOneByBarcode(barcode: String) = repo.findOneByBarcode(barcode)

    override fun saveOne(item: Item): Boolean {
        if (repo.findOneByBarcode(item.barcode!!) != null) return false
        repo.save(item)
        return true
    }

    override fun updateOne(item: Item): Boolean {
        return if (repo.existsById(item.id!!)) {
            repo.save(item)
            true
        }
        else false
    }

    override fun deleteOne(id: Int) {
        val item = repo.findById(id).orElse(null)
        if (item == null) throw RuntimeException("item not found!")
        else repo.delete(item)
    }

    override fun deleteOneByBarcode(barcode: String) {
        val item = repo.findOneByBarcode(barcode)
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