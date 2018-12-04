package io.maslick.barkoder


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
data class Resp(val check: Item?, val status: Status)

@RestController
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
    fun postItem(@RequestBody item: Item): Resp {
        return try {
            service.saveOne(item)
            Resp(item, OK)
        } catch (e: Exception) {
            e.printStackTrace()
            Resp(item, ERROR)
        }
    }

    @PutMapping(value = ["/item"], produces = [APPLICATION_JSON_UTF8_VALUE])
    fun putItem(@RequestBody item: Item): Resp {
        return if (service.updateOne(item))
            Resp(item, OK)
        else Resp(item, ERROR)
    }
}

interface IService {
    fun getAll(): List<Item>
    fun getOneById(id: Int): Item?
    fun getOneByBarcode(barcode: String): Item?
    fun saveOne(item: Item)
    fun updateOne(item: Item): Boolean
}

@Service
class MyService(val repo: MyRepo): IService {
    override fun getAll() = repo.findAll()
    override fun getOneById(id: Int) = repo.findById(id).orElse(null)
    override fun getOneByBarcode(barcode: String) = repo.findByBarcode(barcode)
    override fun saveOne(item: Item) { repo.save(item) }
    override fun updateOne(item: Item): Boolean {
        if (repo.existsById(item.id!!)) {
            repo.save(item)
            return true
        }
        return false
    }
}

@Configuration
@EnableSwagger2
class SwaggerConfig {
    @Bean
    fun api() = Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build()
}