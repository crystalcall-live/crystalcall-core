import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import models.Users
import org.jetbrains.exposed.dao.id.EntityID

fun Application.configureSerialisation() {
    install(ContentNegotiation) {
        json()
    }
}

// Custom serialisers
object EntityIDSerialiser : KSerializer<EntityID<Int>> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("EntityID<Int>", PrimitiveKind.INT)
    override fun serialize(encoder: Encoder, value: EntityID<Int>) = encoder.encodeInt(value.toString().toInt())

    override fun deserialize(decoder: Decoder) = EntityID(decoder.decodeInt(), Users)
}

