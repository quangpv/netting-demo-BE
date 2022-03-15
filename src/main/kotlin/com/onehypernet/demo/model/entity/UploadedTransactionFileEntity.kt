package com.onehypernet.demo.model.entity

import java.io.Serializable
import java.time.LocalDateTime
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.IdClass

@Entity(name = "uploaded_transaction")
@IdClass(UploadedTransactionId::class)
class UploadedTransactionFileEntity(
    @Id
    @Column(name = "netting_id")
    var nettingId: String = "",

    @Id
    @Column(name = "user_id")
    var userId: String = "",

    @Column(name = "file_name_original")
    var fileName: String = "",

    @Column(name = "file_name_stored")
    var storedFileName: String = "",

    @Column(name = "create_at")
    var createAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "update_at")
    var updatedAt: LocalDateTime = LocalDateTime.now()
)

class UploadedTransactionId(
    var nettingId: String = "",
    var userId: String = "",
) : Serializable