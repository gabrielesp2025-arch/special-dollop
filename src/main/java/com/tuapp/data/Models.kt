@Serializable
enum class PhotoStage { BEFORE, DURING, AFTER }

@Serializable
data class PhotoRef(
    val id: Long = 0,
    val stage: PhotoStage,
    val path: String
@Serializable
data class Order(
    ...
    val photos: List<PhotoRef> = emptyList()
)

