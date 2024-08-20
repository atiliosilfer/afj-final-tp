package puc.user

import org.springframework.web.bind.annotation.*
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import puc.util.JwtUtil

@RestController
@RequestMapping("/purchase")
class PurchaseController(val rabbitTemplate: RabbitTemplate, val jwtUtil: JwtUtil) {

    @Value("\${rabbitmq.exchange}")
    lateinit var exchange: String

    @Value("\${rabbitmq.routingkey}")
    lateinit var routingKey: String

    @PostMapping("/buy")
    fun buy(@RequestHeader("Authorization") token: String, @RequestBody purchaseRequest: PurchaseRequest): ResponseEntity<String> {
        val userId = jwtUtil.extractUserId(token.removePrefix("Bearer "))
        val purchaseMessage = PurchaseMessage(userId, purchaseRequest.productId, purchaseRequest.quantity)
        rabbitTemplate.convertAndSend(exchange, routingKey, purchaseMessage)
        return ResponseEntity.ok("Purchase request sent.")
    }
}

data class PurchaseRequest(val productId: String, val quantity: Int)