package io.maslick.barkoder


import org.springframework.data.jpa.repository.JpaRepository
import javax.persistence.*


@Entity
@Table(name = "items")
data class Item(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Int? = null,
        var title: String? = null,
        var category: String? = null,
        var description: String? = null,
        var barcode: String? = null,
        var quantity: Int? = null
)


interface MyRepo : JpaRepository<Item, Int> {
    fun findByBarcode(barcode: String): List<Item>
}