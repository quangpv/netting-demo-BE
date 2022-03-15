package com.onehypernet.demo.model.entity

import com.onehypernet.demo.model.enumerate.UserRole
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import javax.persistence.*

@Entity(name = "user")
class UserEntity(
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    var id: String = "",
    var email: String = "",
    var password: String = "",
    var role: UserRole = UserRole.None,

    @OneToOne(
        cascade = [CascadeType.ALL],
        fetch = FetchType.LAZY, optional = true,
        mappedBy = "user",
    )
    @PrimaryKeyJoinColumn
    var detail: UserDetailEntity? = null
)